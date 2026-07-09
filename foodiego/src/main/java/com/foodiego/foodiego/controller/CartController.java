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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

        private static final String REDIRECT_CART = "redirect:/cart";
        private static final String LOGGED_IN_USER = "loggedInUser";
        private static final String TOTAL = "total";
        private static final String PLATFORM_FEE = "platformFee";
        private static final String GRAND_TOTAL = "grandTotal";

        private final CartItemRepository cartItemRepository;

        private final MenuItemRepository menuItemRepository;

        public CartController(
                        CartItemRepository cartItemRepository,
                        MenuItemRepository menuItemRepository) {

                this.cartItemRepository = cartItemRepository;
                this.menuItemRepository = menuItemRepository;
        }

        @ResponseBody
        @PostMapping("/add/{menuItemId}")
        public String addToCart(
                        @PathVariable Long menuItemId,
                        @RequestParam Long restaurantId,
                        HttpSession session) {

                String userEmail = (String) session.getAttribute(LOGGED_IN_USER);

                if (userEmail == null) {
                        return "LOGIN_REQUIRED";
                }

                List<CartItem> existingCart = cartItemRepository.findByUserEmail(userEmail);

                if (!existingCart.isEmpty()) {

                        Long cartRestaurantId = existingCart.get(0).getRestaurantId();

                        if (!cartRestaurantId.equals(restaurantId)) {

                                return "DIFFERENT_RESTAURANT";
                        }
                }

                Optional<CartItem> existingItem = cartItemRepository
                                .findByUserEmailAndMenuItemId(
                                                userEmail,
                                                menuItemId);

                if (existingItem.isPresent()) {

                        CartItem item = existingItem.get();

                        item.setQuantity(item.getQuantity() + 1);

                        cartItemRepository.save(item);

                } else {

                        CartItem item = new CartItem();

                        item.setUserEmail(userEmail);

                        item.setMenuItemId(menuItemId);

                        item.setRestaurantId(restaurantId);

                        item.setQuantity(1);

                        cartItemRepository.save(item);
                }

                return "SUCCESS";
        }

        @ResponseBody
        @PostMapping("/replace-cart")
        public String replaceCart(
                        @RequestParam Long menuItemId,
                        @RequestParam Long restaurantId,
                        HttpSession session) {

                String userEmail = (String) session.getAttribute(LOGGED_IN_USER);

                if (userEmail == null) {
                        return "LOGIN_REQUIRED";
                }

                List<CartItem> existingCart = cartItemRepository.findByUserEmail(userEmail);

                cartItemRepository.deleteAll(existingCart);

                CartItem item = new CartItem();

                item.setUserEmail(userEmail);
                item.setMenuItemId(menuItemId);
                item.setRestaurantId(restaurantId);
                item.setQuantity(1);

                cartItemRepository.save(item);

                return "SUCCESS";
        }

        @GetMapping
        public String viewCart(
                        HttpSession session,
                        Model model) {

                String userEmail = (String) session.getAttribute(
                                LOGGED_IN_USER);

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

                double platformFee = 10.0;

                double gst = total * 0.05;

                double grandTotal = total + platformFee + gst;

                model.addAttribute(TOTAL, total);

                model.addAttribute(PLATFORM_FEE, platformFee);

                model.addAttribute("gst", gst);

                model.addAttribute(GRAND_TOTAL, grandTotal);

                if (!cartItems.isEmpty()) {
                        model.addAttribute(
                                        "restaurantId",
                                        cartItems.get(0).getRestaurantId());
                }

                return "cart";
        }

        @ResponseBody
        @PostMapping("/increase/{id}")
        public Map<String, Object> increase(
                        @PathVariable Long id) {

                CartItem item = cartItemRepository
                                .findById(id)
                                .orElseThrow();

                item.setQuantity(
                                item.getQuantity() + 1);

                cartItemRepository.save(item);

                Map<String, Object> response = new HashMap<>();

                response.put(
                                "quantity",
                                item.getQuantity());

                double total = calculateCartTotal(item.getUserEmail());

                double platformFee = 10.0;
                double gst = total * 0.05;
                double grandTotal = total + platformFee + gst;

                response.put(TOTAL, total);
                response.put(PLATFORM_FEE, platformFee);
                response.put("gst", gst);
                response.put(GRAND_TOTAL, grandTotal);

                return response;
        }

        @ResponseBody
        @PostMapping("/decrease/{id}")
        public Map<String, Object> decrease(
                        @PathVariable Long id) {

                CartItem item = cartItemRepository
                                .findById(id)
                                .orElseThrow();

                Map<String, Object> response = new HashMap<>();

                if (item.getQuantity() > 1) {

                        item.setQuantity(
                                        item.getQuantity() - 1);

                        cartItemRepository.save(item);

                        response.put(
                                        "quantity",
                                        item.getQuantity());

                        double total = calculateCartTotal(item.getUserEmail());

                        double platformFee = 10.0;
                        double gst = total * 0.05;
                        double grandTotal = total + platformFee + gst;

                        response.put(TOTAL, total);
                        response.put(PLATFORM_FEE, platformFee);
                        response.put("gst", gst);
                        response.put(GRAND_TOTAL, grandTotal);

                        response.put(
                                        "removed",
                                        false);

                } else {

                        cartItemRepository.delete(item);

                        response.put(
                                        "removed",
                                        true);
                }

                return response;
        }

        @PostMapping("/remove/{id}")
        public String remove(
                        @PathVariable Long id) {

                cartItemRepository.deleteById(id);

                return REDIRECT_CART;
        }

        private double calculateCartTotal(
                        String userEmail) {

                List<CartItem> cartItems = cartItemRepository.findByUserEmail(
                                userEmail);

                double total = 0;

                for (CartItem cart : cartItems) {

                        MenuItem menu = menuItemRepository.findById(
                                        cart.getMenuItemId())
                                        .orElse(null);

                        if (menu != null) {

                                total += menu.getPrice()
                                                * cart.getQuantity();
                        }
                }

                return total;
        }
}