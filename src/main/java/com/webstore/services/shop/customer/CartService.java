package com.webstore.services.shop.customer;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import com.webstore.exceptions.DisapprovedPaymentException;
import com.webstore.entities.Good;
import com.webstore.entities.Order;
import com.webstore.entities.User;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.repositories.GoodsRepository;
import com.webstore.repositories.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            HashMap<Good, Integer> cart, String cancelUrl, String successUrl
    ) throws IllegalGoodsCountException, PayPalRESTException, DisapprovedPaymentException {
        validateCart(cart);

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(
                cart.entrySet().stream()
                .map(entry -> entry.getValue() * entry.getKey().getPrice())
                .reduce(0, Integer::sum)
        ));

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
            HashMap<Good, Integer> cart, User user, String paymentId, String payerId
    ) throws IllegalGoodsCountException, PayPalRESTException, DisapprovedPaymentException {
        validateCart(cart);

        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        if (!payment.execute(getAPIContext(), paymentExecute).getState().equals("approved")) {
            throw new DisapprovedPaymentException();
        }

        for (Entry<Good, Integer> entry : cart.entrySet()) {
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
    }

    private void validateCart(HashMap<Good, Integer> goodsCart) throws IllegalGoodsCountException {
        for (Entry<Good, Integer> entry : goodsCart.entrySet()) {
            if (entry.getValue() > entry.getKey().getCount()) {
                throw new IllegalGoodsCountException();
            }
        }
    }

    public Good getGoodById(Long id) throws GoodNotFoundException {
        Optional<Good> response = goodsRepository.findById(id);
        if (response.isEmpty()) {
            throw new GoodNotFoundException();
        }

        return response.get();
    }
}
