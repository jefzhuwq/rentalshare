package com.mediabox.rentalshare.model;

import lombok.Getter;

@Getter
public enum ContactUsStatus {
    CREATED(1);

    ContactUsStatus(int value) {
        this.value = value;
    }

    int value;
}
