package com.webstore.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;

@Getter
@Component
@SessionScope
public class Cart {
    private final HashMap<Long, Integer> cart = new HashMap<>();

    public void addGoodToCart(Long goodId) {
        cart.put(goodId, 1);
    }

    public void removeFromCart(Long goodId) {
        for (Long id : cart.keySet()) {
            if (id.equals(goodId)) {
                cart.remove(id);
                break;
            }
        }
    }

    public void cleanCart() {
        cart.clear();
    }

    public void setGoodCountInCart(Long goodId, int quantity) {
        for (Long id : cart.keySet()) {
            if (id.equals(goodId)) {
                cart.put(id, quantity);
                break;
            }
        }
    }
}
