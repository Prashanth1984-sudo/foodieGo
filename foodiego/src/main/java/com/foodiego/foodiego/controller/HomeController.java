package com.foodiego.foodiego.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String error,
            Model model) {

        model.addAttribute("error", error);

        return "signup";
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            Model model) {

        model.addAttribute("error", error);

        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        return "dashboard";
    }
}