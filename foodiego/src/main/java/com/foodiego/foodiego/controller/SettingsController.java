package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.User;
import com.foodiego.foodiego.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile/settings")
public class SettingsController {

    private final UserRepository userRepository;

    private static final String LOGGED_IN_USER = "loggedInUser";

    private static final String REDIRECT_SETTINGS = "redirect:/profile/settings";

    public SettingsController(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @GetMapping
    public String settings(
            HttpSession session,
            Model model,

            @RequestParam(required = false) String error,

            @RequestParam(required = false) String success) {

        String email = (String) session.getAttribute(
                "loggedInUser");

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("error", error);
        model.addAttribute("success", success);

        return "settings";
    }

    @PostMapping("/update")
    public String updateProfile(

            @RequestParam String name,

            @RequestParam String email,

            HttpSession session) {

        String currentEmail = (String) session.getAttribute(
                LOGGED_IN_USER);

        User user = userRepository
                .findByEmail(currentEmail)
                .orElseThrow();

        user.setName(name);

        user.setEmail(email);

        userRepository.save(user);

        session.setAttribute(
                LOGGED_IN_USER,
                email);

        return REDIRECT_SETTINGS;
    }

    @PostMapping("/password")
    public String changePassword(

            @RequestParam String currentPassword,

            @RequestParam String newPassword,

            @RequestParam String confirmPassword,

            HttpSession session) {

        String email = (String) session.getAttribute(
                "loggedInUser");

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(
                currentPassword,
                user.getPassword())) {

            return "redirect:/profile/settings?error=current";
        }

        if (!newPassword.equals(confirmPassword)) {

            return "redirect:/profile/settings?error=confirm";
        }

        user.setPassword(
                encoder.encode(newPassword));

        userRepository.save(user);

        return "redirect:/profile/settings?success=password";
    }
}