package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.Address;
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

import com.razorpay.RazorpayClient;
import org.json.JSONObject;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

        private final CartItemRepository cartItemRepository;
        private final MenuItemRepository menuItemRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final AddressRepository addressRepository;
        private final RazorpayClient razorpayClient;

        public CheckoutController(
                        CartItemRepository cartItemRepository,
                        MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository,
                        RazorpayClient razorpayClient,
                        OrderItemRepository orderItemRepository,
                        AddressRepository addressRepository) {

                this.cartItemRepository = cartItemRepository;
                this.menuItemRepository = menuItemRepository;
                this.orderRepository = orderRepository;
                this.orderItemRepository = orderItemRepository;
                this.addressRepository = addressRepository;
                this.razorpayClient = razorpayClient;
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
                        @RequestParam String paymentMethod,
                        @RequestParam(required = false) String paymentId,
                        @RequestParam(required = false) String razorpayOrderId,
                        HttpSession session) {

                String userEmail = (String) session.getAttribute("loggedInUser");
                if (userEmail == null) {
                        return "redirect:/login";
                }

                if (addressId == null || !addressRepository.existsById(addressId)) {
                        return "redirect:/profile/addresses";
                }

                List<CartItem> cartItems = cartItemRepository.findByUserEmail(userEmail);
                if (cartItems.isEmpty()) {
                        return "redirect:/cart";
                }

                double subtotal = calculateCartTotal(userEmail);

                DiscountResult dr = evaluateCoupon(couponCode, subtotal, userEmail);
                if (dr.errorRedirect != null) {
                        return dr.errorRedirect;
                }

                double total = subtotal - dr.discount;
                if (total < 0) {
                        total = 0;
                }

                Order order = buildAndSaveOrder(
                                userEmail,
                                total,
                                paymentMethod,
                                dr.discount,
                                couponCode,
                                addressId);

                order.setPaymentId(paymentId);
                order.setRazorpayOrderId(razorpayOrderId);

                orderRepository.save(order);

                saveOrderItemsFromCart(order, cartItems);

                cartItemRepository.deleteAll(cartItems);

                return "redirect:/order-success/" + order.getId();
        }

        // helper result container for coupon evaluation
        private static class DiscountResult {
                double discount;
                String errorRedirect;
        }

        // evaluate coupon and return discount or an error redirect string in
        // result.errorRedirect
        private DiscountResult evaluateCoupon(String couponCode, double subtotal, String userEmail) {
                DiscountResult res = new DiscountResult();
                res.discount = 0;
                res.errorRedirect = null;

                if (couponCode == null || couponCode.isBlank()) {
                        return res;
                }

                String code = couponCode.trim().toUpperCase();

                if ("FOOD50".equals(code)) {
                        if (subtotal < 499) {
                                res.errorRedirect = "redirect:/checkout?error=food50";
                                return res;
                        }
                        res.discount = 50;
                        return res;
                }

                if ("WELCOME20".equals(code)) {
                        long orderCount = orderRepository.countByUserEmail(userEmail);
                        if (orderCount > 0) {
                                res.errorRedirect = "redirect:/checkout?error=welcome20";
                                return res;
                        }
                        res.discount = subtotal * 0.20;
                        return res;
                }

                res.errorRedirect = "redirect:/checkout?error=invalid";
                return res;
        }

        private Order buildAndSaveOrder(String userEmail, double total, String paymentMethod, double discount,
                        String couponCode, Long addressId) {
                Order order = new Order();
                order.setUserEmail(userEmail);
                order.setTotalAmount(total);
                order.setPaymentMethod(paymentMethod);
                order.setDiscountAmount(discount);
                order.setCouponCode(couponCode);
                order.setStatus("PLACED");

                if ("COD".equals(paymentMethod)) {
                        order.setPaymentStatus("PENDING");
                } else {
                        order.setPaymentStatus("PAID");
                }
                order.setOrderDate(LocalDateTime.now());
                order.setAddressId(addressId);
                orderRepository.save(order);

                return order;
        }

        private void saveOrderItemsFromCart(Order order, List<CartItem> cartItems) {
                for (CartItem cart : cartItems) {
                        MenuItem menu = menuItemRepository.findById(cart.getMenuItemId()).orElse(null);
                        if (menu == null) {
                                continue;
                        }
                        OrderItem item = new OrderItem();
                        item.setOrderId(order.getId());
                        item.setMenuItemId(menu.getId());
                        item.setQuantity(cart.getQuantity());
                        item.setPrice(menu.getPrice());
                        orderItemRepository.save(item);
                }
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

        @PostMapping("/add-address")
        @ResponseBody
        public Long addAddress(
                        @RequestParam String fullName,
                        @RequestParam String phone,
                        @RequestParam String addressLine,
                        @RequestParam String city,
                        @RequestParam String state,
                        @RequestParam String pincode,
                        HttpSession session) {

                String userEmail = (String) session.getAttribute(
                                "loggedInUser");

                Address address = new Address();

                address.setUserEmail(userEmail);
                address.setFullName(fullName);
                address.setPhone(phone);
                address.setAddressLine(addressLine);
                address.setCity(city);
                address.setState(state);
                address.setPincode(pincode);

                Address saved = addressRepository.save(address);

                return saved.getId();
        }

        @PostMapping("/create-order")
        @ResponseBody
        public String createOrder(
                        HttpSession session) throws Exception {

                String userEmail = (String) session.getAttribute("loggedInUser");

                double total = calculateCartTotal(userEmail);

                JSONObject options = new JSONObject();

                options.put(
                                "amount",
                                (int) (total * 100));

                options.put(
                                "currency",
                                "INR");

                options.put(
                                "receipt",
                                "receipt_" + System.currentTimeMillis());

                com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);

                return razorpayOrder.toString();
        }
}