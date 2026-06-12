package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.repository.MenuItemRepository;
import com.foodiego.foodiego.repository.RestaurantRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantController(
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository) {

        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/{id}")
    public String restaurantDetails(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "restaurant",
                restaurantRepository.findById(id).orElse(null));

        model.addAttribute(
                "menuItems",
                menuItemRepository.findByRestaurantId(id));

        return "restaurant-details";
    }
}