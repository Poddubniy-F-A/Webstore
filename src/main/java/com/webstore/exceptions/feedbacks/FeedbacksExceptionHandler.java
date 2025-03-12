package com.webstore.exceptions.feedbacks;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FeedbacksExceptionHandler {

    @ExceptionHandler(FeedbackNotFoundException.class)
    public ModelAndView handleFeedbackNotFound() {
        return new ModelAndView("errors/feedback-not-exists");
    }

    @ExceptionHandler(IllegalRatingTryException.class)
    public ModelAndView handleIllegalRatingTry() {
        return new ModelAndView("errors/feedback-not-allowed");
    }
}
