package com.webstore.exceptions.cart;

import com.webstore.exceptions.cart.payment.PaymentException;
import com.webstore.utils.Cart;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CartExceptionHandler {

    @ExceptionHandler(LockedCartException.class)
    public ModelAndView handleLockedCart(LockedCartException e) {
        ModelAndView modelAndView = new ModelAndView("shop/customer/cart");
        modelAndView.getModel().put("errorMessage", "Дождитесь окончания оплаты");
        modelAndView.getModel().put("cart", e.getCart().getCart());
        return modelAndView;
    }

    @ExceptionHandler(PaymentException.class)
    public ModelAndView handlePayment(PaymentException e) {
        Cart cart = e.getCart();
        cart.unlock();

        ModelAndView modelAndView = new ModelAndView("shop/customer/cart");
        modelAndView.getModel().put("errorMessage", e.getMessage());
        modelAndView.getModel().put("cart", cart.getCart());
        return modelAndView;
    }
}
