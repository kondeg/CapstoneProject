package edu.udacity.project.dividendpayout.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = Position.class,
        parentColumns = "id",
        childColumns = "positionId"))
public class Dividend implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "positionId")
    String positionId;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "dividendDate")
    Date dividendDate;

    @TypeConverters(CurrencyConverter.class)
    @ColumnInfo(name = "dividendAmountPerShare")
    BigDecimal dividendAmountPerShare;

    @ColumnInfo(name = "numberOfShares")
    Integer numberOfShares;

    public Dividend(@NonNull String id, String positionId, Date dividendDate, BigDecimal dividendAmountPerShare, Integer numberOfShares) {
        this.id = id;
        this.positionId = positionId;
        this.dividendDate = dividendDate;
        this.dividendAmountPerShare = dividendAmountPerShare;
        this.numberOfShares = numberOfShares;
    }

    public Dividend(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.positionId);
        parcel.writeSerializable(this.dividendDate);
        parcel.writeSerializable(this.dividendAmountPerShare);
        parcel.writeSerializable(this.numberOfShares);
    }

    private Dividend (Parcel in) {
        this.id = in.readString();
        this.positionId = in.readString();
        this.dividendDate = (Date)in.readSerializable();
        this.dividendAmountPerShare = (BigDecimal)in.readSerializable();
        this.numberOfShares = in.readInt();
    }

    public static final Parcelable.Creator<Dividend> CREATOR = new
            Parcelable.Creator<Dividend>() {
                @Override
                public Dividend createFromParcel(Parcel parcel) {
                    return new Dividend(parcel);
                }
                @Override
                public Dividend[] newArray(int i) {
                    return new Dividend[i];
                }
            };

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

    public Date getDividendDate() {
        return dividendDate;
    }

    public void setDividendDate(Date dividendDate) {
        this.dividendDate = dividendDate;
    }

    public BigDecimal getDividendAmountPerShare() {
        return dividendAmountPerShare;
    }

    public void setDividendAmountPerShare(BigDecimal dividendAmountPerShare) {
        this.dividendAmountPerShare = dividendAmountPerShare;
    }

    public Integer getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(Integer numberOfShares) {
        this.numberOfShares = numberOfShares;
    }
}
