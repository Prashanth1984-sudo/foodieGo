package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.dto.CartItemView;
import com.foodiego.foodiego.entity.CartItem;
import com.foodiego.foodiego.entity.MenuItem;
import com.foodiego.foodiego.repository.CartItemRepository;
import com.foodiego.foodiego.repository.MenuItemRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.ArrayList;

import com.foodiego.foodiego.dto.CartItemView;

import com.foodiego.foodiego.entity.MenuItem;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final String REDIRECT_CART = "redirect:/cart";

    private final CartItemRepository cartItemRepository;

    private final MenuItemRepository menuItemRepository;

    public CartController(
            CartItemRepository cartItemRepository,
            MenuItemRepository menuItemRepository) {

        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @PostMapping("/add/{menuItemId}")
    public String addToCart(
            @PathVariable Long menuItemId,
            HttpSession session,
            @RequestParam Long restaurantId) {

        String userEmail = (String) session.getAttribute(
                "loggedInUser");

        if (userEmail == null) {
            return "redirect:/login";
        }

        Optional<CartItem> existingItem = cartItemRepository
                .findByUserEmailAndMenuItemId(
                        userEmail,
                        menuItemId);

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();

            item.setQuantity(
                    item.getQuantity() + 1);

            cartItemRepository.save(item);

        } else {

            CartItem item = new CartItem();

            item.setUserEmail(userEmail);

            item.setMenuItemId(menuItemId);

            item.setQuantity(1);

            cartItemRepository.save(item);
        }

        return "redirect:/restaurants/" + restaurantId;
    }

    @GetMapping
    public String viewCart(
            HttpSession session,
            Model model) {

        String userEmail = (String) session.getAttribute(
                "loggedInUser");

        if (userEmail == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartItemRepository
                .findByUserEmail(userEmail);

        List<CartItemView> cartView = new ArrayList<>();

        double total = 0;

        for (CartItem cart : cartItems) {

            MenuItem menu = menuItemRepository
                    .findById(
                            cart.getMenuItemId())
                    .orElse(null);

            if (menu != null) {

                CartItemView item = new CartItemView();

                item.setCartId(cart.getId());
                item.setMenuItemId(menu.getId());

                item.setName(menu.getName());

                item.setImageUrl(
                        menu.getImageUrl());

                item.setPrice(
                        menu.getPrice());

                item.setQuantity(
                        cart.getQuantity());

                cartView.add(item);

                total += menu.getPrice()
                        *
                        cart.getQuantity();
            }
        }

        model.addAttribute(
                "cartItems",
                cartView);

        model.addAttribute(
                "total",
                total);

        return "cart";
    }

    @PostMapping("/increase/{id}")
    public String increase(
            @PathVariable Long id) {

        CartItem item = cartItemRepository
                .findById(id)
                .orElse(null);

        if (item != null) {

            item.setQuantity(
                    item.getQuantity() + 1);

            cartItemRepository.save(item);
        }

        return REDIRECT_CART;
    }

    @PostMapping("/decrease/{id}")
    public String decrease(
            @PathVariable Long id) {

        CartItem item = cartItemRepository
                .findById(id)
                .orElse(null);

        if (item != null) {

            if (item.getQuantity() > 1) {

                item.setQuantity(
                        item.getQuantity() - 1);

                cartItemRepository.save(item);

            } else {

                cartItemRepository.delete(item);
            }
        }

        return REDIRECT_CART;
    }

    @PostMapping("/remove/{id}")
    public String remove(
            @PathVariable Long id) {

        cartItemRepository.deleteById(id);

        return REDIRECT_CART;
    }
}