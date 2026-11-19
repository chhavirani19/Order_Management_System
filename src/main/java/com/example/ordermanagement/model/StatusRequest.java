package com.example.ordermanagement.model;

/** JSON body for part (d):  { "status": "Shipped" } */
public class StatusRequest {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
