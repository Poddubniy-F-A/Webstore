package com.webstore.services.shop;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.webstore.utils.PayPalConfig.getAPIContext;
import static java.util.Collections.singletonList;

@Service
public class PaymentService {

    // Метод для создания платежа
    public Payment createPayment(
            BigDecimal total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total.toString());

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(singletonList(transaction));
        payment.setRedirectUrls(redirectUrls);
        return payment.create(getAPIContext());
    }

    // Метод для выполнения платежа (после перенаправления пользователя обратно в приложение)
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        return payment.execute(getAPIContext(), paymentExecute);
    }
}
