package com.webstore.controllers.admin;

import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.services.admin.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class ManagementController {

    @Value("${app.endpoints.management.main}")
    String rootUrl;

    private final ManagementService service;

    @GetMapping(value = "${app.endpoints.management.main}")
    public String managementPage(Model model) {
        model.addAttribute("goods", service.getAllGoods());
        return "admin/management/main";
    }

    @GetMapping(value = "${app.endpoints.management.creating}")
    public String addGoodPage() {
        return "admin/management/creating-form";
    }

    @PostMapping(value = "${app.endpoints.management.creating}")
    public RedirectView addGood(
            @RequestParam("pp") String picturePath,
            @RequestParam String label,
            @RequestParam String description,
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam int price
    ) {
        service.addGood(picturePath, label, description, brand, category, price);
        return new RedirectView(rootUrl);
    }

    @GetMapping(value = "${app.endpoints.management.editing}/{id}")
    public String editGoodPage(Model model, @PathVariable Long id) throws GoodNotFoundException {
        model.addAttribute("good", service.getGoodById(id));
        return "admin/management/editing-form";
    }

    @PutMapping(value = "${app.endpoints.management.editing}/{id}") //
    public RedirectView editGood(
            @RequestParam Long id,
            @RequestParam("pp") String picturePath,
            @RequestParam String label,
            @RequestParam String description,
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam int price
    ) throws GoodNotFoundException {
        service.updateGood(id, picturePath, label, description, brand, category, price);
        return new RedirectView(rootUrl);
    }
}
