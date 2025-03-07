package com.webstore.controllers.shop.customer;

import com.webstore.entities.Feedback;
import com.webstore.entities.Good;
import com.webstore.exceptions.DuplicateFeedbackException;
import com.webstore.exceptions.FeedbackNotFoundException;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.NoFeedbacksException;
import com.webstore.services.shop.customer.FeedbacksService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.webstore.utils.security.MyUserDetailsService.userFromContext;

@Controller
@AllArgsConstructor
@RequestMapping("/customer/feedbacks")
public class FeedbacksController {
    private FeedbacksService feedbacksService;

    @GetMapping
    public String feedbackPage(Model model) throws DuplicateFeedbackException {
        //transaction
        HashMap<Good, Feedback> goodsWithFeedbacks = feedbacksService.getGoodsWithFeedbacksBy(userFromContext());
        model.addAttribute(
                "rated_goods",
                goodsWithFeedbacks.entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        model.addAttribute(
                "unrated_goods",
                goodsWithFeedbacks.keySet().stream()
                        .filter(good -> goodsWithFeedbacks.get(good) == null)
                        .collect(Collectors.toSet())
        );
        //
        return "shop/customer/feedbacks/main";
    }

    @GetMapping("/create/{id}")
    public String createFeedbackPage(Model model, @PathVariable Long id) throws GoodNotFoundException {
        model.addAttribute("good", feedbacksService.getGoodById(id));
        return "shop/customer/feedbacks/creating-form";
    }

    @PostMapping("/create/{id}")
    public String createFeedback(
            @PathVariable Long id,
            @RequestParam String review, @RequestParam int stars
    ) throws GoodNotFoundException, NoFeedbacksException {
        feedbacksService.handleFeedbackCreating(userFromContext(), id, review, stars);
        return "redirect:/customer/feedbacks";
    }

    @GetMapping("/edit/{id}")
    public String editFeedbackPage(
            Model model, @PathVariable Long id
    ) throws GoodNotFoundException, FeedbackNotFoundException, DuplicateFeedbackException {
        //transaction
        Good good = feedbacksService.getGoodById(id);
        model.addAttribute("good", good);
        model.addAttribute("feedback", feedbacksService.getFeedbackAboutBy(good, userFromContext()));
        //
        return "shop/customer/feedbacks/editing-form";
    }

    @PostMapping("/edit/{id}")
    public String editFeedback(
            @PathVariable Long id,
            @RequestParam String review, @RequestParam int stars
    ) throws GoodNotFoundException, FeedbackNotFoundException, DuplicateFeedbackException, NoFeedbacksException {
        feedbacksService.handleFeedbackEditing(userFromContext(), id, review, stars);
        return "redirect:/customer/feedbacks";
    }
}
