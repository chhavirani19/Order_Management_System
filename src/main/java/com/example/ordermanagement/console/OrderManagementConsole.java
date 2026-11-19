package com.example.ordermanagement.console;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.example.ordermanagement.dao.DBConnection;
import com.example.ordermanagement.dao.OrderDao;
import com.example.ordermanagement.exception.DuplicateOrderException;
import com.example.ordermanagement.exception.InvalidOrderException;
import com.example.ordermanagement.exception.OrderNotFoundException;
import com.example.ordermanagement.model.Order;
import com.example.ordermanagement.service.OrderService;

/**
 * MENU-DRIVEN Java JDBC application (answer to the question paper).
 * Right-click -> Run As -> Java Application, then type in the Console.
 *
 *   1 -> (a) Insert at least three orders (PreparedStatement, duplicate IDs handled)
 *   2 -> (b) Orders with a status and quantity > 2, sorted by unit price DESC
 *   3 -> (c) Increase the quantity of an order
 *   4 -> (d) Retrieve an order and update its status
 *   5 -> (e) Total order value (SUM) and delete a Cancelled order
 *   6 -> Display all orders
 */
public class OrderManagementConsole {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // (a) Establish a connection with the MySQL database
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Connected to " + con.getCatalog() + " successfully.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("Check that MySQL is running and USER / PASSWORD in DBConnection.java are correct.");
            return;
        }

        OrderService service = new OrderService(new OrderDao());

        int choice;
        do {
            System.out.println("\n========== ORDER MANAGEMENT SYSTEM ==========");
            System.out.println("1. Insert Order Records                          (a)");
            System.out.println("2. Display Orders by Status (quantity > 2)       (b)");
            System.out.println("3. Increase Order Quantity                       (c)");
            System.out.println("4. Update Order Status                           (d)");
            System.out.println("5. Total Order Value & Delete Cancelled Order    (e)");
            System.out.println("6. Display All Orders");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = readInt();

            try {
                switch (choice) {
                    case 1: insertOrders(service); break;
                    case 2: displayByStatus(service); break;
                    case 3: increaseQuantity(service); break;
                    case 4: updateStatus(service); break;
                    case 5: totalValueAndDelete(service); break;
                    case 6: printOrders(service.getAllOrders(), "No orders found."); break;
                    case 0: System.out.println("Exiting... Thank you!"); break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } catch (OrderNotFoundException | InvalidOrderException | DuplicateOrderException e) {
                System.out.println(e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 0);
    }

    // (a) Insert at least three orders
    private static void insertOrders(OrderService service) {
        System.out.print("How many orders do you want to insert? (minimum 3): ");
        int count = readInt();
        while (count < 3) {
            System.out.print("At least 3 orders are required. Enter again: ");
            count = readInt();
        }

        int inserted = 0;
        for (int i = 1; i <= count; i++) {
            System.out.println("\nEnter details of order " + i + ":");
            Order o = new Order();
            System.out.print("Order ID      : ");
            o.setOrderId(readInt());
            if (service.orderExists(o.getOrderId())) {          // duplicate order ID
                System.out.println("Error: Order ID " + o.getOrderId() + " already exists. Record skipped.");
                continue;
            }
            System.out.print("Customer Name : ");
            o.setCustomerName(sc.nextLine());
            System.out.print("Product Name  : ");
            o.setProductName(sc.nextLine());
            System.out.print("Quantity      : ");
            o.setQuantity(readInt());
            System.out.print("Unit Price    : ");
            o.setUnitPrice(readDouble());
            System.out.print("Status (Pending/Shipped/Delivered/Cancelled): ");
            o.setStatus(readValidStatus());

            try {
                service.addOrder(o);       // PreparedStatement INSERT
                inserted++;
                System.out.println("Order " + o.getOrderId() + " inserted successfully.");
            } catch (DuplicateOrderException | InvalidOrderException e) {
                System.out.println("Error: " + e.getMessage() + " Record skipped.");
            }
        }
        System.out.println("\n" + inserted + " order(s) inserted.");
    }

    // (b)
    private static void displayByStatus(OrderService service) {
        System.out.print("Enter order status (Pending/Shipped/Delivered/Cancelled): ");
        String status = sc.nextLine().trim();
        printOrders(service.getOrdersByStatus(status),
                "No orders with status '" + status + "' and quantity greater than 2.");
    }

    // (c)
    private static void increaseQuantity(OrderService service) {
        System.out.print("Enter order ID: ");
        int id = readInt();
        System.out.print("Enter additional quantity: ");
        int extra = readInt();
        Order updated = service.increaseQuantity(id, extra);
        System.out.println("Quantity of order " + id + " increased by " + extra + ".");
        printOrders(List.of(updated), "");
    }

    // (d)
    private static void updateStatus(OrderService service) {
        System.out.print("Enter order ID: ");
        int id = readInt();
        Order existing = service.getOrderById(id);           // retrieve (not found -> message)
        System.out.println("Order found:");
        printOrders(List.of(existing), "");

        System.out.print("Enter new status (Pending/Shipped/Delivered/Cancelled): ");
        String newStatus = sc.nextLine();
        Order updated = service.updateStatus(id, newStatus);  // invalid status -> message
        System.out.println("Status updated successfully. Updated order details:");
        printOrders(List.of(updated), "");
    }

    // (e)
    private static void totalValueAndDelete(OrderService service) {
        System.out.printf("Total Order Value: %.2f%n", service.getTotalOrderValue());
        System.out.print("\nEnter order ID to delete: ");
        int id = readInt();
        service.deleteCancelledOrder(id);   // not found / not Cancelled -> message
        System.out.println("Order " + id + " deleted successfully.");
        System.out.printf("Total Order Value now: %.2f%n", service.getTotalOrderValue());
    }

    // ---------------------- Helpers ----------------------

    private static void printOrders(List<Order> orders, String emptyMessage) {
        System.out.printf("%-8s %-18s %-18s %8s %12s %12s  %-10s%n",
                "OrderID", "Customer", "Product", "Qty", "Unit Price", "Total", "Status");
        System.out.println("---------------------------------------------------------------------------------------------");
        if (orders.isEmpty()) {
            System.out.println(emptyMessage);
        }
        for (Order o : orders) {
            System.out.printf("%-8d %-18s %-18s %8d %12.2f %12.2f  %-10s%n",
                    o.getOrderId(), o.getCustomerName(), o.getProductName(),
                    o.getQuantity(), o.getUnitPrice(), o.getTotalPrice(), o.getStatus());
        }
    }

    private static String readValidStatus() {
        while (true) {
            String status = OrderService.normalizeStatus(sc.nextLine());
            if (status != null) {
                return status;
            }
            System.out.print("Invalid status. Enter Pending, Shipped, Delivered or Cancelled: ");
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid whole number: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
