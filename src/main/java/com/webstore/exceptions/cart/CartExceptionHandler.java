package com.webstore.exceptions.cart;

import com.webstore.exceptions.cart.payment.PaymentException;
import com.webstore.utils.EndpointsURLs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.webstore.controllers.shop.customer.CartController.ERROR_MESSAGE_ATTRIBUTE_NAME;

@ControllerAdvice
@RequiredArgsConstructor
public class CartExceptionHandler {

    private final EndpointsURLs endpointsURLs;

    @ExceptionHandler(LockedCartException.class)
    public RedirectView handleLockedCart(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Дождитесь окончания оплаты");
        return new RedirectView(endpointsURLs.CART_MAIN);
    }

    @ExceptionHandler(PaymentException.class)
    public RedirectView handleIncorrectPayment(PaymentException e, RedirectAttributes redirectAttributes) {
        e.getCart().unlock();

        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, e.getMessage());
        return new RedirectView(endpointsURLs.CART_MAIN);
    }
}
