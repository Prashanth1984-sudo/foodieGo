package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.MenuItem;
import com.foodiego.foodiego.entity.Restaurant;
import com.foodiego.foodiego.repository.MenuItemRepository;
import com.foodiego.foodiego.repository.RestaurantRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

        Restaurant restaurant = restaurantRepository
                .findById(id)
                .orElse(null);

        List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(id);

        Set<String> categories = new LinkedHashSet<>();

        for (MenuItem item : menuItems) {
            if (item.getCategory() != null) {
                categories.add(item.getCategory());
            }
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("categories", categories);

        return "restaurant-details";
    }
}