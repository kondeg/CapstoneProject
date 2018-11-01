package edu.udacity.project.dividendpayout.app;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import edu.udacity.project.divdendpayout.R;
import edu.udacity.project.dividendpayout.database.Dividend;
import edu.udacity.project.dividendpayout.database.DividendPaymentHistory;
import edu.udacity.project.dividendpayout.database.DividendSystem;
import edu.udacity.project.dividendpayout.database.PortfolioDao;
import edu.udacity.project.dividendpayout.database.PortfolioDatabase;
import edu.udacity.project.dividendpayout.database.PortfolioEntry;
import edu.udacity.project.dividendpayout.database.PortfolioExecutor;
import edu.udacity.project.dividendpayout.database.Position;
import edu.udacity.project.dividendpayout.database.PositionWithDividend;
import edu.udacity.project.dividendpayout.util.Endpoints;
import edu.udacity.project.dividendpayout.util.NetworkCallUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PortfolioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PortfolioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PortfolioFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recList;
    private PortfolioExecutor executor = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private PortfolioDatabase pDatabase;

    private LiveData<List<PositionWithDividend>> portfolioEntryList;
    private LiveData<DividendSystem> lu;
    private ArrayList<PortfolioEntry> portfolioEntries;
    private RecyclerView.Adapter pReviewAdapter = null;
    private TextView lastUpdatedDate = null;
    private BigDecimal totalGainLoss = null;
    private BigDecimal totalDividendsReceived = new BigDecimal(0);
    private BigDecimal totalDividendsProjected = new BigDecimal(0);
    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
    Calendar current = Calendar.getInstance();
    Date firstOfYear = null;
    Date firstNextYear = null;
    SparseArray<BigDecimal> actualDividendMap = new SparseArray<>(12);
    SparseArray<BigDecimal> predictedDividendMap = new SparseArray<>(12);
    private final static String LOG_TAG = PortfolioFragment.class.getSimpleName();
    private AdView mAdView;
    private Tracker mTracker;


    public PortfolioFragment() {
        // Required empty public constructor
    }

    public interface OnGainSelectedListener{
        public void setTotalGainLoss(BigDecimal gainLoss, BigDecimal dividendsReceived, BigDecimal projectedDividends);
    }

    public interface OnCashFlowSelectedListener{
        public void setCashFlow(SparseArray<BigDecimal> actualCashFlow, SparseArray<BigDecimal> predictedCashFlow);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PortfolioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PortfolioFragment newInstance(String param1, String param2) {
        PortfolioFragment fragment = new PortfolioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        executor = new PortfolioExecutor();
        pDatabase = PortfolioDatabase.getDatabase(getActivity());
        portfolioEntries = new ArrayList<>();
        current.set(Calendar.DAY_OF_MONTH, 1);
        current.set(Calendar.MONTH, Calendar.JANUARY);
        firstOfYear = current.getTime();
        current.set(Calendar.YEAR, current.get(Calendar.YEAR)+1);
        firstNextYear = current.getTime();
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        MobileAds.initialize(getActivity(),getString(R.string.adUnitId));
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        recList = (RecyclerView) view.findViewById(R.id.cardListPortfolio);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        lastUpdatedDate = (TextView) view.findViewById(R.id.lastUpdatedView);
        lastUpdatedDate.setText(String.format(getString(R.string.lastUpdated), ""));
        if (savedInstanceState!=null && savedInstanceState.getParcelableArrayList(getString(R.string.portfolioList))!=null) {
            portfolioEntries = savedInstanceState.getParcelableArrayList(getString(R.string.portfolioList));
            lastUpdatedDate.setText(savedInstanceState.getString(getString(R.string.lastUpdated)));
            lastUpdatedDate.setContentDescription(savedInstanceState.getString(getString(R.string.lastUpdated)));
        } else {
            queryPositions();
        }
        pReviewAdapter = new PortfolioAdapter(portfolioEntries, getActivity());
        recList.setAdapter(pReviewAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(LOG_TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void queryPositions() {
        Log.d(LOG_TAG, "Query positions");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "Query positions run");
                NetworkCallUtil networkCallUtil = new NetworkCallUtil(pDatabase, getActivity(), executor);
                networkCallUtil.synchStockData();
                networkCallUtil.syncMarketData(Endpoints.dowSymbol);
                networkCallUtil.syncMarketData(Endpoints.spSymbol);
                networkCallUtil.syncMarketData(Endpoints.nasdaqSymbol);
                portfolioEntryList = pDatabase.portfolioDao().getAllActivePositionsWithDividendBetweenDates(firstOfYear, firstNextYear);

                portfolioEntryList.observe(getActivity(), new Observer<List<PositionWithDividend>>() {
                    @Override
                    public void onChanged(@Nullable List<PositionWithDividend> portfolioData) {
                        if (portfolioEntries == null) {
                            portfolioEntries = new ArrayList<PortfolioEntry>();
                        } else {
                            portfolioEntries.clear();
                        }
                        BigDecimal gain;
                        totalGainLoss = new BigDecimal(0);
                        totalDividendsReceived = new BigDecimal(0);
                        totalDividendsProjected = new BigDecimal(0);
                        int divMonth = 0;
                        Calendar cal = Calendar.getInstance();
                        int currentYear = cal.get(Calendar.YEAR);
                        BigDecimal monthDiv = null;
                        actualDividendMap.clear();
                        predictedDividendMap.clear();
                        if (portfolioData!=null) {
                            Log.d(LOG_TAG, "Found number of positions "+portfolioData.size());
                        }

                        for (PositionWithDividend pod : portfolioData) {
                            Log.d(LOG_TAG, "Position with dividend iterate "+pod.getPosition().getTicker()+" "+pod.getPosition().getPurchaseDate()+" "+pod.getPosition().getSaleDate());
                            if (pod.getPosition().getSaleDate()==null) {
                                PortfolioEntry pe = new PortfolioEntry();
                                pe.setTicker(pod.getPosition().getTicker());
                                pe.setShares(pod.getPosition().getNumberOfShares());
                                if (pod.getPosition().getDividendYield() != null) {
                                    pe.setPortfolioYield(pod.getPosition().getDividendYield().toString().concat("%"));
                                }
                                if (pod.getPosition().getLastKnownPrice() == null) {
                                    pe.setPortfolioPrice(NumberFormat.getCurrencyInstance().format(pod.getPosition().getPurchasePrice()));
                                } else {
                                    pe.setPortfolioPrice(NumberFormat.getCurrencyInstance().format(pod.getPosition().getLastKnownPrice()));
                                }
                                pe.setPositionId(pod.getPosition().getId());
                                if (pod.getPosition().getLastKnownPrice() != null && pod.getPosition().getNumberOfShares() != null) {
                                    pe.setPortfolioAmount(NumberFormat.getCurrencyInstance().format(pod.getPosition().getLastKnownPrice()
                                            .multiply(new BigDecimal(pod.getPosition().getNumberOfShares()))));
                                }
                                if (pod.getPosition().getPurchasePrice() != null && pod.getPosition().getNumberOfShares() != null && pod.getPosition().getLastKnownPrice() != null) {
                                    gain = (new BigDecimal(pod.getPosition().getNumberOfShares()).multiply(pod.getPosition().getLastKnownPrice())).subtract(
                                            new BigDecimal(pod.getPosition().getNumberOfShares()).multiply(pod.getPosition().getPurchasePrice()));
                                    Log.d(LOG_TAG, "Gain " + gain.toString());
                                    pe.setPortfolioGain(NumberFormat.getCurrencyInstance().format(gain));
                                    if (totalGainLoss == null) {
                                        totalGainLoss = new BigDecimal(0);
                                    }
                                    totalGainLoss = totalGainLoss.add(gain);
                                }
                                portfolioEntries.add(pe);
                            }

                            Log.d(LOG_TAG, "Dividends "+ pod.getPosition().getTicker()+" "+ (pod.getDividends()==null));
                            if (pod.getDividends()!=null) {
                                Log.d(LOG_TAG, "Dividends "+(pod.getDividends().size()));
                            }
                            for (Dividend div : pod.getDividends()) {
                                if (div.getDividendPaymentDate().after(firstOfYear) && div.getDividendPaymentDate().before(firstNextYear)) {
                                    Log.d(LOG_TAG, pod.getPosition().getTicker()+" Adding dividend "+div.getDividendAmountPerShare()+ " "+div.getNumberOfShares());
                                    totalDividendsReceived = totalDividendsReceived.add(div.getDividendAmountPerShare().multiply(new BigDecimal(div.getNumberOfShares())));
                                    cal.setTime(div.getDividendPaymentDate());
                                    divMonth = cal.get(Calendar.MONTH);
                                    monthDiv = div.getDividendAmountPerShare().multiply(new BigDecimal(div.getNumberOfShares()));
                                    Log.d(LOG_TAG, "Div month is "+Integer.toString(divMonth));
                                    if (actualDividendMap.get(divMonth)==null) {
                                        actualDividendMap.put(divMonth, monthDiv);
                                        Log.d(LOG_TAG, "Putting new div "+pod.getPosition().getTicker()+" "+divMonth+" "+monthDiv);
                                    } else {
                                        actualDividendMap.put(divMonth, actualDividendMap.get(divMonth).add(monthDiv));
                                        Log.d(LOG_TAG, "Incrementing div  "+pod.getPosition().getTicker()+" "+actualDividendMap.get(divMonth));
                                    }
                                }
                            }
                            for (DividendPaymentHistory predicted : pod.getPredictedDividendsForYear(currentYear)) {
                                if(predictedDividendMap.get(predicted.getMonth())==null) {
                                    predictedDividendMap.put(predicted.getMonth(), (predicted.getDivAmount().multiply(new BigDecimal(pod.getPosition().getNumberOfShares()))));
                                    totalDividendsProjected = totalDividendsProjected.add(predicted.getDivAmount().multiply(new BigDecimal(pod.getPosition().getNumberOfShares())));
                                    Log.d(LOG_TAG, "Putting predicted div "+predicted.getMonth()+" "+predicted.getDivAmount());
                                } else {
                                    predictedDividendMap.put(predicted.getMonth(), predictedDividendMap.get(predicted.getMonth()).add(predicted.getDivAmount().multiply(new BigDecimal(pod.getPosition().getNumberOfShares()))));
                                    totalDividendsProjected = totalDividendsProjected.add(predicted.getDivAmount().multiply(new BigDecimal(pod.getPosition().getNumberOfShares())));
                                    Log.d(LOG_TAG, "Incrementing predicted div  "+predicted.getMonth()+" "+predictedDividendMap.get(predicted.getMonth()));
                                }
                            }
                            Log.d(LOG_TAG, "Yearly dividend amount "+pod.getPosition().getYearlyDividendAmount());
                           //totalDividendsProjected = totalDividendsProjected.add((pod.getPosition().getYearlyDividendAmount()==null?new BigDecimal(0):pod.getPosition().getYearlyDividendAmount()).multiply(new BigDecimal(pod.getPosition().getNumberOfShares())));
                            ((OnGainSelectedListener)((MyPortfolio)getActivity()).getYearToDate()).setTotalGainLoss(totalGainLoss, totalDividendsReceived, totalDividendsProjected);
                            ((OnCashFlowSelectedListener)((MyPortfolio)getActivity()).getCashFlow()).setCashFlow(actualDividendMap, predictedDividendMap);
                        }
                        pReviewAdapter.notifyDataSetChanged();
                    }
                });
                lu = pDatabase.portfolioDao().getSettingByName(getString(R.string.lastUpdatedDateSetting));
                DividendSystem du = pDatabase.portfolioDao().getSettingByNameValue(getString(R.string.lastUpdatedDateSetting));
                if (du!=null) {
                    Message message = mHandler.obtainMessage(0, du.getSettingValue());
                    message.sendToTarget();
                }
                lu.observe(getActivity(), new Observer<DividendSystem>() {
                    @Override
                    public void onChanged(@Nullable DividendSystem lastUpdated) {
                         if (lastUpdated!=null) {
                             Message message = mHandler.obtainMessage(0, lastUpdated.getSettingValue());
                             message.sendToTarget();
                        }
                    }
                });
            }
        });
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String lastUpdated = (String) message.obj;
            Log.d(LOG_TAG, "Last updated "+lastUpdated);
            if (lastUpdated!=null) {
                lastUpdatedDate.setText(String.format(getString(R.string.lastUpdated), sdf.format(new Date(Long.valueOf(lastUpdated)))));
                lastUpdatedDate.setContentDescription(String.format(getString(R.string.lastUpdated), sdf.format(new Date(Long.valueOf(lastUpdated)))));
            } else {
                lastUpdatedDate.setText(String.format(getString(R.string.lastUpdated), ""));
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "save instance state fragment");
        savedInstanceState.putParcelableArrayList(getResources().getString(R.string.portfolioList),portfolioEntries);
        savedInstanceState.putString(getString(R.string.lastUpdated), lastUpdatedDate.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
