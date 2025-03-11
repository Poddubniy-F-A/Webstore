package com.webstore.utils;

import com.webstore.entities.Good;
import com.webstore.exceptions.LockedCartException;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;

@Component
@SessionScope
public class Cart {

    private boolean isLocked = false;
    @Getter
    private final HashMap<Good, Integer> cart = new HashMap<>();

    public void lock() {
        isLocked = true;
    }

    public void unlock() {
        isLocked = false;
    }

    public void addGoodToCart(Good good) throws LockedCartException {
        check();
        cart.put(good, 1);
    }

    public void removeFromCart(Good good) throws LockedCartException {
        check();
        cart.remove(good);
    }

    public void setGoodCountInCart(Good good, int quantity) throws LockedCartException {
        check();
        cart.put(good, quantity);
    }

    public void cleanCart() throws LockedCartException {
        check();
        cart.clear();
    }

    private void check() throws LockedCartException {
        if (isLocked) {
            throw new LockedCartException();
        }
    }
}
