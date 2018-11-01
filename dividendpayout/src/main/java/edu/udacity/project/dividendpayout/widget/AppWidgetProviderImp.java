package edu.udacity.project.dividendpayout.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import edu.udacity.project.divdendpayout.R;
import edu.udacity.project.dividendpayout.database.DividendPaymentHistory;
import edu.udacity.project.dividendpayout.database.DividendSystem;
import edu.udacity.project.dividendpayout.database.PortfolioDatabase;
import edu.udacity.project.dividendpayout.database.PortfolioExecutor;
import edu.udacity.project.dividendpayout.database.PositionWithDividend;
import edu.udacity.project.dividendpayout.util.Endpoints;

public class AppWidgetProviderImp extends AppWidgetProvider {

    private final static String LOG_TAG = AppWidgetProviderImp.class.getSimpleName();

    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "On update");
        final int N = appWidgetIds.length;
        final PortfolioDatabase portfolioDatabase = PortfolioDatabase.getDatabase(context);
        PortfolioExecutor executor = new PortfolioExecutor();
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            final int appWidgetId = appWidgetIds[i];
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            executor.execute(new Runnable() {
                                 @Override
                                 public void run() {
                                     DividendSystem dow1 = portfolioDatabase.portfolioDao().getSettingByNameValue(Endpoints.dowSymbol+"prev_close");
                                     DividendSystem dow2 = portfolioDatabase.portfolioDao().getSettingByNameValue(Endpoints.dowSymbol+"data_value");
                                     Log.d(LOG_TAG, "Dow is null "+(dow1==null));
                                     Log.d(LOG_TAG, "Dow 2 is null "+(dow2==null));
                                     if (dow1!=null && dow2!=null) {
                                         Log.d(LOG_TAG, "Dow 1 value "+dow1.getSettingValue()+" Dow 2 value "+dow2.getSettingValue());
                                         Integer dif = Math.round(Float.valueOf(dow2.getSettingValue()) - (Float.valueOf(dow1.getSettingValue())));
                                         views.setTextViewText(R.id.widget_dow, context.getResources().getString(R.string.dowStr)+" "+dow2.getSettingValue()+(dif>=0?" (+"+dif+")":" (-"+dif+")"));
                                     }
                                     DividendSystem sp1 = portfolioDatabase.portfolioDao().getSettingByNameValue(Endpoints.spSymbol+"prev_close");
                                     DividendSystem sp2 = portfolioDatabase.portfolioDao().getSettingByNameValue(Endpoints.spSymbol+"data_value");
                                     Log.d(LOG_TAG, "sp is null "+(sp1==null));
                                     Log.d(LOG_TAG, "sp 2 is null "+(sp2==null));
                                     if (sp1!=null && sp2!=null) {
                                         Log.d(LOG_TAG, "SP 1 value "+sp1.getSettingValue()+" Dow 2 value "+sp2.getSettingValue());
                                         Integer dif = Math.round(Float.valueOf(sp2.getSettingValue()) - (Float.valueOf(sp1.getSettingValue())));
                                         views.setTextViewText(R.id.widget_sp, context.getResources().getString(R.string.sp)+" "+sp2.getSettingValue()+(dif>=0?" (+"+dif+")":" (-"+dif+")"));
                                     }
                                     DividendSystem ns1 = portfolioDatabase.portfolioDao().getSettingByNameValue(Endpoints.nasdaqSymbol+"prev_close");
                                     DividendSystem ns2 = portfolioDatabase.portfolioDao().getSettingByNameValue(Endpoints.nasdaqSymbol+"data_value");
                                     Log.d(LOG_TAG, "nasdaq is null "+(sp1==null));
                                     Log.d(LOG_TAG, "nasdaq is null "+(sp2==null));
                                     if (ns1!=null && ns2!=null) {
                                         Log.d(LOG_TAG, "Nasdaq 1 value "+ns1.getSettingValue()+" Nasdaq 2 value "+ns2.getSettingValue());
                                         Integer dif = Math.round(Float.valueOf(ns2.getSettingValue()) - (Float.valueOf(ns1.getSettingValue())));
                                         views.setTextViewText(R.id.widget_nasdaq, context.getResources().getString(R.string.nasdaq)+" "+ns2.getSettingValue()+(dif>=0?" (+"+dif+")":" (-"+dif+")"));
                                     }
                                     BigDecimal purchaseCost = new BigDecimal(0);
                                     BigDecimal marketCost = new BigDecimal(0);
                                     List<PositionWithDividend> list  =portfolioDatabase.portfolioDao().getAllActivePositionsWithDividendData();
                                     DividendPaymentHistory nextDividend = null;
                                     Date now = new Date();
                                     for (PositionWithDividend pos : list) {
                                         purchaseCost = purchaseCost.add(pos.getPosition().getPurchasePrice().multiply(new BigDecimal(pos.getPosition().getNumberOfShares())));
                                         marketCost = marketCost.add(pos.getPosition().getLastKnownPrice().multiply(new BigDecimal(pos.getPosition().getNumberOfShares())));
                                         Calendar next = Calendar.getInstance();
                                         for (DividendPaymentHistory his : pos.getPredictedDividendsForYear(next.get(Calendar.YEAR))) {
                                             if (nextDividend==null && his.getPaymentDate().after(now) ) {
                                                 nextDividend = his;
                                                 Log.d(LOG_TAG, "Next dividend "+nextDividend.getTicker()+ " "+nextDividend.getPaymentDate());
                                             } else if (nextDividend!=null && his.getPaymentDate().after(now) && his.getPaymentDate().before(nextDividend.getPaymentDate())) {
                                                 nextDividend = his;
                                                 Log.d(LOG_TAG, "Next dividend "+nextDividend.getTicker()+ " "+nextDividend.getPaymentDate());
                                             }
                                         }
                                     }
                                     if (marketCost!=null && purchaseCost!=null && purchaseCost.compareTo(new BigDecimal(0))>0) {
                                         BigDecimal percentage = (marketCost.subtract(purchaseCost).divide(purchaseCost).multiply(new BigDecimal(100))).setScale(2, RoundingMode.CEILING);
                                         BigDecimal gain = (marketCost.subtract(purchaseCost)).setScale(2, RoundingMode.CEILING);
                                         Log.d(LOG_TAG, "Gain "+gain+ " "+" Percent "+percentage);
                                         views.setTextViewText(R.id.widget_myportfolio, context.getResources().getString(R.string.myPortfolio)+" "+ NumberFormat.getCurrencyInstance().format(gain)+" ("+
                                         percentage+"%)");
                                     } else {
                                         Log.d(LOG_TAG, "Market cost or purchase cost are null");
                                     }
                                     if (nextDividend!=null) {
                                         views.setTextViewText(R.id.widget_next_div, context.getResources().getString(R.string.nextDividend)+" "+nextDividend.getTicker()+ " "+sdf.format(nextDividend.getPaymentDate())+ " "+
                                         NumberFormat.getCurrencyInstance().format(nextDividend.getDivAmount().multiply(new BigDecimal(nextDividend.getShares()).setScale(2, RoundingMode.CEILING))));
                                     } else {
                                         Log.d(LOG_TAG, "Next dividend is null");
                                     }
                                     appWidgetManager.updateAppWidget(appWidgetId, views);
                                 }
                             });
            // Tell the AppWidgetManager to perform an update on the current app widget
            //appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
