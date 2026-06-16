package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.repository.OrderRepository;
import com.foodiego.foodiego.repository.RestaurantRepository;
import com.foodiego.foodiego.repository.MenuItemRepository;
import com.foodiego.foodiego.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.foodiego.foodiego.entity.MenuItem;
import com.foodiego.foodiego.entity.Order;
import com.foodiego.foodiego.entity.Restaurant;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminController {

        private final UserRepository userRepository;
        private final RestaurantRepository restaurantRepository;
        private final MenuItemRepository menuItemRepository;
        private final OrderRepository orderRepository;

        public AdminController(
                        UserRepository userRepository,
                        RestaurantRepository restaurantRepository,
                        MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository) {

                this.userRepository = userRepository;
                this.restaurantRepository = restaurantRepository;
                this.menuItemRepository = menuItemRepository;
                this.orderRepository = orderRepository;
        }

        @GetMapping("/admin")
        public String adminDashboard(Model model) {

                long totalUsers = userRepository.count();

                long totalRestaurants = restaurantRepository.count();

                long totalMenuItems = menuItemRepository.count();

                long totalOrders = orderRepository.count();

                double totalRevenue = 0;

                for (Order order : orderRepository.findAll()) {

                        totalRevenue += order.getTotalAmount();
                }

                model.addAttribute("totalUsers", totalUsers);
                model.addAttribute("totalRestaurants", totalRestaurants);
                model.addAttribute("totalMenuItems", totalMenuItems);
                model.addAttribute("totalOrders", totalOrders);
                model.addAttribute("totalRevenue", totalRevenue);

                // NEW
                model.addAttribute(
                                "users",
                                userRepository.findAll());

                Map<String, Long> userOrders = new HashMap<>();

                for (var user : userRepository.findAll()) {

                        userOrders.put(
                                        user.getEmail(),
                                        orderRepository.countByUserEmail(
                                                        user.getEmail()));
                }

                model.addAttribute(
                                "userOrders",
                                userOrders);

                return "admin-dashboard";
        }

        @GetMapping("/admin/orders")
        public String manageOrders(Model model) {

                model.addAttribute(
                                "orders",
                                orderRepository.findAllByOrderByOrderDateDesc());

                return "admin-orders";
        }

        @PostMapping("/admin/orders/update")
        public String updateOrderStatus(
                        @RequestParam Long orderId,
                        @RequestParam String status) {

                Order order = orderRepository
                                .findById(orderId)
                                .orElse(null);

                if (order != null) {

                        order.setStatus(status);

                        orderRepository.save(order);
                }

                return "redirect:/admin/orders";
        }

        @GetMapping("/admin/restaurants")
        public String restaurants(Model model) {

                model.addAttribute(
                                "restaurants",
                                restaurantRepository.findAll());

                return "admin-restaurants";
        }

        @PostMapping("/admin/restaurants/delete/{id}")
        public String deleteRestaurant(
                        @PathVariable Long id) {

                restaurantRepository.deleteById(id);

                return "redirect:/admin/restaurants";
        }

        @GetMapping("/admin/restaurants/add")
        public String addRestaurantPage(
                        Model model) {

                model.addAttribute(
                                "restaurant",
                                new Restaurant());

                return "restaurant-form";
        }

        @PostMapping("/admin/restaurants/save")
        public String saveRestaurant(
                        Restaurant restaurant) {

                restaurantRepository.save(restaurant);

                return "redirect:/admin/restaurants";
        }

        @GetMapping("/admin/restaurants/edit/{id}")
        public String editRestaurant(
                        @PathVariable Long id,
                        Model model) {

                model.addAttribute(
                                "restaurant",
                                restaurantRepository
                                                .findById(id)
                                                .orElse(null));

                return "restaurant-form";
        }

        @GetMapping("/admin/menu")
        public String menuItems(Model model) {

                model.addAttribute(
                                "menuItems",
                                menuItemRepository.findAll());

                return "admin-menu";
        }

        @GetMapping("/admin/menu/add")
        public String addMenuItemPage(
                        Model model) {

                model.addAttribute(
                                "menuItem",
                                new MenuItem());

                model.addAttribute(
                                "restaurants",
                                restaurantRepository.findAll());

                return "menu-form";
        }

        @GetMapping("/admin/menu/edit/{id}")
        public String editMenuItem(
                        @PathVariable Long id,
                        Model model) {

                model.addAttribute(
                                "menuItem",
                                menuItemRepository
                                                .findById(id)
                                                .orElse(null));

                model.addAttribute(
                                "restaurants",
                                restaurantRepository.findAll());

                return "menu-form";
        }

        @PostMapping("/admin/menu/save")
        public String saveMenuItem(
                        MenuItem menuItem) {

                menuItemRepository.save(menuItem);

                return "redirect:/admin/menu";
        }

        @PostMapping("/admin/menu/delete/{id}")
        public String deleteMenuItem(
                        @PathVariable Long id) {

                menuItemRepository.deleteById(id);

                return "redirect:/admin/menu";
        }
}