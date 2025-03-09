package com.webstore.controllers.admin;

import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.services.shop.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WarehouseController {

    private final GoodsService service;

    @GetMapping(value = "${app.endpoints.warehouse.main}")
    public String warehousePage() {
        return "admin/warehouse/delivery-form";
    }

    @PostMapping(value = "${app.endpoints.warehouse.main}")
    public String deliveryHandling(Model model, @RequestParam Long id, @RequestParam int count) {
        try {
            service.handleDelivery(id, count);
        } catch (IllegalGoodsCountException e) {
            model.addAttribute(
                    "errorMessage",
                    "Нельзя поставить такое количество товара"
            );
        } catch (GoodNotFoundException e) {
            model.addAttribute(
                    "errorMessage",
                    "Товара с таким id ещё не зарегистрировано - обратитесь к модераторам"
            );
        }
        return "admin/warehouse/delivery-form";
    }
}
