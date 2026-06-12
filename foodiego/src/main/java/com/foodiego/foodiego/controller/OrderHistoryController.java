package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderHistoryController {

    private final OrderRepository orderRepository;

    public OrderHistoryController(
            OrderRepository orderRepository) {

        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    public String orders(
            HttpSession session,
            Model model) {

        String userEmail = (String) session.getAttribute(
                "loggedInUser");

        if (userEmail == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "orders",
                orderRepository
                        .findByUserEmailOrderByOrderDateDesc(
                                userEmail));

        return "orders";
    }
}