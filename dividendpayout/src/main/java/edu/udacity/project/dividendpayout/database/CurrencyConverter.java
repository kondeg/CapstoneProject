package edu.udacity.project.dividendpayout.database;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;

public class CurrencyConverter {

    @TypeConverter
    public BigDecimal fromLong(Long value) {
        return value == null ? null : new BigDecimal(value).divide(new BigDecimal(100));
    }

    @TypeConverter
    public Long toLong(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        } else {
            return bigDecimal.multiply(new BigDecimal(100)).longValue();
        }
    }
}
