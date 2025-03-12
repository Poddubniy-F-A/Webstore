package com.webstore.exceptions.cart.payment;

import com.webstore.utils.Cart;

public class DisapprovedPaymentException extends PaymentException {
    public DisapprovedPaymentException(Cart cart) {
        super(cart, "Ошибка при оплате");
    }
}
