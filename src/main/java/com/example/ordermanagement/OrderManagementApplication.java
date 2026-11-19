package com.example.ordermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the REST API on http://localhost:8080/api/orders
 * (Right-click -> Run As -> Java Application, then test with Postman.)
 *
 * For the menu-driven console program run console/OrderManagementConsole.java instead.
 */
@SpringBootApplication
public class OrderManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderManagementApplication.class, args);
    }
}
