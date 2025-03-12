package com.webstore.controllers.shop.customer;

import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.cart.LockedCartException;
import com.webstore.exceptions.cart.payment.PaymentException;
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

    @Value("${app.endpoints.cart.main}")
    String rootUrl;
    @Value("${app.endpoints.catalog.main}")
    String catalogUrl;

    @Value("${app.host}")
    String hostUrl;

    @Value("${app.endpoints.cart.canceled_payment}")
    String canceledPaymentUrl;
    @Value("${app.endpoints.cart.finished_payment}")
    String finishedPaymentUrl;

    private final CartService service;

    @GetMapping(value = "${app.endpoints.cart.main}")
    public String cartPage(Model model) {
        return filledCartPage(model);
    }

    @PostMapping(value = "${app.endpoints.cart.main}")
    public RedirectView addGoodToCart(@RequestParam Long id) throws GoodNotFoundException, LockedCartException { //
        service.addGoodToCart(id);
        return new RedirectView(catalogUrl);
    }

    @PutMapping(value = "${app.endpoints.cart.main}")
    public RedirectView editGoodCountInCart(
            @RequestParam Long id,
            @RequestParam int quantity
    ) throws GoodNotFoundException, LockedCartException { //
        service.setGoodCountInCart(id, quantity);
        return new RedirectView(rootUrl);
    }

    @DeleteMapping(value = "${app.endpoints.cart.main}")
    public RedirectView deleteGoodFromCart(@RequestParam Long id) throws GoodNotFoundException, LockedCartException { //
        service.removeFromCart(id);
        return new RedirectView(rootUrl);
    }

    @PostMapping(value = "${app.endpoints.cart.validating}")
    public RedirectView checkout() throws PaymentException {
        service.lockCart();
        return new RedirectView(service.getPaymentUrl(
                hostUrl + canceledPaymentUrl,
                hostUrl + finishedPaymentUrl
        ));
    }

    @GetMapping(value = "${app.endpoints.cart.finished_payment}")
    public String successfulPayment(
            Model model,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) throws PaymentException {
        service.handleBuy(userFromContext(), paymentId, payerId);
        service.refreshCart();

        return filledCartPage(model);
    }

    @GetMapping(value = "${app.endpoints.cart.canceled_payment}")
    public String canceledPayment(Model model) {
        service.unlockCart();

        model.addAttribute("errorMessage", "Оплата отменена");
        return filledCartPage(model);
    }

    private String filledCartPage(Model model) {
        model.addAttribute("cart", service.getCart());
        return "shop/customer/cart";
    }
}
