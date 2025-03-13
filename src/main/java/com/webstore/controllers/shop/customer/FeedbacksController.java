package com.webstore.controllers.shop.customer;

import com.webstore.model.entities.Good;
import com.webstore.exceptions.feedbacks.FeedbackNotFoundException;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.feedbacks.IllegalRatingTryException;
import com.webstore.services.shop.customer.FeedbacksService;
import com.webstore.utils.EndpointsURLs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static com.webstore.security.MyUserDetailsService.userFromContext;

@Controller
@RequiredArgsConstructor
public class FeedbacksController {

    private final EndpointsURLs endpointsURLs;
    private final FeedbacksService service;

    @GetMapping(value = "${app.endpoints.feedbacks.main}")
    public String feedbackPage(Model model) {
        model.addAttribute("feedbackByGood", service.getGoodsWithFeedbacksBy(userFromContext()));
        return "shop/customer/feedbacks/main";
    }

    @GetMapping(value = "${app.endpoints.feedbacks.creating}/{id}")
    public String feedbackCreatingPage(Model model, @PathVariable Long id) throws GoodNotFoundException {
        model.addAttribute("good", service.getGoodById(id));
        return "shop/customer/feedbacks/creating-form";
    }

    @PostMapping(value = "${app.endpoints.feedbacks.creating}/{id}") //
    public RedirectView createFeedback(
            @PathVariable Long id,
            @RequestParam String review,
            @RequestParam int stars
    ) throws GoodNotFoundException, IllegalRatingTryException {
        service.handleFeedbackCreating(userFromContext(), id, review, stars);
        return new RedirectView(endpointsURLs.FEEDBACKS_MAIN);
    }

    @GetMapping(value = "${app.endpoints.feedbacks.editing}/{id}")
    public String feedbackEditingPage(
            Model model,
            @PathVariable Long id
    ) throws GoodNotFoundException, FeedbackNotFoundException {
        //atomically
        Good good = service.getGoodById(id);
        model.addAttribute("good", good);
        model.addAttribute("feedback", service.getFeedbackAboutBy(good, userFromContext()));
        //
        return "shop/customer/feedbacks/editing-form";
    }

    @PutMapping(value = "${app.endpoints.feedbacks.editing}/{id}") //
    public RedirectView editFeedback(
            @PathVariable Long id,
            @RequestParam String review,
            @RequestParam int stars
    ) throws GoodNotFoundException, FeedbackNotFoundException {
        service.handleFeedbackEditing(userFromContext(), id, review, stars);
        return new RedirectView(endpointsURLs.FEEDBACKS_MAIN);
    }
}
