package edu.udacity.project.dividendpayout.database;

import android.os.Parcel;
import android.os.Parcelable;

public class PortfolioEntry implements Parcelable {

    private String ticker;

    private Integer shares;

    private String portfolioPrice;

    private String portfolioYield;

    private String portfolioAmount;

    private String portfolioGain;

    String positionId;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public String getPortfolioPrice() {
        return portfolioPrice;
    }

    public void setPortfolioPrice(String portfolioPrice) {
        this.portfolioPrice = portfolioPrice;
    }

    public String getPortfolioYield() {
        return portfolioYield;
    }

    public void setPortfolioYield(String portfolioYield) {
        this.portfolioYield = portfolioYield;
    }

    public String getPortfolioAmount() {
        return portfolioAmount;
    }

    public void setPortfolioAmount(String portfolioAmount) {
        this.portfolioAmount = portfolioAmount;
    }

    public String getPortfolioGain() {
        return portfolioGain;
    }

    public void setPortfolioGain(String portfolioGain) {
        this.portfolioGain = portfolioGain;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public PortfolioEntry() {}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ticker);
        dest.writeInt(shares);
        dest.writeString(portfolioPrice);
        dest.writeString(portfolioYield);
        dest.writeString(portfolioAmount);
        dest.writeString(portfolioGain);
        dest.writeString(positionId);
    }

    public PortfolioEntry(Parcel in) {
        ticker = in.readString();
        shares = in.readInt();
        portfolioPrice = in.readString();
        portfolioYield = in.readString();
        portfolioAmount = in.readString();
        portfolioGain = in.readString();
        positionId = in.readString();
    }

    public static Creator<PortfolioEntry> CREATOR = new Creator<PortfolioEntry>() {

        @Override
        public PortfolioEntry createFromParcel(Parcel source) {
            return new PortfolioEntry(source);
        }

        @Override
        public PortfolioEntry[] newArray(int size) {
            return new PortfolioEntry[size];
        }
    };
}
