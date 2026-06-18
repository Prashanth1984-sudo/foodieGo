package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.User;
import com.foodiego.foodiego.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profile")
public class ProfileController {

        private final UserRepository userRepository;

        public ProfileController(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @GetMapping
        public String profile(
                        HttpSession session,
                        Model model) {

                String userEmail = (String) session.getAttribute("loggedInUser");

                if (userEmail == null) {
                        return "redirect:/login";
                }

                User user = userRepository
                                .findByEmail(userEmail)
                                .orElse(null);

                model.addAttribute("user", user);

                return "profile";
        }

        @PostMapping("/upload-image")
        public String uploadImage(
                        @RequestParam("profileImage") MultipartFile file,
                        HttpSession session) {

                try {

                        String email = (String) session.getAttribute("loggedInUser");

                        if (email == null) {
                                return "redirect:/login";
                        }

                        User user = userRepository
                                        .findByEmail(email)
                                        .orElseThrow();

                        if (!file.isEmpty()) {

                                String fileName = System.currentTimeMillis()
                                                + "_"
                                                + file.getOriginalFilename();

                                Path uploadPath = Paths.get(
                                                "src/main/resources/static/uploads");

                                Files.createDirectories(uploadPath);

                                Files.write(
                                                uploadPath.resolve(fileName),
                                                file.getBytes());

                                user.setProfileImage(
                                                "/uploads/" + fileName);

                                userRepository.save(user);
                        }

                        return "redirect:/profile";

                } catch (Exception e) {

                        e.printStackTrace();

                        return "redirect:/profile?updated=" + System.currentTimeMillis();
                }
        }
}