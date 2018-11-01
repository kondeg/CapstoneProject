package edu.udacity.project.dividendpayout.adapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import edu.udacity.project.dividendpayout.app.PortfolioAdapter;
import edu.udacity.project.dividendpayout.database.PortfolioDatabase;
import edu.udacity.project.dividendpayout.database.PortfolioExecutor;
import edu.udacity.project.dividendpayout.util.Endpoints;
import edu.udacity.project.dividendpayout.util.NetworkCallUtil;

public class StockSyncAdapter extends AbstractThreadedSyncAdapter{

    private final static String LOG_TAG = PortfolioAdapter.class.getSimpleName();

    ContentResolver mContentResolver;

    PortfolioDatabase pDatabase;

    NetworkCallUtil networkCallUtil;

    PortfolioExecutor executor;

    public StockSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.d(LOG_TAG, "New Stock Sync Adapter");
        mContentResolver = context.getContentResolver();
        pDatabase = PortfolioDatabase.getDatabase(context);
        executor = new PortfolioExecutor();
        networkCallUtil = new NetworkCallUtil(pDatabase, context, executor);
    }

    public StockSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        Log.d(LOG_TAG, "New Stock Sync Adapter");
        mContentResolver = context.getContentResolver();
        pDatabase = PortfolioDatabase.getDatabase(context);
        executor = new PortfolioExecutor();
        networkCallUtil = new NetworkCallUtil(pDatabase, context, executor);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "On Perform Sync");
        networkCallUtil.synchStockData();
        networkCallUtil.syncMarketData(Endpoints.dowSymbol);
        networkCallUtil.syncMarketData(Endpoints.spSymbol);
        networkCallUtil.syncMarketData(Endpoints.nasdaqSymbol);
    }
}
