package edu.udacity.project.dividendpayout;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import edu.udacity.project.dividendpayout.app.MyPortfolio;
import edu.udacity.project.dividendpayout.database.Dividend;
import edu.udacity.project.dividendpayout.database.PortfolioDao;
import edu.udacity.project.dividendpayout.database.PortfolioDatabase;
import edu.udacity.project.dividendpayout.database.Position;
import edu.udacity.project.dividendpayout.database.PositionWithDividend;

@RunWith(AndroidJUnit4.class)
public class RoomDatabaseTest {

    private PortfolioDao portfolioDao;
    private PortfolioDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, PortfolioDatabase.class).build();
        portfolioDao = mDb.portfolioDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }


    @Test
    public void writeUserAndReadInList() throws Exception {
        Position myPosition = new Position();
        myPosition.setId(UUID.randomUUID().toString());
        myPosition.setTicker("AAPL");
        myPosition.setNumberOfShares(20);
        myPosition.setPurchaseDate(new Date());
        myPosition.setPurchasePrice(new BigDecimal(200));

        Dividend div1 = new Dividend();
        div1.setId(UUID.randomUUID().toString());
        div1.setPositionId(myPosition.getId());
        div1.setNumberOfShares(20);
        div1.setDividendDate(new Date());
        div1.setDividendAmountPerShare(new BigDecimal(1));

        Dividend div2 = new Dividend();
        div2.setId(UUID.randomUUID().toString());
        div2.setPositionId(myPosition.getId());
        div2.setNumberOfShares(20);
        div2.setDividendDate(new Date());
        div2.setDividendAmountPerShare(new BigDecimal(1));

        portfolioDao.insertPosition(myPosition);
        portfolioDao.insertDividend(div1);
        portfolioDao.insertDividend(div2);

        LiveData<List<PositionWithDividend>> positionWithDividendList = portfolioDao.getAllPositions();

        List<PositionWithDividend> divList = positionWithDividendList.getValue();

        assert  divList.size()==1;
        assert  divList.get(0).getDividends().size()==2;
    }



}
