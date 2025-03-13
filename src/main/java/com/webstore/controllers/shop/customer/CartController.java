package com.webstore.controllers.shop.customer;

import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.cart.LockedCartException;
import com.webstore.exceptions.cart.payment.PaymentException;
import com.webstore.services.shop.customer.CartService;
import com.webstore.utils.EndpointsURLs;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.webstore.security.MyUserDetailsService.userFromContext;

@Controller
@RequiredArgsConstructor
public class CartController {

    public static final String
            WAS_PURCHASE_ATTRIBUTE_NAME = "wasPurchase",
            ERROR_MESSAGE_ATTRIBUTE_NAME = "errorMessage";

    private final EndpointsURLs endpointsURLs;
    private final CartService service;

    @GetMapping(value = "${app.endpoints.cart.main}")
    public String cartPage(
            Model model,
            @ModelAttribute(WAS_PURCHASE_ATTRIBUTE_NAME) String wasPurchase,
            @ModelAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME) String errorMessage
    ) {
        model.addAttribute("cart", service.getCart());
        return "shop/customer/cart";
    }

    @PostMapping(value = "${app.endpoints.cart.main}")
    public RedirectView addGoodToCart(@RequestParam Long id) throws GoodNotFoundException, LockedCartException { //
        service.addGoodToCart(id);
        return new RedirectView(endpointsURLs.CATALOG_MAIN);
    }

    @PutMapping(value = "${app.endpoints.cart.main}")
    public RedirectView editGoodCountInCart(
            @RequestParam Long id,
            @RequestParam int quantity
    ) throws GoodNotFoundException, LockedCartException { //
        service.setGoodCountInCart(id, quantity);
        return new RedirectView(endpointsURLs.CART_MAIN);
    }

    @DeleteMapping(value = "${app.endpoints.cart.main}")
    public RedirectView deleteGoodFromCart(@RequestParam Long id) throws GoodNotFoundException, LockedCartException { //
        service.removeFromCart(id);
        return new RedirectView(endpointsURLs.CART_MAIN);
    }

    @PostMapping(value = "${app.endpoints.cart.validating}")
    public RedirectView checkout(HttpServletRequest request) throws PaymentException {
        service.lockCart();

        String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return new RedirectView(service.getPaymentUrl(
                host + endpointsURLs.CART_CANCELED_PAYMENT,
                host + endpointsURLs.CART_FINISHED_PAYMENT
        ));
    }

    @GetMapping(value = "${app.endpoints.cart.finished_payment}")
    public RedirectView successfulPayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            RedirectAttributes redirectAttributes
    ) throws PaymentException {
        service.handleBuy(userFromContext(), paymentId, payerId);
        service.refreshCart();

        redirectAttributes.addFlashAttribute(WAS_PURCHASE_ATTRIBUTE_NAME, "true");
        return new RedirectView(endpointsURLs.CART_MAIN);
    }

    @GetMapping(value = "${app.endpoints.cart.canceled_payment}")
    public RedirectView canceledPayment(RedirectAttributes redirectAttributes) {
        service.unlockCart();

        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Оплата отменена");
        return new RedirectView(endpointsURLs.CART_MAIN);
    }
}
