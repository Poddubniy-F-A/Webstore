package com.webstore.services.shop.customer;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import com.webstore.exceptions.DisapprovedPaymentException;
import com.webstore.utils.Cart;
import com.webstore.entities.Good;
import com.webstore.entities.Order;
import com.webstore.entities.User;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.InvalidCartConditionException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.repositories.GoodsRepository;
import com.webstore.repositories.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import static com.webstore.config.PayPalConfig.getAPIContext;
import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class CartService {
    private GoodsRepository goodsRepository;
    private OrdersRepository ordersRepository;

    public String getPaymentUrl(
            Cart cart, String cancelUrl, String successUrl
    ) throws InvalidCartConditionException, IllegalGoodsCountException, PayPalRESTException, DisapprovedPaymentException {
        HashMap<Good, Integer> goodsCart = getGoodsCart(cart);
        validateCart(goodsCart);

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(
                goodsCart.entrySet().stream()
                .map(entry -> BigDecimal.valueOf((long) entry.getValue() * entry.getKey().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .toString()
        );

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Transaction transaction = new Transaction();
        transaction.setDescription("Оплата заказа");
        transaction.setAmount(amount);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(singletonList(transaction));
        payment.setRedirectUrls(redirectUrls);
        for (Links link : payment.create(getAPIContext()).getLinks()) {
            if (link.getRel().equals("approval_url")) {
                return link.getHref();
            }
        }
        throw new DisapprovedPaymentException();
    }

    @Transactional
    public void handleBuy(
            Cart cart, User user, String payerId, String paymentId
    ) throws InvalidCartConditionException, IllegalGoodsCountException, PayPalRESTException, DisapprovedPaymentException {
        HashMap<Good, Integer> goodsCart = getGoodsCart(cart);
        validateCart(goodsCart);
        for (Entry<Good, Integer> entry : goodsCart.entrySet()) {
            Good good = entry.getKey();
            int count = entry.getValue();

            Order order = new Order();
            order.setGood(good);
            order.setCount(count);
            order.setUser(user);
            ordersRepository.save(order);

            good.setCount(good.getCount() - count);
            good.setOrdersCount(good.getOrdersCount() + 1);
            goodsRepository.save(good);
        }

        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        if (!payment.execute(getAPIContext(), paymentExecute).getState().equals("approved")) {
            throw new DisapprovedPaymentException();
        }
    }

    private void validateCart(HashMap<Good, Integer> goodsCart) throws IllegalGoodsCountException {
        for (Entry<Good, Integer> entry : goodsCart.entrySet()) {
            if (entry.getValue() > entry.getKey().getCount()) {
                throw new IllegalGoodsCountException();
            }
        }
    }

    public HashMap<Good, Integer> getGoodsCart(Cart cart) throws InvalidCartConditionException {
        HashMap<Good, Integer> result = new HashMap<>();
        for (Entry<Long, Integer> entry : cart.getCart().entrySet()) {
            try {
                result.put(getGoodById(entry.getKey()), entry.getValue());
            } catch (GoodNotFoundException e) {
                throw new InvalidCartConditionException();
            }
        }
        return result;
    }

    public Good getGoodById(Long id) throws GoodNotFoundException {
        Optional<Good> response = goodsRepository.findById(id);
        if (response.isEmpty()) {
            throw new GoodNotFoundException();
        }

        return response.get();
    }
}
