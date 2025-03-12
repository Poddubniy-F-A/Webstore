package com.webstore.exceptions.cart.payment;

import com.webstore.utils.Cart;
import lombok.Getter;

@Getter
public abstract class PaymentException extends Exception {

    private final Cart cart;
    private final String message;

    public PaymentException(Cart cart, String message) {
        this.cart = cart;
        this.message = message;
    }
}
