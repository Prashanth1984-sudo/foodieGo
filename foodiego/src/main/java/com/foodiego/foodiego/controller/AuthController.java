package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.User;
import com.foodiego.foodiego.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodiego.foodiego.service.EmailService;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/?error=passwordMismatch";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/?error=emailExists";
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(password));

        userRepository.save(user);

        emailService.sendWelcomeEmail(
                user.getEmail(),
                user.getName());

        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()
                &&
                passwordEncoder.matches(
                        password,
                        user.get().getPassword())) {

            session.setAttribute(
                    "loggedInUser",
                    user.get().getEmail());

            session.setAttribute(
                    "userRole",
                    user.get().getRole());

            if ("ADMIN".equals(user.get().getRole())) {
                return "redirect:/admin";
            }

            return "redirect:/dashboard";
        }

        return "redirect:/login?error=invalidCredentials";
    }

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}