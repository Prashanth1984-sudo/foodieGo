package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.User;
import com.foodiego.foodiego.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(
            HttpSession session,
            Model model) {

        String userEmail = (String) session.getAttribute(
                "loggedInUser");

        if (userEmail == null) {

            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(userEmail)
                .orElse(null);

        model.addAttribute(
                "user",
                user);

        return "profile";
    }
}