package com.webstore.controllers;

import com.webstore.exceptions.auth.NotUniqueLoginException;
import com.webstore.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

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
    public RedirectView regTry(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String nick
    ) throws NotUniqueLoginException {
        service.register(login, password, nick);
        return new RedirectView(custAuthUrl);
    }

    @GetMapping(value = "${app.endpoints.auth.customer.main}")
    public String custAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleLoginError(model, error);
        return "auth/login/cust-login";
    }

    @GetMapping(value = "${app.endpoints.auth.moderator.main}")
    public String modAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleLoginError(model, error);
        return "auth/login/mod-login";
    }

    @GetMapping(value = "${app.endpoints.auth.wh_worker.main}")
    public String wwAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleLoginError(model, error);
        return "auth/login/ww-login";
    }

    private void handleLoginError(Model model, Boolean error) {
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
