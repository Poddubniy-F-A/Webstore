package com.webstore.controllers;

import com.webstore.exceptions.NotUniqueLoginException;
import com.webstore.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping
public class AuthController {
    private RegistrationService service;

    @GetMapping("/cust_auth/registration")
    public String regPage() {
        return "auth/registration";
    }

    @PostMapping("/cust_auth/registration")
    public String regTry(
            Model model,
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String nick
    ) {
        try {
            service.register(login, password, nick);
            return "redirect:/cust_auth/enter";
        } catch (NotUniqueLoginException e) {
            model.addAttribute("error_message", "Логин уже занят");
            return "auth/registration";
        }
    }

    @GetMapping("cust_auth/enter")
    public String custAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleEnteringError(model, error);
        return "auth/login/cust-login";
    }

    @GetMapping("/mod_auth")
    public String modAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleEnteringError(model, error);
        return "auth/login/mod-login";
    }

    @GetMapping("/ww_auth")
    public String wwAuthPage(Model model, @RequestParam(required = false) Boolean error) {
        handleEnteringError(model, error);
        return "auth/login/ww-login";
    }

    private void handleEnteringError(Model model, Boolean error) {
        if (error != null) {
            model.addAttribute("error_message", "Неправильный логин или пароль");
        }
    }

    @GetMapping("/cat_access_denied")
    public String catalogAccessDenied() {
        return "errors/access-denied/catalog";
    }

    @GetMapping("/man_access_denied")
    public String managementAccessDenied() {
        return "errors/access-denied/management";
    }

    @GetMapping("/wh_access_denied")
    public String warehouseAccessDenied() {
        return "errors/access-denied/warehouse";
    }
}
