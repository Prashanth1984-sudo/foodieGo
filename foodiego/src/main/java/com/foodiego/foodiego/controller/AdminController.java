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

import jakarta.servlet.http.HttpSession;
import com.foodiego.foodiego.util.AdminUtil;

@Controller
public class AdminController {

        private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";

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
        public String adminDashboard(
                        HttpSession session,
                        Model model) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

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
        public String manageOrders(
                        HttpSession session,
                        Model model) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                model.addAttribute(
                                "orders",
                                orderRepository.findAllByOrderByOrderDateDesc());

                return "admin-orders";
        }

        @PostMapping("/admin/orders/update")
        public String updateOrderStatus(
                        HttpSession session,
                        @RequestParam Long orderId,
                        @RequestParam String status) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

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
        public String restaurants(
                        HttpSession session,
                        Model model) {
                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }
                model.addAttribute(
                                "restaurants",
                                restaurantRepository.findAll());

                return "admin-restaurants";
        }

        @PostMapping("/admin/restaurants/delete/{id}")
        public String deleteRestaurant(
                        HttpSession session,
                        @PathVariable Long id) {
                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                restaurantRepository.deleteById(id);

                return "redirect:/admin/restaurants";
        }

        @GetMapping("/admin/restaurants/add")
        public String addRestaurantPage(
                        HttpSession session,
                        Model model) {
                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                model.addAttribute(
                                "restaurant",
                                new Restaurant());

                return "restaurant-form";
        }

        @PostMapping("/admin/restaurants/save")
        public String saveRestaurant(
                        HttpSession session,
                        Restaurant restaurant) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                restaurantRepository.save(restaurant);

                return "redirect:/admin/restaurants";
        }

        @GetMapping("/admin/restaurants/edit/{id}")
        public String editRestaurant(
                        HttpSession session,
                        @PathVariable Long id,
                        Model model) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                model.addAttribute(
                                "restaurant",
                                restaurantRepository
                                                .findById(id)
                                                .orElse(null));

                return "restaurant-form";
        }

        @GetMapping("/admin/menu")
        public String menuItems(
                        HttpSession session,
                        Model model) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                model.addAttribute(
                                "menuItems",
                                menuItemRepository.findAll());

                return "admin-menu";
        }

        @GetMapping("/admin/menu/add")
        public String addMenuItemPage(
                        HttpSession session,
                        Model model) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

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
                        HttpSession session,
                        @PathVariable Long id,
                        Model model) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

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
                        HttpSession session,
                        MenuItem menuItem) {
                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                menuItemRepository.save(menuItem);

                return "redirect:/admin/menu";
        }

        @PostMapping("/admin/menu/delete/{id}")
        public String deleteMenuItem(
                        HttpSession session,
                        @PathVariable Long id) {

                if (!AdminUtil.isAdmin(session)) {
                        return REDIRECT_DASHBOARD;
                }

                menuItemRepository.deleteById(id);

                return "redirect:/admin/menu";
        }
}