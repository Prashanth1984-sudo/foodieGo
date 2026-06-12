package com.foodiego.foodiego.repository;

import com.foodiego.foodiego.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {
}