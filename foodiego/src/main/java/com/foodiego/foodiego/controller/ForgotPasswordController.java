package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.User;
import com.foodiego.foodiego.repository.UserRepository;
import com.foodiego.foodiego.service.EmailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@Controller
public class ForgotPasswordController {

    private static final String RESET_EMAIL_ATTR = "resetEmail";

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public ForgotPasswordController(
            UserRepository userRepository,
            EmailService emailService,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestParam String email,
            HttpSession session) {

        Optional<User> user =
                userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return "redirect:/forgot-password?error=email";
        }

        String otp =
                String.valueOf(
                        100000 + new Random().nextInt(900000));

        session.setAttribute("otp", otp);
        session.setAttribute(RESET_EMAIL_ATTR, email);

        emailService.sendOtp(email, otp);

        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session) {

        String savedOtp =
                (String) session.getAttribute("otp");

        if (savedOtp == null ||
                !savedOtp.equals(otp)) {

            return "redirect:/verify-otp?error=invalid";
        }

        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/reset-password?error=match";
        }

        String email =
                (String) session.getAttribute(RESET_EMAIL_ATTR);

        User user =
                userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        user.setPassword(
                passwordEncoder.encode(password));

        userRepository.save(user);

        session.removeAttribute("otp");
        session.removeAttribute(RESET_EMAIL_ATTR);

        return "redirect:/login?resetSuccess";
    }
}