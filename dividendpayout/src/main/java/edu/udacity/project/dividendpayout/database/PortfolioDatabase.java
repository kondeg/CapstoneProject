package edu.udacity.project.dividendpayout.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Position.class, Dividend.class}, version = 1)
@TypeConverters({CurrencyConverter.class, DateConverter.class})
public abstract class PortfolioDatabase extends RoomDatabase {

    private static PortfolioDatabase INSTANCE;

    public static PortfolioDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (PortfolioDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            PortfolioDatabase.class, "dividendpayout")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }

    public abstract PortfolioDao portfolioDao();
}
