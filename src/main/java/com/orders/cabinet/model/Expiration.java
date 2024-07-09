package com.orders.cabinet.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.temporal.ChronoUnit;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Expiration {
    INSTANCE;

    int amount;
    ChronoUnit unit;

    Expiration(){
        this.amount = 24;
        this.unit = ChronoUnit.HOURS;
    }

    public long getExpirationMillis() {
        return unit.getDuration().toMillis() * amount;
    }
}
