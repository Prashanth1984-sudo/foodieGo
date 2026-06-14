package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.Favorite;
import com.foodiego.foodiego.repository.FavoriteRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;

    public FavoriteController(
            FavoriteRepository favoriteRepository) {

        this.favoriteRepository = favoriteRepository;
    }

    @PostMapping("/{restaurantId}")
    public String toggleFavorite(

            @PathVariable Long restaurantId,

            HttpSession session) {

        String email = (String) session.getAttribute(
                "loggedInUser");

        if (email == null) {

            return "login";
        }

        var existing = favoriteRepository
                .findByUserEmailAndRestaurantId(
                        email,
                        restaurantId);

        if (existing.isPresent()) {

            favoriteRepository.delete(
                    existing.get());

            return "removed";
        }

        Favorite favorite = new Favorite();

        favorite.setUserEmail(email);

        favorite.setRestaurantId(
                restaurantId);

        favoriteRepository.save(
                favorite);

        return "added";
    }
}