package edu.udacity.project.dividendpayout.util;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import edu.udacity.project.divdendpayout.R;
import edu.udacity.project.dividendpayout.database.Dividend;
import edu.udacity.project.dividendpayout.database.DividendPaymentHistory;
import edu.udacity.project.dividendpayout.database.DividendSystem;
import edu.udacity.project.dividendpayout.database.PortfolioDatabase;
import edu.udacity.project.dividendpayout.database.PortfolioExecutor;
import edu.udacity.project.dividendpayout.database.PositionWithDividend;

public class NetworkCallUtil {

    private final static String LOG_TAG = NetworkCallUtil.class.getSimpleName();

    private PortfolioDatabase pDatabase;

    private Context mContext;

    private PortfolioExecutor portfolioExecutor;

    private final SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public NetworkCallUtil(PortfolioDatabase pDatabase, Context mContext, PortfolioExecutor executor) {
        this.pDatabase = pDatabase;
        this.mContext = mContext;
        this.portfolioExecutor = executor;
    }

    public void syncMarketData(final String index) {
        String apiUri = Endpoints.marketEndpoint+index;
        final JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject jsonObject) {
                portfolioExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String prevClose = jsonObject.getString("prev_close");
                            JSONArray jArray = jsonObject.getJSONArray("data_values");
                            JSONArray jsonArray2 = jArray.getJSONArray(jArray.length()-1);
                            String lastQuote = jsonArray2.getString(1);
                            Log.d(LOG_TAG, index+" prev close: "+prevClose+" last quote: "+lastQuote);
                            DividendSystem dividendSystemPclose = pDatabase.portfolioDao().getSettingByNameValue(index+"prev_close");
                            DividendSystem dividendSystemPprev  = pDatabase.portfolioDao().getSettingByNameValue(index+"data_value");
                            if (dividendSystemPclose==null) {
                                dividendSystemPclose = new DividendSystem(index+"prev_close", prevClose);
                                pDatabase.portfolioDao().insertDividendSystem(dividendSystemPclose);
                            } else {
                                dividendSystemPclose.setSettingValue(prevClose);
                                pDatabase.portfolioDao().updateDividendSystem(dividendSystemPclose);
                            }
                            if (dividendSystemPprev==null) {
                                dividendSystemPprev = new DividendSystem(index+"data_value", lastQuote);
                                pDatabase.portfolioDao().insertDividendSystem(dividendSystemPprev);
                            } else {
                                dividendSystemPprev.setSettingValue(lastQuote);
                                pDatabase.portfolioDao().updateDividendSystem(dividendSystemPprev);
                            }
                        } catch (Exception ex) {
                            Log.e(LOG_TAG, ex.getMessage());
                        }         }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i(LOG_TAG, error.toString());
            }
        });
        Volley.newRequestQueue(mContext).add(mJsonObjectRequest);
    }

    public void synchStockData() {
       final List<PositionWithDividend> positions = pDatabase.portfolioDao().getAllActivePositionsWithDividendData();
       Log.d(LOG_TAG, "Found positions "+Integer.toString(positions.size()));
        StringBuilder builder = new StringBuilder();
        for (PositionWithDividend p : positions)  {
            builder.append(p.getPosition().getTicker());
            builder.append(",");
        }
        if (builder.length()==0) {
            return;
        }
        String apiUri = Endpoints.combinedQuoteUrl+builder.toString()+Endpoints.getCombinedQuoteKey;
        Log.d(LOG_TAG, apiUri);
        final JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUri, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject jsonObject) {
                        portfolioExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String delayedPrice = null;
                                    String exDate = null;
                                    Date exDateDate = null;
                                    String paymentDate = null;
                                    Date paymentDateDate = null;
                                    String dividendAmount = null;
                                    BigDecimal dividentAmtamt = null;
                                    List<Dividend> existingDividendList = null;
                                    Date lastUpdatedDate = null;
                                    Dividend dividend = null;
                                    String dividendExDate = null;
                                    String annualDivAmt=null;
                                    String divYield = null;
                                    Integer frequency=0;
                                    int previousYear = Calendar.getInstance().get(Calendar.YEAR)-1;
                                    List<DividendPaymentHistory> divHistory = null;
                                    Calendar cal = Calendar.getInstance();

                                    Long delayedPriceTime = null;
                                    for (final PositionWithDividend p  : positions) {
                                        divHistory = pDatabase.portfolioDao().getDividendHistoryForYear(p.getPosition().getId(), previousYear);
                                        Log.d(LOG_TAG, jsonObject.toString());
                                        JSONObject comp = jsonObject.getJSONObject(p.getPosition().getTicker());
                                        JSONObject delayedQuote = comp.getJSONObject("delayed-quote");
                                        JSONObject stats = comp.getJSONObject("stats");
                                        Log.d(LOG_TAG, "STATS: "+ stats.toString());
                                        divYield = stats.getString("dividendYield");
                                        Log.d(LOG_TAG, "Yield: "+ divYield);
                                        dividendExDate = stats.getString("exDividendDate");
                                        annualDivAmt = stats.getString("dividendRate");
                                        Log.d(LOG_TAG, "Found ticker "+delayedQuote.getString("symbol")+" dividend amount  "+annualDivAmt);
                                        delayedPrice = delayedQuote.getString("delayedPrice");
                                        delayedPriceTime = delayedQuote.getLong("delayedPriceTime");
                                        Log.d(LOG_TAG, "Found ticker "+delayedQuote.getString("symbol")+" last price "+delayedPrice);
                                        p.getPosition().setLastKnownPrice(new BigDecimal(delayedPrice));
                                        lastUpdatedDate = new Date(delayedPriceTime);
                                        p.getPosition().setDividendYield(new BigDecimal(divYield));
                                        p.getPosition().setYearlyDividendAmount(new BigDecimal(annualDivAmt==null?"0":annualDivAmt));
                                        p.getPosition().setLastExDate(dividendExDate.equals("0")?null:sim.parse(dividendExDate));
                                        JSONArray jonj = comp.getJSONArray("dividends");
                                        Log.d(LOG_TAG, jonj.toString());
                                        for (int i = 0; i < jonj.length(); i++) {
                                            JSONObject div = jonj.getJSONObject(i);
                                            exDate = div.getString("exDate");
                                            exDateDate = sim.parse(exDate);
                                            paymentDate = div.getString("paymentDate");
                                            paymentDateDate = sim.parse(paymentDate);
                                            dividendAmount = div.getString("amount");
                                            dividentAmtamt = new BigDecimal(dividendAmount);
                                            Log.d(LOG_TAG,"Found dividend "+exDate+" "+paymentDate+" "+dividendAmount);
                                            Log.d(LOG_TAG,"Last updated date is "+p.getPosition().getLastUpdated());
                                            if (exDateDate.after(p.getPosition().getLastUpdated())) {
                                                dividend = new Dividend();
                                                dividend.setPositionId(p.getPosition().getId());
                                                dividend.setDividendPaymentDate(paymentDateDate);
                                                dividend.setDividendExDate(exDateDate);
                                                dividend.setDividendAmountPerShare(dividentAmtamt);
                                                dividend.setNumberOfShares(p.getPosition().getNumberOfShares());
                                                pDatabase.portfolioDao().insertDividend(dividend);
                                                Log.d(LOG_TAG, "Inserting "+exDate+" "+paymentDate+" "+dividendAmount);
                                            }
                                            frequency++;
                                            Log.d(LOG_TAG, "Dividend history is null "+(divHistory==null));
                                            if(divHistory == null || divHistory.size()==0) {
                                                cal.setTime(paymentDateDate);
                                                if (cal.get(Calendar.YEAR)==previousYear) {
                                                    Log.d(LOG_TAG, "Adding div history "+previousYear+ " "+paymentDateDate+" "+dividentAmtamt);
                                                    DividendPaymentHistory history = new DividendPaymentHistory(p.getPosition().getId(), previousYear, paymentDateDate, dividentAmtamt);
                                                    pDatabase.portfolioDao().insertDividendHistory(history);
                                                }
                                            } else {
                                                Log.d(LOG_TAG, "Dividend history is null "+divHistory.size());
                                            }
                                        }
                                        if (frequency!=null && frequency>0) {
                                            Log.d(LOG_TAG, "Freguency "+12/frequency);
                                            p.getPosition().setDividendFrequency(12/frequency);
                                        }
                                        p.getPosition().setLastUpdated(lastUpdatedDate);
                                        pDatabase.portfolioDao().updatePosition(p.getPosition());


                                }
                                if (delayedPriceTime!=null) {
                                    DividendSystem update = pDatabase.portfolioDao().getSettingByNameValue(mContext.getString(R.string.lastUpdatedDateSetting));
                                    if (update == null) {
                                        update = new DividendSystem(mContext.getString(R.string.lastUpdatedDateSetting), delayedPriceTime.toString());
                                        pDatabase.portfolioDao().insertDividendSystem(update);
                                    } else {
                                        update.setSettingValue(delayedPriceTime.toString());
                                        pDatabase.portfolioDao().updateDividendSystem(update);
                                    }
                                }
                        } catch (JSONException ex) {
                            Log.e(LOG_TAG, ex.getMessage());
                        }  catch (ParseException e) {
                                    Log.e(LOG_TAG, e.getMessage());
                                    e.printStackTrace();
                        }            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.i(LOG_TAG, error.toString());
                    }
                });

        // Queue the async request
        Volley.newRequestQueue(mContext).add(mJsonObjectRequest);
    }

}
