package com.webstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GoodNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleGoodNotFound() {
        return new ModelAndView("errors/good-no-exists");
    }

    @ExceptionHandler(FeedbackNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleFeedbackNotFound() {
        return new ModelAndView("errors/feedback-not-exists");
    }

    @ExceptionHandler(IllegalCartConditionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalCartCondition() {
        return new ModelAndView("errors/illegal-cart-condition");
    }

    @ExceptionHandler(DuplicateFeedbackException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleDuplicatedFeedbacks(DuplicateFeedbackException e) {
        System.err.println(e.getMessage());
    }

    @ExceptionHandler(NoFeedbacksException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleNoFeedbacks(NoFeedbacksException e) {
        System.err.println(e.getMessage());
    }
}