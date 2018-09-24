package com.mediabox.rentalshare.model;

import lombok.Getter;

@Getter
public enum ShippingOption {
    ITEM_LOCATION(1, "Pick up at item location"),
    MY_LOCATION(2, "Pick up at my location"),
    OTHER_LOCATION(3, "Pick up at other location"),
    SHIPPING(4, "Shipping to my location");

    ShippingOption(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static String getTextByValue(int value) {
        for (ShippingOption shippingOption : values()) {
            if (shippingOption.getValue() == value) {
                return shippingOption.getText();
            }
        }
        return null;
    }

    String text;
    int value;
}
