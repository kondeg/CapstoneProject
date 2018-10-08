package edu.udacity.project.dividendpayout.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PortfolioDao {

    @Query("SELECT * FROM Position")
    LiveData<List<PositionWithDividend>> getAllPositions();

    @Query("SELECT * FROM Position WHERE id = :id")
    PositionWithDividend getByPositionId(String id);

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


}
