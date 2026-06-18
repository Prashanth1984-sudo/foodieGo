package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.User;
import com.foodiego.foodiego.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

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

        @PostMapping("/update-profile")
        public String updateProfile(

                        @RequestParam String name,

                        @RequestParam String email,

                        @RequestParam(required = false) MultipartFile profileImage,

                        HttpSession session)

                        throws Exception {

                String currentEmail = (String) session.getAttribute(
                                "loggedInUser");

                User user = userRepository.findByEmail(
                                currentEmail)
                                .orElseThrow();

                user.setName(name);

                if (!user.getEmail().equals(email)) {

                        if (userRepository.findByEmail(email).isPresent()) {

                                return "redirect:/profile/settings?error=email";
                        }

                        user.setEmail(email);

                        session.setAttribute(
                                        "loggedInUser",
                                        email);
                }

                if (profileImage != null &&
                                !profileImage.isEmpty()) {

                        String fileName = System.currentTimeMillis()
                                        + "_"
                                        + profileImage.getOriginalFilename();

                        Path uploadPath = Paths.get(
                                        "src/main/resources/static/uploads");

                        Files.createDirectories(uploadPath);

                        Files.write(
                                        uploadPath.resolve(fileName),
                                        profileImage.getBytes());

                        user.setProfileImage(
                                        "/uploads/" + fileName);
                }

                userRepository.save(user);

                session.setAttribute(
                                "loggedInUser",
                                email);

                return "redirect:/profile/settings?success=profile";
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

                if (encoder.matches(
                                newPassword,
                                user.getPassword())) {

                        return "redirect:/profile/settings?error=samepassword";
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