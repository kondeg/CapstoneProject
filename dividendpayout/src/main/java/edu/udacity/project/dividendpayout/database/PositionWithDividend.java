package edu.udacity.project.dividendpayout.database;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Relation;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.udacity.project.dividendpayout.app.PortfolioFragment;

public class PositionWithDividend {

    private final static String LOG_TAG = PositionWithDividend.class.getSimpleName();

    @Embedded
    Position position;

    @Relation(parentColumn =  "id", entityColumn = "positionId", entity = Dividend.class)
    List<Dividend> dividends;

    @Relation(parentColumn =  "id", entityColumn = "positionId", entity = DividendPaymentHistory.class)
    List<DividendPaymentHistory> lastYearsHistory;

    @Ignore
    BigDecimal lastYearsDividend = new BigDecimal(0);

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Dividend> getDividends() {
        return dividends;
    }

    public void setDividends(List<Dividend> dividends) {
        this.dividends = dividends;
    }

    public List<DividendPaymentHistory> getLastYearsHistory() {
        return lastYearsHistory;
    }

    public void setLastYearsHistory(List<DividendPaymentHistory> lastYearsHistory) {
        this.lastYearsHistory = lastYearsHistory;
    }


    private List<DividendPaymentHistory> getHistoryByYear(int year, Date saleDate) {
        List<DividendPaymentHistory> list = new ArrayList<>();
        int saleMonth = -1;
        int saleDayOfMonth = -1;
        int saleYear = -1;
        if (saleDate!=null) {
            Calendar c = Calendar.getInstance();
            c.setTime(saleDate);
            saleMonth = c.get(Calendar.MONTH);
            saleDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            saleYear = c.get(Calendar.YEAR);
            Log.d(LOG_TAG, "Sale month "+saleMonth+" sale day "+saleDayOfMonth + " sale year "+saleYear);
        }
        Log.d(LOG_TAG, "Last year history size "+lastYearsHistory.size());
        for (DividendPaymentHistory his : lastYearsHistory) {
            Log.d(LOG_TAG, "Historical dividend "+his.paymentDate.toString());
            if (his.getYear()==year) {
                Log.d(LOG_TAG, "Correct Year");
                if (saleMonth>0 && saleDayOfMonth>0) {
                    Log.d(LOG_TAG, "Position sold");
                    Calendar c = Calendar.getInstance();
                    c.setTime(his.getPaymentDate());
                    if(c.get(Calendar.MONTH)<saleMonth) {
                        Log.d(LOG_TAG, "Before sale month");
                        Log.d(LOG_TAG, "Adding sold dividend " + his.getDivAmount());
                        list.add(his);
                    } else if (c.get(Calendar.MONTH)==saleMonth && c.get(Calendar.DAY_OF_MONTH)<=saleDayOfMonth) {
                        Log.d(LOG_TAG, "Same sale month");
                        Log.d(LOG_TAG, "Adding sold dividend "+his.getDivAmount());
                        list.add(his);
                    }
                } else {
                    list.add(his);
                }
                lastYearsDividend = lastYearsDividend.add(his.getDivAmount());
            }
        }
        return list;
    }

    private BigDecimal getMultiplier() {
        if (position.getYearlyDividendAmount()!=null && position.getYearlyDividendAmount().longValue()>0 && lastYearsDividend!=null && lastYearsDividend.longValue()>0) {
            return position.getYearlyDividendAmount().divide(lastYearsDividend,2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(1);
    }

    public List<DividendPaymentHistory> getPredictedDividendsForYear(int year) {
        Log.d(LOG_TAG, getPosition().getTicker()+" Getting predicted dividends for year " +year+" "+(lastYearsHistory==null));
        if (lastYearsHistory!=null) {
            Log.d(LOG_TAG, "Dividend history size is  " +(lastYearsHistory.size()));
        }
        Calendar cal = Calendar.getInstance();
        List<DividendPaymentHistory> list = getHistoryByYear(year-1, position.getSaleDate());
        BigDecimal multiplier = getMultiplier();
        Log.d(LOG_TAG, "Multiplier "+multiplier);
        for (DividendPaymentHistory d : list) {
            cal.setTime(d.getPaymentDate());
            cal.add(Calendar.YEAR, 1);
            d.setPaymentDate(cal.getTime());
            d.setDivAmount(d.getDivAmount().multiply(multiplier));
            d.setTicker(position.getTicker());
            d.setShares(position.getNumberOfShares());
        }
        return list;
    }
}
