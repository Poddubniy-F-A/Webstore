package com.webstore.controllers;

import com.webstore.exceptions.NotUniqueLoginException;
import com.webstore.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @Value("${app.endpoints.auth.customer.main}")
    String custAuthUrl;

    private final RegistrationService service;

    @GetMapping(value = "${app.endpoints.auth.customer.registration}")
    public String regPage() {
        return "auth/registration";
    }

    @PostMapping(value = "${app.endpoints.auth.customer.registration}")
    public String regTry(
            Model model,
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String nick
    ) {
        try {
            service.register(login, password, nick);
            return "redirect:" + custAuthUrl;
        } catch (NotUniqueLoginException e) {
            model.addAttribute("errorMessage", "Логин уже занят");
            return "auth/registration";
        }
    }

    @GetMapping(value = "${app.endpoints.auth.customer.main}")
    public String custAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleEnteringError(model, error);
        return "auth/login/cust-login";
    }

    @GetMapping(value = "${app.endpoints.auth.moderator.main}")
    public String modAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleEnteringError(model, error);
        return "auth/login/mod-login";
    }

    @GetMapping(value = "${app.endpoints.auth.wh_worker.main}")
    public String wwAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleEnteringError(model, error);
        return "auth/login/ww-login";
    }

    private void handleEnteringError(Model model, Boolean error) {
        if (error != null) {
            model.addAttribute("errorMessage", "Неправильный логин или пароль");
        }
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
