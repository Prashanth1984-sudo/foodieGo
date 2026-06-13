package com.foodiego.foodiego.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodiego.foodiego.repository.RestaurantRepository;

@Controller
public class HomeController {

        private final RestaurantRepository restaurantRepository;

        public HomeController(
                        RestaurantRepository restaurantRepository) {

                this.restaurantRepository = restaurantRepository;
        }

        @GetMapping("/")
        public String home(
                        @RequestParam(required = false) String error,
                        Model model) {

                model.addAttribute(
                                "error",
                                error);

                return "signup";
        }

        @GetMapping("/login")
        public String loginPage(
                        @RequestParam(required = false) String error,
                        Model model) {

                model.addAttribute(
                                "error",
                                error);

                return "login";
        }

        @GetMapping("/dashboard")
        public String dashboard(
                        HttpSession session,
                        Model model,

                        @RequestParam(required = false) String keyword,

                        @RequestParam(required = false) String cuisine) {

                if (session.getAttribute(
                                "loggedInUser") == null) {

                        return "redirect:/login";
                }

                if (cuisine != null &&
                                !cuisine.isBlank()) {

                        model.addAttribute(
                                        "restaurants",

                                        restaurantRepository
                                                        .findByCuisineContainingIgnoreCase(
                                                                        cuisine));

                } else if (keyword != null &&
                                !keyword.isBlank()) {

                        model.addAttribute(
                                        "restaurants",

                                        restaurantRepository
                                                        .searchRestaurants(
                                                                        keyword));

                } else {

                        model.addAttribute(
                                        "restaurants",

                                        restaurantRepository
                                                        .findAll());
                }

                return "dashboard";
        }

}