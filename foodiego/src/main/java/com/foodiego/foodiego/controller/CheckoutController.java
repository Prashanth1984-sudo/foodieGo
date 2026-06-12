package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.CartItem;
import com.foodiego.foodiego.entity.MenuItem;
import com.foodiego.foodiego.entity.Order;
import com.foodiego.foodiego.entity.OrderItem;

import com.foodiego.foodiego.repository.CartItemRepository;
import com.foodiego.foodiego.repository.MenuItemRepository;
import com.foodiego.foodiego.repository.OrderItemRepository;
import com.foodiego.foodiego.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CheckoutController(
            CartItemRepository cartItemRepository,
            MenuItemRepository menuItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {

        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping
    public String checkout(
            HttpSession session,
            Model model) {

        String userEmail = (String) session.getAttribute(
                "loggedInUser");

        if (userEmail == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartItemRepository
                .findByUserEmail(userEmail);

        double total = 0;

        for (CartItem cart : cartItems) {

            MenuItem menu = menuItemRepository
                    .findById(
                            cart.getMenuItemId())
                    .orElse(null);

            if (menu != null) {

                total += menu.getPrice()
                        *
                        cart.getQuantity();
            }
        }

        model.addAttribute("total", total);

        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(
            HttpSession session) {

        String userEmail = (String) session.getAttribute(
                "loggedInUser");

        if (userEmail == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartItemRepository
                .findByUserEmail(userEmail);

        double total = 0;

        for (CartItem cart : cartItems) {

            MenuItem menu = menuItemRepository
                    .findById(
                            cart.getMenuItemId())
                    .orElse(null);

            if (menu != null) {

                total += menu.getPrice()
                        *
                        cart.getQuantity();
            }
        }

        Order order = new Order();

        order.setUserEmail(userEmail);

        order.setTotalAmount(total);

        order.setStatus("PLACED");

        order.setOrderDate(
                LocalDateTime.now());

        orderRepository.save(order);

        for (CartItem cart : cartItems) {

            MenuItem menu = menuItemRepository
                    .findById(
                            cart.getMenuItemId())
                    .orElse(null);

            if (menu != null) {

                OrderItem item = new OrderItem();

                item.setOrderId(order.getId());

                item.setMenuItemId(menu.getId());

                item.setQuantity(
                        cart.getQuantity());

                item.setPrice(
                        menu.getPrice());

                orderItemRepository.save(item);
            }
        }

        cartItemRepository.deleteAll(cartItems);

        return "redirect:/checkout/success";
    }

    @GetMapping("/success")
    public String success() {
        return "order-success";
    }
}