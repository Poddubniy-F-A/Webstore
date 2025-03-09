package com.webstore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping(value = "${app.endpoints.main}")
    public String mainPage() {
        return "index";
    }
}
