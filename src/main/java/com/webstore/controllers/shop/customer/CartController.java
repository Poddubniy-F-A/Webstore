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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.webstore.utils.security.MyUserDetailsService.userFromContext;

@Controller
@AllArgsConstructor
@RequestMapping("/customer/cart")
public class CartController {

    private CartService service;
    private PaymentService payPalService;

    private final Cart cart;

    @GetMapping
    public String cartPage(Model model) throws IllegalCartConditionException {
        return filledCartPage(model);
    }

    // 1. Метод для обработки нажатия на кнопку "Оформить заказ"
    @PostMapping("/checkout")
    public String checkout(Model model) throws IllegalCartConditionException {
        // 1.1. Валидация корзины
        HashMap<Good, Integer> goodsCart = service.getGoodsCart(cart); // Получаем товары из корзины

        // 1.2. Проверка наличия товаров на складе
        for (java.util.Map.Entry<Good, Integer> entry : goodsCart.entrySet()) {
            Good good = entry.getKey();
            int count = entry.getValue();
            if (count > good.getCount()) {
                model.addAttribute(
                        "errorMessage",
                        "Товара " + good.getLabel() + " нет в наличии в нужном количестве."
                );
                return filledCartPage(model); // Вернуться на страницу корзины с сообщением об ошибке
            }
        }

        // 1.3. Расчет общей суммы заказа
        BigDecimal totalAmount = goodsCart.entrySet().stream()
                .map(entry -> BigDecimal.valueOf((long) entry.getValue() * entry.getKey().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 1.4. Перенаправление на оплату через PayPal
        return "redirect:/pay?amount=" + totalAmount + "&currency=USD";
    }

    // 2. Метод для обработки успешной оплаты (после перенаправления из PayPal)
    @GetMapping("/success")
    public String handleSuccessPayment(
            Model model, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId
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

    @GetMapping("/cancel")
    public String cancelPay(Model model) throws IllegalCartConditionException {
        model.addAttribute("errorMessage", "Оплата отменена.");
        return filledCartPage(model);
    }

    private String filledCartPage(Model model) throws IllegalCartConditionException {
        model.addAttribute("cart", service.getGoodsCart(cart));
        return "shop/customer/cart";
    }

    @GetMapping("/add/{id}")
    public String addGoodToCart(@PathVariable Long id) throws GoodNotFoundException {
        cart.addGoodToCart(service.getGoodById(id).getId());
        return "redirect:/catalog/" + id;
    }

    @PostMapping("/edit/{id}")
    public String editGoodCountInCart(@PathVariable Long id, @RequestParam int quantity) {
        cart.setGoodCountInCart(id, quantity);
        return "redirect:/customer/cart";
    }

    @PostMapping("/delete/{id}")
    public String deleteGoodFromCart(HttpSession ignoredSession, @PathVariable Long id) {
        cart.removeFromCart(id);
        return "redirect:/customer/cart";
    }
}
