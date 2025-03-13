package com.webstore.services.shop.customer;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import com.webstore.model.entities.Good;
import com.webstore.model.entities.Order;
import com.webstore.model.entities.User;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.cart.LockedCartException;
import com.webstore.exceptions.cart.payment.*;
import com.webstore.repositories.GoodsRepository;
import com.webstore.repositories.OrdersRepository;
import com.webstore.utils.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import static com.webstore.config.PayPalConfig.getAPIContext;
import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final GoodsRepository goodsRepository;
    private final OrdersRepository ordersRepository;

    private final Cart cart;

    public String getPaymentUrl(String cancelUrl, String successUrl) throws PaymentException {
        validateCart(cart.getCart());

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(
                cart.getCart().entrySet().stream()
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
        try {
            for (Links link : payment.create(getAPIContext()).getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return link.getHref();
                }
            }
            throw new DisapprovedPaymentException(cart);
        } catch (PayPalRESTException e) {
            throw new PayPalHandlingException(cart);
        }
    }

    @Transactional
    public void handleBuy(User user, String paymentId, String payerId) throws PaymentException {
        validateCart(cart.getCart());

        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        try {
            if (!payment.execute(getAPIContext(), paymentExecute).getState().equals("approved")) {
                throw new DisapprovedPaymentException(cart);
            }
        } catch (PayPalRESTException e) {
            throw new PayPalHandlingException(cart);
        }


        for (Entry<Good, Integer> entry : cart.getCart().entrySet()) {
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
                throw new IllegalGoodsCountException(cart);
            }
        }
    }

    public HashMap<Good, Integer> getCart() {
        return cart.getCart();
    }

    public void addGoodToCart(Long goodId) throws GoodNotFoundException, LockedCartException {
        cart.addGood(getGoodById(goodId));
    }

    public void setGoodCountInCart(Long goodId, int quantity) throws GoodNotFoundException, LockedCartException {
        cart.setGoodCount(getGoodById(goodId), quantity);
    }

    public void removeFromCart(Long goodId) throws GoodNotFoundException, LockedCartException {
        cart.removeGood(getGoodById(goodId));
    }

    private Good getGoodById(Long id) throws GoodNotFoundException {
        Optional<Good> response = goodsRepository.findById(id);
        if (response.isEmpty()) {
            throw new GoodNotFoundException();
        }

        return response.get();
    }

    public void lockCart() {
        cart.lock();
    }

    public void unlockCart() {
        cart.unlock();
    }

    public void refreshCart() {
        cart.refresh();
    }
}
