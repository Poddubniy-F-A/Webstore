package com.webstore.services.shop.customer;

import com.paypal.api.payments.Payment;
import com.webstore.exceptions.DisapprovedPaymentException;
import com.webstore.utils.Cart;
import com.webstore.entities.Good;
import com.webstore.entities.Order;
import com.webstore.entities.User;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.IllegalCartConditionException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.repositories.GoodsRepository;
import com.webstore.repositories.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {
    private GoodsRepository goodsRepository;
    private OrdersRepository ordersRepository;

    @Transactional
    public void handleBuy(
            Cart cart, User user, Payment payment
    ) throws IllegalCartConditionException, IllegalGoodsCountException, DisapprovedPaymentException {
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

        if (!payment.getState().equals("approved")) {
            throw new DisapprovedPaymentException();
        }
    }

    public void validateCart(HashMap<Good, Integer> goodsCart) throws IllegalGoodsCountException {
        for (Entry<Good, Integer> entry : goodsCart.entrySet()) {
            if (entry.getValue() > entry.getKey().getCount()) {
                throw new IllegalGoodsCountException();
            }
        }
    }

    public HashMap<Good, Integer> getGoodsCart(Cart cart) throws IllegalCartConditionException {
        HashMap<Good, Integer> result = new HashMap<>();
        for (Entry<Long, Integer> entry : cart.getCart().entrySet()) {
            try {
                result.put(getGoodById(entry.getKey()), entry.getValue());
            } catch (GoodNotFoundException e) {
                throw new IllegalCartConditionException();
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
