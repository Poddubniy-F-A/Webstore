package com.webstore.controllers.shop.customer;

import com.paypal.base.rest.PayPalRESTException;
import com.webstore.exceptions.DisapprovedPaymentException;
import com.webstore.utils.Cart;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.InvalidCartConditionException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.services.shop.customer.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static com.webstore.security.MyUserDetailsService.userFromContext;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final static String ERROR_PARAM_NAME = "error",
            GOOD_COUNT_ERROR_CODE = "good_count",
            PAYPAL_ERROR_CODE = "paypal",
            CANCELED_PAYMENT_ERROR_CODE = "canceled_payment",
            DISAPPROVED_PAYMENT_ERROR_CODE = "payment";

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

    private final Cart cart;

    @GetMapping(value = "${app.endpoints.cart.main}")
    public String cartPage(
            Model model, @RequestParam(value = ERROR_PARAM_NAME, required = false) String error
    ) throws InvalidCartConditionException {
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
                case DISAPPROVED_PAYMENT_ERROR_CODE -> model.addAttribute(
                        "errorMessage",
                        "Ошибка при оплате"
                );
                case CANCELED_PAYMENT_ERROR_CODE -> model.addAttribute(
                        "errorMessage",
                        "Оплата отменена."
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
    public RedirectView checkout() throws InvalidCartConditionException {
        try {
            return new RedirectView(service.getPaymentUrl(
                    cart,
                    hostUrl + canceledPaymentUrl,
                    hostUrl + successfulPaymentUrl
            ));
        } catch (IllegalGoodsCountException e) {
            return new RedirectView(hostUrl + rootUrl + errorParam(GOOD_COUNT_ERROR_CODE));
        } catch (PayPalRESTException e) {
            return new RedirectView(hostUrl + rootUrl + errorParam(PAYPAL_ERROR_CODE));
        } catch (DisapprovedPaymentException e) {
            return new RedirectView(hostUrl + rootUrl + errorParam(DISAPPROVED_PAYMENT_ERROR_CODE));
        }
    }

    @GetMapping(value = "${app.endpoints.cart.successful_payment}")
    public String successfulPayment(
            @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId
    ) throws InvalidCartConditionException {
        try {
            service.handleBuy(cart, userFromContext(), paymentId, payerId);
            cart.cleanCart();
            return "redirect:" + rootUrl;
        } catch (IllegalGoodsCountException e) {
            return "redirect:" + rootUrl + errorParam(GOOD_COUNT_ERROR_CODE);
        } catch (PayPalRESTException e) {
            return "redirect:" + rootUrl + errorParam(PAYPAL_ERROR_CODE);
        } catch (DisapprovedPaymentException e) {
            return "redirect:" + rootUrl + errorParam(DISAPPROVED_PAYMENT_ERROR_CODE);
        }
    }

    @GetMapping(value = "${app.endpoints.cart.canceled_payment}")
    public String canceledPayment() {
        return "redirect:" + rootUrl + errorParam(CANCELED_PAYMENT_ERROR_CODE);
    }

    private static String errorParam(String errorCode) {
        return "?" + ERROR_PARAM_NAME + "=" + errorCode;
    }
}
