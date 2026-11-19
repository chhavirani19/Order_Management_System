-- Run this in MySQL Workbench / MySQL command line before running the Java program
-- (The Java program also creates the table automatically if it is missing.)

CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

CREATE TABLE IF NOT EXISTS Orders (
    order_id      INT PRIMARY KEY,
    customer_name VARCHAR(100),
    product_name  VARCHAR(100),
    quantity      INT,
    unit_price    DOUBLE,
    status        VARCHAR(30)
);
