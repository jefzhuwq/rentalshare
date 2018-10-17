package com.mediabox.rentalshare.model;

import lombok.Getter;

@Getter
public enum PeriodType {
    HOUR(1, "Per Hour"),
    DAY(2, "Per Day"),
    WEEK(3, "Per Week"),
    MONTH(4, "Per Month");

    PeriodType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static String getTextByValue(int value) {
        for (PeriodType periodType : values()) {
            if (periodType.getValue() == value) {
                return periodType.getText();
            }
        }
        return null;
    }

    String text;
    int value;
}
