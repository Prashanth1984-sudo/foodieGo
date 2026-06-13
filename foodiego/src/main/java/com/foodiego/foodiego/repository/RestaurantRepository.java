package com.foodiego.foodiego.repository;

import com.foodiego.foodiego.entity.Restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository
                extends JpaRepository<Restaurant, Long> {

        List<Restaurant> findByNameContainingIgnoreCase(
                        String keyword);

        List<Restaurant> findByCuisineContainingIgnoreCase(
                        String cuisine);

        @Query("""
                        SELECT r
                        FROM Restaurant r
                        WHERE LOWER(r.name)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(r.cuisine)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                        """)
        List<Restaurant> searchRestaurants(
                        @Param("keyword") String keyword);
}