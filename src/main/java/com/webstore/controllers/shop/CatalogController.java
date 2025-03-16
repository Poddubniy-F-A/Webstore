package com.webstore.controllers.shop;

import com.webstore.model.entities.Good;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.services.shop.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService service;

    @GetMapping(value = "${app.endpoints.catalog.main}")
    public String catalogPage(
            Model model,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Boolean inStock
    ) {
        List<Good> allGoods = service.getAllGoods().stream()
                .filter(good ->
                        (brand == null || brand.equals("all") || good.getBrand().equals(brand)) &&
                                (category == null || category.equals("all") || good.getCategory().equals(category)) &&
                                (maxPrice == null || good.getPrice() <= maxPrice) &&
                                (minRating == null || good.getRating() >= minRating) &&
                                (inStock == null || good.getCount() > 0))
                .sorted((o1, o2) -> {
                    if (sort != null) {
                        switch (sort) {
                            case ("priceAsc") -> {
                                return Integer.compare(o1.getPrice(), o2.getPrice());
                            }
                            case ("rateDesc") -> {
                                return Double.compare(o2.getRating(), o1.getRating());
                            }
                        }
                    }
                    return Integer.compare(o2.getOrdersCount(), o1.getOrdersCount());
                }).collect(Collectors.toList());

        model.addAttribute("goods", allGoods);
        model.addAttribute("brands", allGoods.stream().map(Good::getBrand).collect(Collectors.toSet()));
        model.addAttribute(
                "categories",
                allGoods.stream().map(Good::getCategory).collect(Collectors.toSet())
        );

        model.addAttribute("defaultSort", sort == null ? "ordersCount" : sort);

        model.addAttribute("defaultBrand", brand == null ? "all" : brand);
        model.addAttribute("defaultCategory", category == null ? "all" : category);
        model.addAttribute(
                "defaultMaxPrice",
                maxPrice == null ? allGoods.stream().mapToInt(Good::getPrice).max().orElse(0) : maxPrice
        );
        model.addAttribute("defaultMinRate", minRating == null ? 0 : minRating);
        model.addAttribute("defaultInStock", inStock != null);

        return "shop/catalog/catalog";
    }

    @GetMapping(value = "${app.endpoints.catalog.main}/{id}")
    public String goodPage(Model model, @PathVariable Long id) throws GoodNotFoundException {
        //atomically
        Good good = service.getGoodById(id);
        model.addAttribute("good", good);
        model.addAttribute("feedbacks", service.getFeedbacksAbout(id));
        //

        return "shop/catalog/good-page";
    }
}
