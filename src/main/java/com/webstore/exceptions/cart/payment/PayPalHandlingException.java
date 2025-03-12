package com.webstore.exceptions.cart.payment;

import com.webstore.utils.Cart;

public class PayPalHandlingException extends PaymentException {
    public PayPalHandlingException(Cart cart) {
        super(cart, "Ошибка при обработке оплаты (PayPal)");
    }
}
