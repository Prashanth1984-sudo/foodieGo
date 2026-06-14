package com.foodiego.foodiego.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodiego.foodiego.entity.Coupon;

public interface CouponRepository
        extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);
}