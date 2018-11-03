package edu.udacity.project.dividendpayout.app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import edu.udacity.project.divdendpayout.R;
import edu.udacity.project.dividendpayout.database.PortfolioDatabase;
import edu.udacity.project.dividendpayout.database.PortfolioExecutor;
import edu.udacity.project.dividendpayout.database.Position;
import edu.udacity.project.dividendpayout.util.Endpoints;
import edu.udacity.project.dividendpayout.util.NumberTextWatcher;
import edu.udacity.project.dividendpayout.util.ValidationUtil;

public class TradeActivity extends AppCompatActivity {

    int selectedTabIndex;
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar2 = Calendar.getInstance();
    EditText purchaseDate;
    EditText saleDate;
    EditText purchasePrice;
    EditText salePrice;
    EditText numberOfShares;
    EditText ticker;
    Button saveButton;
    String dateFormat = "MM/dd/yyyy";
    Position tradePosition;
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener dateSold;
    Context context = this;
    private PortfolioDatabase mDatabase;
    private Executor executor = null;
    String tradeId;
    boolean update = false;
    private Tracker mTracker;


    private final static String LOG_TAG = TradeActivity.class.getSimpleName();

    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"onCreate");
        setContentView(R.layout.activity_trade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tradeId = getIntent().getExtras().getString(getString(R.string.positionId));
        mDatabase = PortfolioDatabase.getDatabase(context);
        executor = new PortfolioExecutor();
        Intent intent = getIntent();
        if (intent!=null && intent.hasExtra(getString(R.string.selectedTabIndex))){
            selectedTabIndex = intent.getIntExtra(getString(R.string.selectedTabIndex), 0);
        }
        purchaseDate= (EditText) findViewById(R.id.purchaseDate);
        saleDate= (EditText) findViewById(R.id.saleDate);
        purchasePrice = (EditText) findViewById(R.id.purchasePrice);
        salePrice = (EditText) findViewById(R.id.salePrice);
        numberOfShares = (EditText) findViewById(R.id.numberOfShares);
        saveButton = (Button) findViewById(R.id.saveButton);
        ticker = findViewById(R.id.ticker_in);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkTrade(tradePosition);
            }
        });
        purchasePrice.addTextChangedListener(new NumberTextWatcher(purchasePrice, "#,###"));
        salePrice.addTextChangedListener(new NumberTextWatcher(salePrice, "#,###"));
        purchaseDate.setOnKeyListener(null);
        saleDate.setOnKeyListener(null);
        dateSold = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateSold();
            }
        };
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDatePurchased();
            }
        };
        purchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        saleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, dateSold, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    Handler mHandler2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String m = (String)message.obj;
            if (m != null && m.matches("[-+]?\\d*\\.?\\d+")) {
                if (tradePosition!=null) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            tradePosition.setLastUpdated(tradePosition.getPurchaseDate());
                            if (!update) {
                                Log.d(LOG_TAG, "Inserting "+tradePosition.getId());
                                mDatabase.portfolioDao().insertPosition(tradePosition);
                            } else {
                                Log.d(LOG_TAG, "Updating "+tradePosition.getId());
                                mDatabase.portfolioDao().updatePosition(tradePosition);
                            }
                            Intent intent = new Intent(context, MyPortfolio.class);
                            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                            if (tabLayout!=null) {
                                intent.putExtra(getString(R.string.selectedTabIndex), selectedTabIndex);
                            }
                            context.startActivity(intent);
                        }
                    });}
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.tradeSaved))
                        .setAction(tradePosition.getTicker())
                        .build());
                Toast.makeText(context, getString(R.string.tradeSaved), Toast.LENGTH_SHORT).show();
            } else {
                ticker.requestFocus();
                ticker.setError(getString(R.string.tickerError),null);
            }
        }
    };

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if (update && tradePosition.getPurchaseDate()!=null) {
                myCalendar.setTime(tradePosition.getPurchaseDate());
                purchaseDate.setText(sdf.format(tradePosition.getPurchaseDate()));
            }
            if (update && tradePosition.getSaleDate()!=null) {
                myCalendar2.setTime(tradePosition.getSaleDate());
                saleDate.setText(sdf.format(tradePosition.getSaleDate()));
            }
            if (update && tradePosition.getPurchasePrice()!=null) {
                purchasePrice.setText(NumberFormat.getCurrencyInstance(Locale.US).format(tradePosition.getPurchasePrice()));
            }
            if (update && tradePosition.getSalePrice()!=null) {
                salePrice.setText(NumberFormat.getInstance().format(tradePosition.getSalePrice()));
            }
            if (update && tradePosition.getNumberOfShares()!=null) {
                numberOfShares.setText(Integer.toString(tradePosition.getNumberOfShares()));
            }
            if (update && tradePosition.getTicker()!=null) {
                ticker.setText(tradePosition.getTicker());
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG,"onStart");
        if (tradeId==null) {
            tradePosition = new Position();
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "reading "+tradeId);
                    tradePosition = mDatabase.portfolioDao().getByPositionId(tradeId).getPosition();
                    Log.d(LOG_TAG, "reading "+tradePosition.getTicker());
                    update = true;
                    Message message = mHandler.obtainMessage(0, null);
                    message.sendToTarget();
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(LOG_TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void updateDatePurchased() {
        purchaseDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateDateSold() {
        saleDate.setText(sdf.format(myCalendar2.getTime()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MyPortfolio.class);
        intent.putExtra(getString(R.string.selectedTabIndex), selectedTabIndex);
        this.startActivity(intent);
        return super.onOptionsItemSelected(item);
    }


    public Position checkTrade(Position position) {
        purchaseDate.setError(null);
        purchasePrice.setError(null);
        saleDate.setError(null);
        salePrice.setError(null);
        ticker.setError(null);
        numberOfShares.setError(null);
        Date dPurchase = null;
        Date dSale = null;
        BigDecimal pPrice = ValidationUtil.convertCurrencyToDecimal(purchasePrice.getText().toString());
        BigDecimal sPrice = ValidationUtil.convertCurrencyToDecimal(salePrice.getText().toString());
        Integer numShares = ValidationUtil.convertTextToIntenger(numberOfShares);
        String tick = ticker.getText().toString();
        if (pPrice==null || pPrice.compareTo(new BigDecimal(0))<=0) {
            purchasePrice.requestFocus();
            purchasePrice.setError(getString(R.string.emptyPurchasePriceError), null);
            return null;
        }
        position.setPurchasePrice(pPrice);

        try {
            dPurchase=ValidationUtil.convertTextToDate(purchaseDate);
        } catch (ParseException e) {
            purchaseDate.requestFocus();
            purchaseDate.setError(getString(R.string.dateFormatError));
        }

        if (dPurchase==null) {
            Log.d(LOG_TAG, "DNull");
            purchaseDate.setFocusable(true);
            purchaseDate.requestFocus();
            purchaseDate.setError(getString(R.string.emptyPurchaseDateError));
            return null;
        } else if(ValidationUtil.isInTheFuture(dPurchase)) {
            purchaseDate.setFocusable(true);
            purchaseDate.requestFocus();
            purchaseDate.setError(getString(R.string.futurePurchaseDateError), null);
            return null;
        }
        position.setPurchaseDate(dPurchase);


        if (numShares==null || numShares<=0) {
                numberOfShares.requestFocus();
                numberOfShares.setError(getString(R.string.numberOfSharesError), null);
                return null;
        }
        position.setNumberOfShares(numShares);
        try {
            dSale=ValidationUtil.convertTextToDate(saleDate);
        } catch (ParseException e) {
            saleDate.requestFocus();
            saleDate.setError(getString(R.string.dateFormatError),null);
        }
        if (dSale!=null || sPrice!=null) {
                if (dSale==null) {
                    saleDate.requestFocus();
                    saleDate.setError(getString(R.string.emptySaleDateError), null);
                    return null;
                } else if(ValidationUtil.isInTheFuture(dSale)) {
                    saleDate.requestFocus();
                    saleDate.setError(getString(R.string.emptySaleDateError), null);
                    return null;
                } else if (dPurchase.after(dSale)) {
                    saleDate.requestFocus();
                    saleDate.setError(getString(R.string.saleDateBeforePurchaseDateError),null);
                    return null;
                }
                position.setSaleDate(dSale);
                if (sPrice==null || sPrice.compareTo(new BigDecimal(0))<=0) {
                    salePrice.requestFocus();
                    salePrice.setError(getString(R.string.emptySalePriceError), null);
                    return null;
                }
        }
        checkTicker(tick);
        position.setTicker(tick);
        return position;
        }



    public void checkTicker(String ticker) {
        String apiUri = Endpoints.priceQuoteUrl + ticker.toString() + Endpoints.priceQuoteUrlKey;
        Log.d(LOG_TAG, apiUri);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Message message = mHandler2.obtainMessage(0, response);
                        message.sendToTarget();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(LOG_TAG, error.toString());
                Message message = mHandler2.obtainMessage(0, null);
                message.sendToTarget();
            }
        });

        // Queue the async request
        Volley.newRequestQueue(this).add(stringRequest);
    }


}
