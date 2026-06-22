package com.foodiego.foodiego.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderSuccessController {

    @GetMapping("/order-success/{id}")
    public String successPage(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("orderId", id);

        return "order-success";
    }
}