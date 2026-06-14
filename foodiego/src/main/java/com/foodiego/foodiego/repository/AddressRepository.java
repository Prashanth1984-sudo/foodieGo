package com.foodiego.foodiego.repository;

import com.foodiego.foodiego.entity.Address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository
        extends JpaRepository<Address, Long> {

    List<Address> findByUserEmail(
            String userEmail);
}