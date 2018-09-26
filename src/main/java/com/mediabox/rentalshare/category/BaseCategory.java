package com.mediabox.rentalshare.category;

import lombok.Getter;

@Getter
public enum BaseCategory {
    TEXT_BOOK(1, "TextBook"),
    BOAT(2, "Boat"),
    CAR(3, "Car");

    BaseCategory(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    private Integer id;
    private String name;
}
