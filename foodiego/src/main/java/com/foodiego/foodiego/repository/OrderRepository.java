package com.foodiego.foodiego.repository;

import com.foodiego.foodiego.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUserEmailOrderByOrderDateDesc(
            String userEmail);
}