package com.mediabox.rentalshare.model;

import lombok.Getter;

@Getter
public enum RentalRequestStatus {
    DELETED(0),
    IN_CART(1),
    SUBMITTED(2),
    CONFIRMED(3),
    ARCHIVED(4);

    RentalRequestStatus(int value) {
        this.value = value;
    }

    int value;
}
