package edu.udacity.project.dividendpayout.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import edu.udacity.project.dividendpayout.app.PortfolioFragment;

@Entity(foreignKeys = @ForeignKey(entity = Position.class,
        parentColumns = "id",
        childColumns = "positionId"))
public class DividendPaymentHistory {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "positionId")
    String positionId;

    @ColumnInfo(name = "year")
    Integer year;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "paymentDate")
    Date paymentDate;

    @TypeConverters(CurrencyConverter.class)
    @ColumnInfo(name = "divAmount")
    BigDecimal divAmount;

    @Ignore
    int month;

    @Ignore
    String ticker;

    @Ignore
    int shares;


    public DividendPaymentHistory(String positionId, Integer year, Date paymentDate, BigDecimal divAmount){
        this.id = UUID.randomUUID().toString();
        this.positionId = positionId;
        this.year = year;
        this.paymentDate = paymentDate;
        this.divAmount = divAmount;
        if (paymentDate!=null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(paymentDate);
            month = cal.get(Calendar.MONTH);
        }
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getDivAmount() {
        return divAmount;
    }

    public void setDivAmount(BigDecimal divAmount) {
        this.divAmount = divAmount;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }
}
