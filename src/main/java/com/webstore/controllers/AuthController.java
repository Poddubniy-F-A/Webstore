package com.webstore.controllers;

import com.webstore.exceptions.auth.NotUniqueLoginException;
import com.webstore.services.RegistrationService;
import com.webstore.utils.EndpointsURLs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class AuthController {

    public static final String
            REG_ERROR_MESSAGE_ATTRIBUTE_NAME = "errorMessage",
            AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME = "errorMessage";

    private final EndpointsURLs endpointsURLs;
    private final RegistrationService service;

    @GetMapping(value = "${app.endpoints.auth.registration}")
    public String regPage(@ModelAttribute(REG_ERROR_MESSAGE_ATTRIBUTE_NAME) String errorMessage) {
        return "auth/registration";
    }

    @PostMapping(value = "${app.endpoints.auth.registration}")
    public RedirectView regTry(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String nick
    ) throws NotUniqueLoginException {
        service.register(login, password, nick);
        return new RedirectView(endpointsURLs.AUTH_MAIN);
    }

    @GetMapping(value = "${app.endpoints.auth.main}")
    public String authPage(@ModelAttribute(AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME) String errorMessage) {
        return "auth/login";
    }

    @GetMapping(value = "${app.endpoints.auth.failure}")
    public RedirectView authFailurePage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME,
                "Неверный логин или пароль"
        );
        return new RedirectView(endpointsURLs.AUTH_MAIN);
    }

    @GetMapping(value = "${app.endpoints.access_denied}")
    public String accessDenied() {
        return "errors/access-denied";
    }
}
