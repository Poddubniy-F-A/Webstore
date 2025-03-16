package com.webstore.utils;

import com.webstore.model.entities.Good;
import com.webstore.exceptions.cart.LockedCartException;
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

    public void addGood(Good good) throws LockedCartException {
        check();
        cart.put(good, 1);
    }

    public void removeGood(Good good) throws LockedCartException {
        check();
        cart.remove(good);
    }

    public void setGoodCount(Good good, int quantity) throws LockedCartException {
        check();
        cart.put(good, quantity);
    }

    private void check() throws LockedCartException {
        if (isLocked) {
            throw new LockedCartException();
        }
    }

    public void refresh() {
        isLocked = false;
        cart.clear();
    }
}
