package com.example.ordermanagement.service;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.ordermanagement.dao.OrderDao;
import com.example.ordermanagement.exception.DatabaseException;
import com.example.ordermanagement.exception.DuplicateOrderException;
import com.example.ordermanagement.exception.InvalidOrderException;
import com.example.ordermanagement.exception.OrderNotFoundException;
import com.example.ordermanagement.model.Order;

/**
 * SERVICE layer - the business rules of parts (a) to (e).
 * Used by the REST Controller and by the console menu program.
 */
@Service
public class OrderService {

    // Valid statuses (Cancelled is needed for part (e))
    public static final String[] VALID_STATUSES = {"Pending", "Shipped", "Delivered", "Cancelled"};

    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
        try {
            orderDao.createTableIfNotExists();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // ------------------------------------------------------------------
    // (a) Insert ONE order. Duplicate order ID -> DuplicateOrderException
    // ------------------------------------------------------------------
    public Order addOrder(Order order) {
        validateNewOrder(order);
        try {
            if (orderDao.exists(order.getOrderId())) {
                throw new DuplicateOrderException(order.getOrderId());
            }
            orderDao.insert(order);
            return order;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateOrderException(order.getOrderId());   // primary key violation
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // ------------------------------------------------------------------
    // (a) Insert AT LEAST THREE orders. Duplicates/invalid ones are
    //     skipped with a message, the others are inserted.
    // ------------------------------------------------------------------
    public Map<String, Object> addOrders(List<Order> orders) {
        if (orders == null || orders.size() < 3) {
            throw new InvalidOrderException("Please send at least 3 orders.");
        }
        List<Order> inserted = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        for (Order o : orders) {
            try {
                inserted.add(addOrder(o));
            } catch (DuplicateOrderException | InvalidOrderException e) {
                skipped.add(e.getMessage() + " Record skipped.");
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", inserted.size() + " order(s) inserted, " + skipped.size() + " skipped.");
        result.put("inserted", inserted);
        result.put("skipped", skipped);
        return result;
    }

    // ------------------------------------------------------------------
    // (b) Orders with the given status and quantity > 2,
    //     sorted by unit price (descending)
    // ------------------------------------------------------------------
    public List<Order> getOrdersByStatus(String status) {
        try {
            return orderDao.findByStatusWithQuantityAbove2(status);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // ------------------------------------------------------------------
    // (c) Increase quantity of an existing order
    // ------------------------------------------------------------------
    public Order increaseQuantity(int orderId, Integer additionalQuantity) {
        if (additionalQuantity == null || additionalQuantity <= 0) {
            throw new InvalidOrderException("Additional quantity must be greater than zero.");
        }
        try {
            int rows = orderDao.addQuantity(orderId, additionalQuantity);
            if (rows == 0) {
                throw new OrderNotFoundException(orderId);
            }
            return orderDao.findById(orderId);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // ------------------------------------------------------------------
    // (d) Retrieve order; update status only if the order exists and
    //     the new status is valid. Returns the UPDATED order.
    // ------------------------------------------------------------------
    public Order updateStatus(int orderId, String newStatus) {
        try {
            Order existing = orderDao.findById(orderId);       // retrieve
            if (existing == null) {
                throw new OrderNotFoundException(orderId);
            }
            String status = normalizeStatus(newStatus);
            if (status == null) {
                throw new InvalidOrderException("Invalid status '" + newStatus
                        + "'. Allowed: Pending, Shipped, Delivered, Cancelled.");
            }
            orderDao.updateStatus(orderId, status);
            return orderDao.findById(orderId);                  // updated details
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // ------------------------------------------------------------------
    // (e) Total order value = SUM(quantity * unit_price)
    // ------------------------------------------------------------------
    public double getTotalOrderValue() {
        try {
            return orderDao.getTotalOrderValue();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // ------------------------------------------------------------------
    // (e) Delete an order only if its status is Cancelled.
    //     Returns the deleted order.
    // ------------------------------------------------------------------
    public Order deleteCancelledOrder(int orderId) {
        try {
            Order existing = orderDao.findById(orderId);
            if (existing == null) {
                throw new OrderNotFoundException(orderId);
            }
            if (!"Cancelled".equalsIgnoreCase(existing.getStatus())) {
                throw new InvalidOrderException("Order " + orderId + " cannot be deleted. Its status is '"
                        + existing.getStatus() + "'. Only Cancelled orders can be deleted.");
            }
            if (orderDao.deleteIfCancelled(orderId) == 0) {
                throw new InvalidOrderException("Order " + orderId + " could not be deleted.");
            }
            return existing;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // (a) Used by the console to report a duplicate ID straight away
    public boolean orderExists(int orderId) {
        try {
            return orderDao.exists(orderId);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // Extra
    public List<Order> getAllOrders() {
        try {
            return orderDao.findAll();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // Extra
    public Order getOrderById(int orderId) {
        try {
            Order order = orderDao.findById(orderId);
            if (order == null) {
                throw new OrderNotFoundException(orderId);
            }
            return order;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    // ---------------------- Helpers ----------------------

    // "shipped" -> "Shipped"; returns null if not a valid status
    public static String normalizeStatus(String input) {
        if (input == null) {
            return null;
        }
        for (String s : VALID_STATUSES) {
            if (s.equalsIgnoreCase(input.trim())) {
                return s;
            }
        }
        return null;
    }

    private void validateNewOrder(Order o) {
        if (o == null || o.getOrderId() == null) {
            throw new InvalidOrderException("orderId is required.");
        }
        String id = "Order " + o.getOrderId() + ": ";
        if (isBlank(o.getCustomerName()) || isBlank(o.getProductName())) {
            throw new InvalidOrderException(id + "customerName and productName are required.");
        }
        if (o.getQuantity() == null || o.getQuantity() <= 0) {
            throw new InvalidOrderException(id + "quantity must be greater than zero.");
        }
        if (o.getUnitPrice() == null || o.getUnitPrice() < 0) {
            throw new InvalidOrderException(id + "unitPrice is required and cannot be negative.");
        }
        String status = normalizeStatus(o.getStatus());
        if (status == null) {
            throw new InvalidOrderException(id + "status must be Pending, Shipped, Delivered or Cancelled.");
        }
        o.setCustomerName(o.getCustomerName().trim());
        o.setProductName(o.getProductName().trim());
        o.setStatus(status);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
