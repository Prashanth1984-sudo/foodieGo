package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.CartItem;
import com.foodiego.foodiego.entity.MenuItem;
import com.foodiego.foodiego.entity.Order;
import com.foodiego.foodiego.entity.OrderItem;

import com.foodiego.foodiego.repository.AddressRepository;
import com.foodiego.foodiego.repository.CartItemRepository;
import com.foodiego.foodiego.repository.MenuItemRepository;
import com.foodiego.foodiego.repository.OrderItemRepository;
import com.foodiego.foodiego.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.foodiego.foodiego.repository.CouponRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

        private static final String SUCCESS_KEY = "success";

        private final CartItemRepository cartItemRepository;
        private final MenuItemRepository menuItemRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final AddressRepository addressRepository;
        private final CouponRepository couponRepository;

        public CheckoutController(
                        CartItemRepository cartItemRepository,
                        MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        AddressRepository addressRepository,
                        CouponRepository couponRepository) {

                this.cartItemRepository = cartItemRepository;
                this.menuItemRepository = menuItemRepository;
                this.orderRepository = orderRepository;
                this.orderItemRepository = orderItemRepository;
                this.addressRepository = addressRepository;
                this.couponRepository = couponRepository;
        }

        @GetMapping
        public String checkout(
                        HttpSession session,
                        Model model,
                        @RequestParam(required = false) String error) {

                String userEmail = (String) session.getAttribute(
                                "loggedInUser");

                if (userEmail == null) {
                        return "redirect:/login";
                }

                List<CartItem> cartItems = cartItemRepository
                                .findByUserEmail(userEmail);

                if (cartItems.isEmpty()) {

                        return "redirect:/cart";
                }

                double total = 0;

                for (CartItem cart : cartItems) {

                        MenuItem menu = menuItemRepository
                                        .findById(
                                                        cart.getMenuItemId())
                                        .orElse(null);

                        if (menu != null) {

                                total += menu.getPrice()
                                                * cart.getQuantity();
                        }
                }

                model.addAttribute("subtotal", total);
                model.addAttribute("discount", 0);
                model.addAttribute("finalTotal", total);

                var addresses = addressRepository
                                .findByUserEmail(
                                                userEmail);

                model.addAttribute(
                                "addresses",
                                addresses);

                model.addAttribute(
                                "hasAddresses",
                                !addresses.isEmpty());

                model.addAttribute("error", error);

                return "checkout";
        }

        @PostMapping("/place-order")
        public String placeOrder(

                        @RequestParam(required = false) Long addressId,

                        @RequestParam(required = false) String couponCode,

                        HttpSession session) {

                String userEmail = (String) session.getAttribute(
                                "loggedInUser");

                if (userEmail == null) {
                        return "redirect:/login";
                }
                if (addressId == null) {

                        return "redirect:/profile/addresses";
                }

                if (!addressRepository.existsById(
                                addressId)) {

                        return "redirect:/profile/addresses";
                }

                List<CartItem> cartItems = cartItemRepository
                                .findByUserEmail(userEmail);

                if (cartItems.isEmpty()) {

                        return "redirect:/cart";
                }

                double total = 0;

                for (CartItem cart : cartItems) {

                        MenuItem menu = menuItemRepository
                                        .findById(
                                                        cart.getMenuItemId())
                                        .orElse(null);

                        if (menu != null) {

                                total += menu.getPrice()
                                                * cart.getQuantity();
                        }
                }
                double discount = 0;

                if (couponCode != null &&
                                !couponCode.isBlank()) {

                        couponCode = couponCode.trim().toUpperCase();

                        if (couponCode.equals("FOOD50")) {

                                if (total >= 499) {

                                        discount = 50;

                                } else {

                                        return "redirect:/checkout?error=food50";
                                }
                        }

                        else if (couponCode.equals("WELCOME20")) {

                                long orderCount = orderRepository.countByUserEmail(userEmail);

                                if (orderCount == 0) {

                                        discount = total * 0.20;

                                } else {

                                        return "redirect:/checkout?error=welcome20";
                                }
                        }

                        else {

                                return "redirect:/checkout?error=invalid";
                        }
                }

                total -= discount;

                if (total < 0) {
                        total = 0;
                }

                Order order = new Order();

                order.setUserEmail(userEmail);

                order.setTotalAmount(total);

                order.setDiscountAmount(discount);

                order.setCouponCode(couponCode);

                order.setStatus("PLACED");

                order.setOrderDate(LocalDateTime.now());

                order.setAddressId(addressId);

                orderRepository.save(order);

                for (CartItem cart : cartItems) {

                        MenuItem menu = menuItemRepository
                                        .findById(
                                                        cart.getMenuItemId())
                                        .orElse(null);

                        if (menu != null) {

                                OrderItem item = new OrderItem();

                                item.setOrderId(
                                                order.getId());

                                item.setMenuItemId(
                                                menu.getId());

                                item.setQuantity(
                                                cart.getQuantity());

                                item.setPrice(
                                                menu.getPrice());

                                orderItemRepository
                                                .save(item);
                        }
                }

                cartItemRepository.deleteAll(
                                cartItems);

                return "redirect:/checkout/success";
        }

        @GetMapping("/success")
        public String success() {

                return "order-success";
        }

        // helper to compute cart subtotal for a given user
        private double calculateCartTotal(String userEmail) {
                if (userEmail == null) {
                        return 0;
                }

                List<CartItem> cartItems = cartItemRepository.findByUserEmail(userEmail);

                if (cartItems == null || cartItems.isEmpty()) {
                        return 0;
                }

                double total = 0;

                for (CartItem cart : cartItems) {

                        MenuItem menu = menuItemRepository
                                        .findById(
                                                        cart.getMenuItemId())
                                        .orElse(null);

                        if (menu != null) {

                                total += menu.getPrice()
                                                * cart.getQuantity();
                        }
                }

                return total;
        }

        @PostMapping("/validate-coupon")
        @ResponseBody
        public Map<String, Object> validateCoupon(
                        @RequestParam String couponCode,
                        HttpSession session) {

                String userEmail = (String) session.getAttribute("loggedInUser");

                Map<String, Object> response = new HashMap<>();

                if (userEmail == null) {

                        response.put("success", false);
                        response.put("message", "Please login first");

                        return response;
                }

                double subtotal = calculateCartTotal(userEmail);

                couponCode = couponCode.trim().toUpperCase();

                double discount = 0;

                // FOOD50
                if ("FOOD50".equals(couponCode)) {

                        if (subtotal < 499) {

                                response.put("success", false);
                                response.put(
                                                "message",
                                                "Minimum order ₹499 required for FOOD50");

                                return response;
                        }

                        discount = 50;

                        response.put("success", true);
                        response.put("discount", discount);
                        response.put("finalTotal", subtotal - discount);
                        response.put(
                                        "message",
                                        "₹50 discount applied successfully");

                        return response;
                }

                // WELCOME20
                if ("WELCOME20".equals(couponCode)) {

                        long orderCount = orderRepository.countByUserEmail(userEmail);

                        if (orderCount > 0) {

                                response.put("success", false);
                                response.put(
                                                "message",
                                                "WELCOME20 is only for first order");

                                return response;
                        }

                        discount = subtotal * 0.20;

                        response.put("success", true);
                        response.put("discount", discount);
                        response.put("finalTotal", subtotal - discount);
                        response.put(
                                        "message",
                                        "20% first-order discount applied");

                        return response;
                }

                // INVALID COUPON
                response.put("success", false);
                response.put(
                                "message",
                                "Invalid coupon code");

                return response;
        }
}