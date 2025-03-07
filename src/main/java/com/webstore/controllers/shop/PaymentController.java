package com.webstore.controllers.shop;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.webstore.services.shop.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;

@Controller
@AllArgsConstructor
@RequestMapping("/pay")
public class PaymentController {

    private final PaymentService payPalService;

    @GetMapping
    public RedirectView pay(
            @RequestParam BigDecimal amount,
            @RequestParam String currency
    ) {
        try {
            // Создаем платеж с помощью PayPalService
            Payment payment = payPalService.createPayment(
                    amount,
                    currency,
                    "paypal",
                    "sale",
                    "Оплата заказа",
                    "http://localhost:8080/customer/cart/cancel",
                    "http://localhost:8080/customer/cart/success"
            );

            // Получаем URL для перенаправления пользователя на PayPal
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return new RedirectView(link.getHref()); // Перенаправляем на PayPal
                }
            }
        } catch (PayPalRESTException e) {
            // Обработка ошибки
            e.printStackTrace();
        }

        // В случае ошибки перенаправляем на главную страницу
        return new RedirectView("http://localhost:8080/customer/cart");
    }
}
