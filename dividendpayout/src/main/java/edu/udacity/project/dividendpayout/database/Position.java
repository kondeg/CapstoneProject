package edu.udacity.project.dividendpayout.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Position implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "ticker")
    String ticker;

    @ColumnInfo(name = "numberOfShares")
    Integer numberOfShares;

    @TypeConverters(CurrencyConverter.class)
    @ColumnInfo(name = "purchasePrice")
    BigDecimal purchasePrice;

    @TypeConverters(CurrencyConverter.class)
    @ColumnInfo(name = "lastKnownPrice")
    BigDecimal lastKnownPrice;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "purchaseDate")
    Date purchaseDate;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "saleDate")
    Date saleDate;

    @TypeConverters(CurrencyConverter.class)
    @ColumnInfo(name = "salePrice")
    BigDecimal salePrice;

    @TypeConverters(CurrencyConverter.class)
    @ColumnInfo(name = "yearlyDividendAmount")
    BigDecimal yearlyDividendAmount;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "lastExDate")
    Date lastExDate;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "lastUpdated")
    Date lastUpdated;

    @ColumnInfo(name = "dividendFrequency")
    Integer dividendFrequency;

    @TypeConverters(CurrencyConverter.class)
    @ColumnInfo(name = "dividendYield")
    BigDecimal dividendYield;

    @Ignore
    List<Dividend> dividends;

    public Position(@NonNull String id, String ticker, Integer numberOfShares, BigDecimal purchasePrice, BigDecimal lastKnownPrice, Date purchaseDate, Date saleDate, BigDecimal salePrice, BigDecimal yearlyDividendAmount, Date lastExDate, Date lastUpdated, Integer dividendFrequency, BigDecimal dividendYield) {
        this.id = id;
        this.ticker = ticker;
        this.numberOfShares = numberOfShares;
        this.purchasePrice = purchasePrice;
        this.lastKnownPrice = lastKnownPrice;
        this.purchaseDate = purchaseDate;
        this.saleDate = saleDate;
        this.salePrice = salePrice;
        this.yearlyDividendAmount = yearlyDividendAmount;
        this.lastExDate = lastExDate;
        this.lastUpdated = lastUpdated;
        this.dividendFrequency = dividendFrequency;
        this.dividendYield = dividendYield;
    }

    public Position() {
        this.setId(UUID.randomUUID().toString());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.ticker);
        parcel.writeInt(this.numberOfShares);
        parcel.writeSerializable(this.purchasePrice);
        parcel.writeSerializable(this.lastKnownPrice);
        parcel.writeSerializable(this.purchaseDate);
        parcel.writeSerializable(this.saleDate);
        parcel.writeSerializable(this.yearlyDividendAmount);
        parcel.writeSerializable(this.lastExDate);
        parcel.writeSerializable(this.lastUpdated);
        parcel.writeList(this.dividends);
        parcel.writeInt(this.dividendFrequency);
        parcel.writeSerializable(this.dividendYield);
    }

    private Position (Parcel in) {
        this.id = in.readString();;
        this.ticker = in.readString();;
        this.numberOfShares = in.readInt();;
        this.purchasePrice = (BigDecimal) in.readSerializable();
        this.lastKnownPrice = (BigDecimal) in.readSerializable();
        this.purchaseDate = (Date) in.readSerializable();
        this.saleDate = (Date) in.readSerializable();
        this.salePrice = (BigDecimal) in.readSerializable();
        this.yearlyDividendAmount = (BigDecimal) in.readSerializable();
        this.lastExDate = (Date) in.readSerializable();
        this.lastUpdated = (Date) in.readSerializable();
        this.dividends = new ArrayList<Dividend>();
        in.readList(this.dividends, null);
        this.dividendFrequency = (Integer) in.readInt();
        this.dividendYield = (BigDecimal) in.readSerializable();
    }

    public static final Parcelable.Creator<Position> CREATOR = new
            Parcelable.Creator<Position>() {
                @Override
                public Position createFromParcel(Parcel parcel) {
                    return new Position(parcel);
                }
                @Override
                public Position[] newArray(int i) {
                    return new Position[i];
                }
            };


    public List<Dividend> getDividends() {
        return dividends;
    }

    public void setDividends(List<Dividend> dividends) {
        this.dividends = dividends;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(Integer numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getLastKnownPrice() {
        return lastKnownPrice;
    }

    public void setLastKnownPrice(BigDecimal lastKnownPrice) {
        this.lastKnownPrice = lastKnownPrice;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Date getLastExDate() {
        return lastExDate;
    }

    public void setLastExDate(Date lastExDate) {
        this.lastExDate = lastExDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getDividendFrequency() {
        return dividendFrequency;
    }

    public void setDividendFrequency(Integer dividendFrequency) {
        this.dividendFrequency = dividendFrequency;
    }

    public BigDecimal getYearlyDividendAmount() {
        return yearlyDividendAmount;
    }

    public void setYearlyDividendAmount(BigDecimal yearlyDividendAmount) {
        this.yearlyDividendAmount = yearlyDividendAmount;
    }

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }
}
