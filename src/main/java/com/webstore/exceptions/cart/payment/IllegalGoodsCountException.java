package com.webstore.exceptions.cart.payment;

import com.webstore.utils.Cart;

public class IllegalGoodsCountException extends PaymentException {
    public IllegalGoodsCountException(Cart cart) {
        super(cart, "Товаров нет в нужном количестве");
    }
}
