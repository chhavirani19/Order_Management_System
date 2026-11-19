package com.example.ordermanagement.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ordermanagement.model.Order;
import com.example.ordermanagement.model.QuantityRequest;
import com.example.ordermanagement.model.StatusRequest;
import com.example.ordermanagement.service.OrderService;

/**
 * CONTROLLER - REST endpoints for the Order Management System.
 * Base URL: http://localhost:8080/api/orders
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // (a) Insert at least three orders -> 201 Created (duplicates skipped with message)
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> addOrders(@RequestBody List<Order> orders) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrders(orders));
    }

    // (a) Insert one order -> 201 Created, or 409 Conflict if the ID already exists
    @PostMapping
    public ResponseEntity<Order> addOrder(@RequestBody Order order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(order));
    }

    // (b) Orders with this status and quantity > 2, sorted by unit price DESC
    @GetMapping("/status/{status}")
    public Map<String, Object> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", orders.isEmpty()
                ? "No orders with status '" + status + "' and quantity greater than 2."
                : orders.size() + " order(s) found with status '" + status + "' and quantity > 2.");
        response.put("orders", orders);
        return response;
    }

    // (c) Increase quantity -> 200 OK, or 404 if the order does not exist
    @PutMapping("/{orderId}/quantity")
    public Map<String, Object> increaseQuantity(@PathVariable Integer orderId,
                                                @RequestBody QuantityRequest request) {
        Order updated = orderService.increaseQuantity(orderId, request.getAdditionalQuantity());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Quantity of order " + orderId + " increased by "
                + request.getAdditionalQuantity() + ".");
        response.put("order", updated);
        return response;
    }

    // (d) Update status -> 200 OK with updated details, 404 if not found, 400 if invalid status
    @PutMapping("/{orderId}/status")
    public Map<String, Object> updateStatus(@PathVariable Integer orderId,
                                            @RequestBody StatusRequest request) {
        Order updated = orderService.updateStatus(orderId, request.getStatus());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Status of order " + orderId + " updated to " + updated.getStatus() + ".");
        response.put("order", updated);
        return response;
    }

    // (e) Total order value using SUM(quantity * unit_price)
    @GetMapping("/total-value")
    public Map<String, Object> getTotalOrderValue() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalOrderValue", orderService.getTotalOrderValue());
        return response;
    }

    // (e) Delete only if status is Cancelled -> 200 OK, 400 if not Cancelled, 404 if not found
    @DeleteMapping("/{orderId}")
    public Map<String, Object> deleteOrder(@PathVariable Integer orderId) {
        Order deleted = orderService.deleteCancelledOrder(orderId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Order " + orderId + " deleted successfully.");
        response.put("deletedOrder", deleted);
        response.put("totalOrderValue", orderService.getTotalOrderValue());
        return response;
    }

    // Extra: all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Extra: one order by ID
    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable Integer orderId) {
        return orderService.getOrderById(orderId);
    }
}
