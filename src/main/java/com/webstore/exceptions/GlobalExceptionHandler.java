package com.webstore.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GoodNotFoundException.class)
    public ModelAndView handleGoodNotFound() {
        return new ModelAndView("errors/good-not-exists");
    }
}
