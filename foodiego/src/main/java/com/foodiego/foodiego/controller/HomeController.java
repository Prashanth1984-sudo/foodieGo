package com.foodiego.foodiego.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodiego.foodiego.repository.RestaurantRepository;

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
    public String dashboard(HttpSession session, Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "restaurants",
                restaurantRepository.findAll());

        return "dashboard";
    }

    private final RestaurantRepository restaurantRepository;

    public HomeController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
}