package com.webstore.exceptions.auth;

import com.webstore.utils.EndpointsURLs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.webstore.controllers.AuthController.REG_ERROR_MESSAGE_ATTRIBUTE_NAME;

@ControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final EndpointsURLs endpointsURLs;

    @ExceptionHandler(NotUniqueLoginException.class)
    public RedirectView handleNotUniqueLogin(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(REG_ERROR_MESSAGE_ATTRIBUTE_NAME, "Логин уже занят");
        return new RedirectView(endpointsURLs.AUTH_REGISTRATION);
    }
}
