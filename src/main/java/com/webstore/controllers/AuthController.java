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

    @GetMapping(value = "${app.endpoints.auth.customer.registration}")
    public String regPage(@ModelAttribute(REG_ERROR_MESSAGE_ATTRIBUTE_NAME) String errorMessage) {
        return "auth/registration";
    }

    @PostMapping(value = "${app.endpoints.auth.customer.registration}")
    public RedirectView regTry(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String nick
    ) throws NotUniqueLoginException {
        service.register(login, password, nick);
        return new RedirectView(endpointsURLs.AUTH_CUSTOMER_MAIN);
    }

    @GetMapping(value = "${app.endpoints.auth.customer.main}")
    public String custAuthPage(@ModelAttribute(AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME) String errorMessage) {
        return "auth/login/cust-login";
    }

    @GetMapping(value = "${app.endpoints.auth.moderator.main}")
    public String modAuthPage(@ModelAttribute(AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME) String errorMessage) {
        return "auth/login/mod-login";
    }

    @GetMapping(value = "${app.endpoints.auth.wh_worker.main}")
    public String wwAuthPage(@ModelAttribute(AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME) String errorMessage) {
        return "auth/login/ww-login";
    }

    @GetMapping(value = "${app.endpoints.auth.customer.failure}")
    public RedirectView custAuthPage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME, "Неверный логин или пароль");
        return new RedirectView(endpointsURLs.AUTH_CUSTOMER_MAIN);
    }

    @GetMapping(value = "${app.endpoints.auth.moderator.failure}")
    public RedirectView modAuthPage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME, "Неверный логин или пароль");
        return new RedirectView(endpointsURLs.AUTH_MODERATOR_MAIN);
    }

    @GetMapping(value = "${app.endpoints.auth.wh_worker.failure}")
    public RedirectView wwAuthPage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(AUTH_ERROR_MESSAGE_ATTRIBUTE_NAME, "Неверный логин или пароль");
        return new RedirectView(endpointsURLs.AUTH_WH_WORKER_MAIN);
    }

    @GetMapping(value = "${app.endpoints.errors.access_denied.cust_service}")
    public String catalogAccessDenied() {
        return "errors/access-denied/catalog";
    }

    @GetMapping(value = "${app.endpoints.errors.access_denied.management}")
    public String managementAccessDenied() {
        return "errors/access-denied/management";
    }

    @GetMapping(value = "${app.endpoints.errors.access_denied.warehouse}")
    public String warehouseAccessDenied() {
        return "errors/access-denied/warehouse";
    }
}
