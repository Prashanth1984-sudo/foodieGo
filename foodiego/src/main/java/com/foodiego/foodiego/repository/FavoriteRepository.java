package com.foodiego.foodiego.repository;

import com.foodiego.foodiego.entity.Favorite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserEmail(
            String userEmail);

    Optional<Favorite> findByUserEmailAndRestaurantId(
            String userEmail,
            Long restaurantId);
}