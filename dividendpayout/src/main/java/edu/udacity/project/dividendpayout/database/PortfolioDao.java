package edu.udacity.project.dividendpayout.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface PortfolioDao {

    @Query("SELECT * FROM Position")
    LiveData<List<Position>> getAllPositions();

    @Query("SELECT * FROM Position")
    LiveData<List<PositionWithDividend>> getAllPositionsWithDividend();

    @Query("SELECT * FROM Position where saleDate is null and salePrice is null order by ticker desc")
    LiveData<List<PositionWithDividend>> getAllActivePositionsWithDividend();

    @Query("SELECT * FROM Position where (saleDate is null and salePrice is null) or (saleDate>=:startDate and saleDate<:endDate) order by ticker desc")
    LiveData<List<PositionWithDividend>> getAllActivePositionsWithDividendBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM Position where saleDate is null and salePrice is null order by ticker desc")
    List<PositionWithDividend> getAllActivePositionsWithDividendData();

    @Query("SELECT * FROM Position WHERE id = :id")
    PositionWithDividend getByPositionId(String id);

    @Query("SELECT * FROM Position WHERE ticker = :ticker")
    PositionWithDividend getByTicker(String ticker);

    @Query("SELECT * FROM Dividend WHERE positionId = :positionId and dividendExDate<=:lastUpdatedDate")
    List<Dividend> getDividendsForPositionId(String positionId, Long lastUpdatedDate);

    @Query("DELETE FROM Dividend WHERE positionId = :positionId")
    public void deleteDividendsForPosition(String positionId);

    @Query("SELECT * FROM DividendPaymentHistory WHERE positionId = :positionId and year=:year")
    public List<DividendPaymentHistory> getDividendHistoryForYear(String positionId, Integer year);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDividendHistory(DividendPaymentHistory history);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPosition(Position position);

    @Delete
    void deletePosition(Position position);

    @Update
    void updatePosition(Position position);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDividend(Dividend dividend);

    @Delete
    void deleteDividend(Dividend position);

    @Update
    void updateDividend(Dividend position);

    @Query("SELECT * FROM DividendSystem")
    LiveData<List<DividendSystem>> getAllSystemSettings();

    @Update
    void updateDividendSystem(DividendSystem divSystem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDividendSystem(DividendSystem dividendSystem);

    @Delete
    void deleteDividendSystem(DividendSystem dividendSystem);

    @Query("SELECT * FROM DividendSystem WHERE settingName = :settingName")
    LiveData<DividendSystem> getSettingByName(String settingName);

    @Query("SELECT * FROM DividendSystem WHERE settingName = :settingName")
    DividendSystem getSettingByNameValue(String settingName);
}
