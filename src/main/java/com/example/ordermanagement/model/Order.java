package com.example.ordermanagement.model;

/**
 * MODEL class - one row of the Orders table in ecommerce_db.
 *
 *   order_id      INT PRIMARY KEY  -> orderId
 *   customer_name VARCHAR(100)     -> customerName
 *   product_name  VARCHAR(100)     -> productName
 *   quantity      INT              -> quantity
 *   unit_price    DOUBLE           -> unitPrice
 *   status        VARCHAR(30)      -> status
 */
public class Order {

    private Integer orderId;
    private String customerName;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private String status;

    public Order() {
    }

    public Order(Integer orderId, String customerName, String productName,
                 Integer quantity, Double unitPrice, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = status;
    }

    // quantity * unit_price (shown in JSON as "totalPrice")
    public double getTotalPrice() {
        if (quantity == null || unitPrice == null) {
            return 0;
        }
        return quantity * unitPrice;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", customerName=" + customerName + ", productName=" + productName
                + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", status=" + status + "]";
    }
}
