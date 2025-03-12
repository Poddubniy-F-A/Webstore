package com.webstore.exceptions.cart;

import com.webstore.utils.Cart;
import lombok.Getter;

@Getter
public class LockedCartException extends Exception {
    private final Cart cart;

    public LockedCartException(Cart cart) {
        this.cart = cart;
    }
}
