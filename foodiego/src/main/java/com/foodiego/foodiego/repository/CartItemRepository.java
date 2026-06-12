package com.foodiego.foodiego.repository;

import com.foodiego.foodiego.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserEmail(String userEmail);

    Optional<CartItem> findByUserEmailAndMenuItemId(
            String userEmail,
            Long menuItemId);
}