package com.webstore.controllers.shop;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.webstore.services.shop.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    @Value("${app.host}")
    String hostUrl;

    @Value("${app.endpoints.cart.canceled_payment}")
    String canceledPaymentUrl;
    @Value("${app.endpoints.cart.successful_payment}")
    String successfulPaymentUrl;
    @Value("${app.endpoints.cart.main}")
    String cartUrl;

    private final PaymentService payPalService;

    @GetMapping(value = "${app.endpoints.payment}")
    public RedirectView pay(@RequestParam BigDecimal amount, @RequestParam String currency) {
        try {
            // Создаем платеж с помощью PayPalService
            Payment payment = payPalService.createPayment(
                    amount,
                    currency,
                    "paypal",
                    "sale",
                    "Оплата заказа",
                    hostUrl + canceledPaymentUrl,
                    hostUrl + successfulPaymentUrl
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
        return new RedirectView(hostUrl + cartUrl);
    }
}
