package com.webstore.controllers.admin;

import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.services.shop.GoodsService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/management")
public class ManagementController {
    private GoodsService service;

    @GetMapping
    public String managementPage(Model model) {
        model.addAttribute("goods", service.getAllGoods());
        return "admin/management/main";
    }

    @GetMapping("/add")
    public String addGoodPage(Model model) {
        return "admin/management/creating-form";
    }

    @PostMapping("/add")
    public String addGood(
            HttpSession session,
            @RequestParam("pp") String picturePath,
            @RequestParam String label,
            @RequestParam String description,
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam int price
    ) {
        service.addGood(picturePath, label, description, brand, category, price);
        return "redirect:/management";
    }

    @GetMapping("/edit/{id}")
    public String editGoodPage(Model model, @PathVariable Long id) throws GoodNotFoundException {
        model.addAttribute("good", service.getGoodById(id));
        return "admin/management/editing-form";
    }

    @PostMapping("/edit/{id}")
    public String editGood(
            @RequestParam Long id,
            @RequestParam("pp") String picturePath,
            @RequestParam String label,
            @RequestParam String description,
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam int price
    ) throws GoodNotFoundException {
        service.updateGood(id, picturePath, label, description, brand, category, price);
        return "redirect:/management";
    }
}
