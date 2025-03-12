package com.webstore.exceptions.auth;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(NotUniqueLoginException.class)
    public ModelAndView handleNotUniqueLogin() {
        ModelAndView modelAndView = new ModelAndView("auth/registration");
        modelAndView.getModel().put("errorMessage", "Логин уже занят");
        return modelAndView;
    }
}
