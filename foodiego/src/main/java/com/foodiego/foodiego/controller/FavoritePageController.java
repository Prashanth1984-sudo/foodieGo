package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.Favorite;
import com.foodiego.foodiego.entity.Restaurant;
import com.foodiego.foodiego.repository.FavoriteRepository;
import com.foodiego.foodiego.repository.RestaurantRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FavoritePageController {

    private final FavoriteRepository favoriteRepository;

    private final RestaurantRepository restaurantRepository;

    public FavoritePageController(
            FavoriteRepository favoriteRepository,
            RestaurantRepository restaurantRepository) {

        this.favoriteRepository = favoriteRepository;

        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/profile/favorites")
    public String favorites(
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute(
                "loggedInUser");

        if (email == null) {

            return "redirect:/login";
        }

        List<Favorite> favorites = favoriteRepository
                .findByUserEmail(email);

        List<Restaurant> restaurants = new ArrayList<>();

        for (Favorite favorite : favorites) {

            restaurantRepository
                    .findById(
                            favorite.getRestaurantId())
                    .ifPresent(
                            restaurants::add);
        }

        model.addAttribute(
                "favorites",
                restaurants);

        return "favorites";
    }
}