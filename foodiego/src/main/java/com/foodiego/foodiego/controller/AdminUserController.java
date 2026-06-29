package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.User;
import com.foodiego.foodiego.repository.OrderRepository;
import com.foodiego.foodiego.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

        private final UserRepository userRepository;
        private final OrderRepository orderRepository;

        public AdminUserController(
                        UserRepository userRepository,
                        OrderRepository orderRepository) {

                this.userRepository = userRepository;
                this.orderRepository = orderRepository;
        }

        @GetMapping
        public String users(
                        @RequestParam(required = false) String keyword,
                        Model model) {

                List<User> users;

                if (keyword == null || keyword.isBlank()) {

                        users = userRepository.findAll();

                } else {

                        users = userRepository
                                        .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                                        keyword,
                                                        keyword);
                }

                model.addAttribute("users", users);

                model.addAttribute(
                                "totalUsers",
                                userRepository.count());

                model.addAttribute(
                                "admins",
                                userRepository.countByRole("ADMIN"));

                model.addAttribute(
                                "activeUsers",
                                userRepository.countByEnabled(true));

                model.addAttribute(
                                "blockedUsers",
                                userRepository.countByEnabled(false));

                return "admin-users";
        }

}