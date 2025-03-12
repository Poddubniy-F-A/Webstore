package com.webstore.controllers.admin;

import com.webstore.exceptions.warehouse.IllegalGoodIdException;
import com.webstore.exceptions.warehouse.IllegalGoodQuantityException;
import com.webstore.services.admin.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @GetMapping(value = "${app.endpoints.warehouse.main}")
    public String warehousePage() {
        return "admin/warehouse/delivery-form";
    }

    @PostMapping(value = "${app.endpoints.warehouse.main}")
    public String deliveryHandling(
            @RequestParam Long id,
            @RequestParam int count
    ) throws IllegalGoodQuantityException, IllegalGoodIdException {
        service.handleDelivery(id, count);
        return "admin/warehouse/delivery-form";
    }
}
