package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.dto.OrderItemView;
import com.foodiego.foodiego.entity.MenuItem;
import com.foodiego.foodiego.entity.Order;
import com.foodiego.foodiego.entity.OrderItem;
import com.foodiego.foodiego.repository.MenuItemRepository;
import com.foodiego.foodiego.repository.OrderItemRepository;
import com.foodiego.foodiego.repository.OrderRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderDetailsController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderDetailsController(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            MenuItemRepository menuItemRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/{id}")
    public String orderDetails(
            @PathVariable Long id,
            Model model) {

        Order order = orderRepository
                .findById(id)
                .orElse(null);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);

        List<OrderItemView> items = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {

            MenuItem menuItem = menuItemRepository
                    .findById(orderItem.getMenuItemId())
                    .orElse(null);

            if (menuItem != null) {

                OrderItemView item = new OrderItemView();

                item.setName(menuItem.getName());

                item.setImageUrl(
                        menuItem.getImageUrl());

                item.setDescription(
                        menuItem.getDescription());

                item.setPrice(
                        orderItem.getPrice());

                item.setQuantity(
                        orderItem.getQuantity());

                items.add(item);
            }
        }
        model.addAttribute(
                "itemCount",
                items.size());
        model.addAttribute("order", order);
        model.addAttribute("items", items);

        return "order-details";
    }
}