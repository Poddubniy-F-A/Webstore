package com.webstore.controllers.shop.customer;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.webstore.entities.Good;
import com.webstore.services.shop.PaymentService;
import com.webstore.utils.Cart;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.IllegalCartConditionException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.services.shop.customer.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.webstore.security.MyUserDetailsService.userFromContext;

@Controller
@RequiredArgsConstructor
public class CartController {

    @Value("${app.endpoints.cart.main}")
    private String rootUrl;
    @Value("${app.endpoints.catalog.main}")
    private String catalogUrl;
    @Value("${app.endpoints.payment}")
    private String paymentUrl;

    private final CartService service;
    private final PaymentService payPalService;

    private final Cart cart;

    @GetMapping(value = "${app.endpoints.cart.main}")
    public String cartPage(Model model) throws IllegalCartConditionException {
        return filledCartPage(model);
    }

    @PostMapping(value = "${app.endpoints.cart.validating}")
    public String checkout(Model model) throws IllegalCartConditionException {
        HashMap<Good, Integer> goodsCart = service.getGoodsCart(cart);
        for (java.util.Map.Entry<Good, Integer> entry : goodsCart.entrySet()) {
            Good good = entry.getKey();
            int count = entry.getValue();
            if (count > good.getCount()) {
                model.addAttribute(
                        "errorMessage",
                        "Товара " + good.getLabel() + " нет в наличии в нужном количестве."
                );
                return filledCartPage(model);
            }
        }

        return "redirect:" + paymentUrl +
                "?amount=" + goodsCart.entrySet().stream()
                .map(entry -> BigDecimal.valueOf((long) entry.getValue() * entry.getKey().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add) +
                "&currency=USD";
    }

    // 2. Метод для обработки успешной оплаты (после перенаправления из PayPal)
    @GetMapping(value = "${app.endpoints.cart.successful_payment}")
    public String handleSuccessPayment(
            Model model,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) throws IllegalCartConditionException {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                service.handleBuy(cart, userFromContext());
                cart.cleanCart();
            } else {
                model.addAttribute("errorMessage", "Ошибка при оплате.");
            }
        } catch (PayPalRESTException e) {
            model.addAttribute("errorMessage", "Ошибка при обработке оплаты (PayPal).");
            e.printStackTrace(); // Логируйте ошибку
        } catch (IllegalCartConditionException | IllegalGoodsCountException e) {
            model.addAttribute("errorMessage", "Ошибка при обработке заказа.");
        }
        return filledCartPage(model); // Вернуться на страницу корзины
    }

    @GetMapping(value = "${app.endpoints.cart.canceled_payment}")
    public String cancelPay(Model model) throws IllegalCartConditionException {
        model.addAttribute("errorMessage", "Оплата отменена.");
        return filledCartPage(model);
    }

    private String filledCartPage(Model model) throws IllegalCartConditionException {
        model.addAttribute("cart", service.getGoodsCart(cart));
        return "shop/customer/cart";
    }

    @PostMapping(value = "${app.endpoints.cart.adding}/{id}")
    public String addGoodToCart(@PathVariable Long id) throws GoodNotFoundException {
        cart.addGoodToCart(service.getGoodById(id).getId());
        return "redirect:" + catalogUrl;
    }

    @PostMapping(value = "${app.endpoints.cart.editing}/{id}")
    public String editGoodCountInCart(@PathVariable Long id, @RequestParam int quantity) {
        cart.setGoodCountInCart(id, quantity);
        return "redirect:" + rootUrl;
    }

    @PostMapping(value = "${app.endpoints.cart.deleting}/{id}")
    public String deleteGoodFromCart(HttpSession ignoredSession, @PathVariable Long id) {
        cart.removeFromCart(id);
        return "redirect:" + rootUrl;
    }
}
