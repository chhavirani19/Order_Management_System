package com.example.ordermanagement.model;

/** JSON body for part (c):  { "additionalQuantity": 4 } */
public class QuantityRequest {

    private Integer additionalQuantity;

    public Integer getAdditionalQuantity() {
        return additionalQuantity;
    }

    public void setAdditionalQuantity(Integer additionalQuantity) {
        this.additionalQuantity = additionalQuantity;
    }
}
