package com.webstore.controllers.admin;

import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.services.shop.GoodsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("/warehouse")
public class WarehouseController {
    private GoodsService service;

    @GetMapping
    public String warehousePage(Model model) {
        return "admin/warehouse/delivery-form";
    }

    @PostMapping
    public String deliveryHandling(Model model, @RequestParam Long id, @RequestParam int count) {
        try {
            service.handleDelivery(id, count);
        } catch (IllegalGoodsCountException e) {
            model.addAttribute(
                    "error_message",
                    "Нельзя поставить такое количество товара"
            );
        } catch (GoodNotFoundException e) {
            model.addAttribute(
                    "error_message",
                    "Товара с таким id ещё не зарегистрировано - обратитесь к модераторам"
            );
        }
        return "admin/warehouse/delivery-form";
    }
}
