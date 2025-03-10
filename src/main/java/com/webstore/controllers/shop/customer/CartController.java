package com.webstore.controllers.shop.customer;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.webstore.entities.Good;
import com.webstore.exceptions.DisapprovedPaymentException;
import com.webstore.services.shop.customer.PaymentService;
import com.webstore.utils.Cart;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.IllegalCartConditionException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.services.shop.customer.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.webstore.security.MyUserDetailsService.userFromContext;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final static String ERROR_PARAM_NAME = "error",
            GOOD_COUNT_ERROR_CODE = "good_count",
            PAYPAL_ERROR_CODE = "paypal",
            CANCELED_PAYMENT_ERROR_CODE = "canceled_payment",
            PAYMENT_ERROR_CODE = "payment";

    @Value("${app.endpoints.cart.main}")
    String rootUrl;
    @Value("${app.endpoints.catalog.main}")
    String catalogUrl;

    @Value("${app.host}")
    String hostUrl;

    @Value("${app.endpoints.cart.canceled_payment}")
    String canceledPaymentUrl;
    @Value("${app.endpoints.cart.successful_payment}")
    String successfulPaymentUrl;

    private final CartService service;
    private final PaymentService paymentService;

    private final Cart cart;

    @GetMapping(value = "${app.endpoints.cart.main}")
    public String cartPage(
            Model model,
            @RequestParam(value = ERROR_PARAM_NAME, required = false) String error
    ) throws IllegalCartConditionException {
        if (error != null) {
            switch (error) {
                case GOOD_COUNT_ERROR_CODE -> model.addAttribute(
                        "errorMessage",
                        "Товаров нет в нужном количестве"
                );
                case PAYPAL_ERROR_CODE -> model.addAttribute(
                        "errorMessage",
                        "Ошибка при обработке оплаты (PayPal)."
                );
                case CANCELED_PAYMENT_ERROR_CODE -> model.addAttribute(
                        "errorMessage",
                        "Оплата отменена."
                );
                case PAYMENT_ERROR_CODE -> model.addAttribute(
                        "errorMessage",
                        "Ошибка при оплате"
                );
            }
        }
        model.addAttribute("cart", service.getGoodsCart(cart));
        return "shop/customer/cart";
    }

    @PostMapping(value = "${app.endpoints.cart.main}")
    public String addGoodToCart(@RequestParam Long id) throws GoodNotFoundException {
        cart.addGoodToCart(service.getGoodById(id).getId());
        return "redirect:" + catalogUrl;
    }

    @PutMapping(value = "${app.endpoints.cart.main}")
    public String editGoodCountInCart(@RequestParam Long id, @RequestParam int quantity) {
        cart.setGoodCountInCart(id, quantity);
        return "redirect:" + rootUrl;
    }

    @DeleteMapping(value = "${app.endpoints.cart.main}")
    public String deleteGoodFromCart(@RequestParam Long id) {
        cart.removeFromCart(id);
        return "redirect:" + rootUrl;
    }

    @PostMapping(value = "${app.endpoints.cart.validating}")
    public RedirectView checkout() throws IllegalCartConditionException {
        HashMap<Good, Integer> goodsCart = service.getGoodsCart(cart);
        try {
            service.validateCart(goodsCart);
        } catch (IllegalGoodsCountException e) {
            return new RedirectView(hostUrl + rootUrl + errorParam(GOOD_COUNT_ERROR_CODE));
        }

        try {
            Payment payment = paymentService.createPayment(
                    goodsCart.entrySet().stream()
                            .map(entry -> BigDecimal.valueOf((long) entry.getValue() * entry.getKey().getPrice()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add),
                    "USD",
                    "paypal",
                    "sale",
                    "Оплата заказа",
                    hostUrl + canceledPaymentUrl,
                    hostUrl + successfulPaymentUrl
            );
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return new RedirectView(link.getHref());
                }
            }
            return new RedirectView(hostUrl + rootUrl + errorParam(PAYPAL_ERROR_CODE));
        } catch (PayPalRESTException e) {
            return new RedirectView(hostUrl + rootUrl + errorParam(PAYPAL_ERROR_CODE));
        }
    }

    @GetMapping(value = "${app.endpoints.cart.successful_payment}")
    public String successfulPayment(
            @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId
    ) throws IllegalCartConditionException {
        try {
            service.handleBuy(cart, userFromContext(), paymentService.executePayment(paymentId, payerId));
            cart.cleanCart();
            return "redirect:" + rootUrl;
        } catch (PayPalRESTException e) {
            return "redirect:" + rootUrl + errorParam(PAYPAL_ERROR_CODE);
        } catch (IllegalGoodsCountException e) {
            return "redirect:" + rootUrl + errorParam(GOOD_COUNT_ERROR_CODE);
        } catch (DisapprovedPaymentException e) {
            return "redirect:" + rootUrl + errorParam(PAYMENT_ERROR_CODE);
        }
    }

    @GetMapping(value = "${app.endpoints.cart.canceled_payment}")
    public String canceledPayment() {
        return "redirect:" + rootUrl + errorParam(CANCELED_PAYMENT_ERROR_CODE);
    }

    private String errorParam(String errorCode) {
        return "?" + ERROR_PARAM_NAME + "=" + errorCode;
    }
}
