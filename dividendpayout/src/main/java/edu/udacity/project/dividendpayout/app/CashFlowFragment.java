package edu.udacity.project.dividendpayout.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Calendar;

import edu.udacity.project.divdendpayout.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CashFlowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CashFlowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CashFlowFragment extends Fragment implements PortfolioFragment.OnCashFlowSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final static String LOG_TAG = CashFlowFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    SparseArray<BigDecimal> actualDividendMap = new SparseArray<>(12);
    SparseArray<TextView> actualDividendMapTextView = new SparseArray<>(12);

    SparseArray<BigDecimal> predDividendMap = new SparseArray<>(12);
    SparseArray<TextView> predDividendMapTextView = new SparseArray<>(12);

    private final String def = NumberFormat.getCurrencyInstance().format(0);

    private TextView cashFlowLabel;

    private TextView actualJan;
    private TextView actualFeb;
    private TextView actualMar;
    private TextView actualApr;
    private TextView actualMay;
    private TextView actualJun;
    private TextView actualJul;
    private TextView actualAug;
    private TextView actualSep;
    private TextView actualOct;
    private TextView actualNov;
    private TextView actualDec;

    private TextView predJan;
    private TextView predFeb;
    private TextView predMar;
    private TextView predApr;
    private TextView predMay;
    private TextView predJun;
    private TextView predJul;
    private TextView predAug;
    private TextView predSep;
    private TextView predOct;
    private TextView predNov;
    private TextView predDec;

    private AdView mAdView;
    private Tracker mTracker;

    public CashFlowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CashFlowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CashFlowFragment newInstance(String param1, String param2) {
        CashFlowFragment fragment = new CashFlowFragment();
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
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cash_flow, container, false);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        cashFlowLabel = view.findViewById(R.id.cashFlowHeader);
        cashFlowLabel.setText(String.format(getString(R.string.cashFlowHeader), Integer.toString(Calendar.getInstance().get(Calendar.YEAR))));
        cashFlowLabel.setContentDescription(String.format(getString(R.string.cashFlowHeader), Integer.toString(Calendar.getInstance().get(Calendar.YEAR))));
        initializeActualTextView(view);
        initializePredTextView(view);
        if (savedInstanceState!=null) {
            for(int i = 0; i<12; i++) {
                if(savedInstanceState.get(getString(R.string.ytdActualDividends)+i)!=null) {
                    actualDividendMap.put(i, (BigDecimal)savedInstanceState.get(getString(R.string.ytdActualDividends)+i));
                    actualDividendMapTextView.get(i).setText(NumberFormat.getCurrencyInstance().format((BigDecimal)savedInstanceState.get(getString(R.string.ytdActualDividends)+i)));
                }
                if(savedInstanceState.get(getString(R.string.ytdPredDividends)+i)!=null) {
                    predDividendMap.put(i, (BigDecimal)savedInstanceState.get(getString(R.string.ytdPredDividends)+i));
                    predDividendMapTextView.get(i).setText(NumberFormat.getCurrencyInstance().format((BigDecimal)savedInstanceState.get(getString(R.string.ytdPredDividends)+i)));
                }
            }
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setCashFlow(SparseArray<BigDecimal> actualCashFlow, SparseArray<BigDecimal> predictedCashFlow) {
        Log.d(LOG_TAG, "setCashFlow");
        actualDividendMap = actualCashFlow;
        predDividendMap = predictedCashFlow;
        Log.d(LOG_TAG,"Actual Dividend map "+Integer.toString(actualCashFlow.size()));
        for(int i = 0; i<12; i++) {
            if (actualDividendMap.get(i)!=null) {
                Log.d(LOG_TAG,"Found dividend actual index "+i);
                actualDividendMapTextView.get(i).setText(NumberFormat.getCurrencyInstance().format(actualDividendMap.get(i)));
                actualDividendMapTextView.get(i).setContentDescription(NumberFormat.getCurrencyInstance().format(actualDividendMap.get(i)));
            }
            if (predDividendMap.get(i)!=null) {
                Log.d(LOG_TAG,"Found dividend predicted index "+i);
                predDividendMapTextView.get(i).setText(NumberFormat.getCurrencyInstance().format(predDividendMap.get(i)));
                predDividendMapTextView.get(i).setContentDescription(NumberFormat.getCurrencyInstance().format(predDividendMap.get(i)));
            }
        }
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "save instance state fragment");
        for(int i = 0; i<12; i++) {
            if (actualDividendMap.get(i)!=null) {
                savedInstanceState.putSerializable(getString(R.string.ytdActualDividends)+i, actualDividendMap.get(i));
            }
            if (predDividendMap.get(i)!=null) {
                savedInstanceState.putSerializable(getString(R.string.ytdPredDividends)+i, predDividendMap.get(i));
            }
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initializeActualTextView(View view) {
        actualJan = view.findViewById(R.id.actJanuary);
        actualJan.setText(def);
        actualDividendMapTextView.put(0, actualJan);
        actualFeb = view.findViewById(R.id.actFebruary);
        actualFeb.setText(def);
        actualDividendMapTextView.put(1, actualFeb);
        actualMar = view.findViewById(R.id.actMarch);
        actualMar.setText(def);
        actualDividendMapTextView.put(2, actualMar);
        actualApr = view.findViewById(R.id.actApril);
        actualApr.setText(def);
        actualDividendMapTextView.put(3, actualApr);
        actualMay = view.findViewById(R.id.actMay);
        actualMay.setText(def);
        actualDividendMapTextView.put(4, actualMay);
        actualJun = view.findViewById(R.id.actJune);
        actualJun.setText(def);
        actualDividendMapTextView.put(5, actualJun);
        actualJul = view.findViewById(R.id.actJuly);
        actualJul.setText(def);
        actualDividendMapTextView.put(6, actualJul);
        actualAug = view.findViewById(R.id.actAugust);
        actualAug.setText(def);
        actualDividendMapTextView.put(7, actualAug);
        actualSep = view.findViewById(R.id.actSept);
        actualSep.setText(def);
        actualDividendMapTextView.put(8, actualSep);
        actualOct = view.findViewById(R.id.actOctober);
        actualOct.setText(def);
        actualDividendMapTextView.put(9, actualOct);
        actualNov = view.findViewById(R.id.actNovember);
        actualNov.setText(def);
        actualDividendMapTextView.put(10, actualNov);
        actualDec = view.findViewById(R.id.actDecember);
        actualDec.setText(def);
        actualDividendMapTextView.put(11, actualDec);
    }


    private void initializePredTextView(View view) {
        predJan = view.findViewById(R.id.predJanuary);
        predJan.setText(def);
        predDividendMapTextView.put(0, predJan);
        predFeb = view.findViewById(R.id.predFebruary);
        predFeb.setText(def);
        predDividendMapTextView.put(1, predFeb);
        predMar = view.findViewById(R.id.predMarch);
        predMar.setText(def);
        predDividendMapTextView.put(2, predMar);
        predApr = view.findViewById(R.id.predApril);
        predApr.setText(def);
        predDividendMapTextView.put(3, predApr);
        predMay = view.findViewById(R.id.predMay);
        predMay.setText(def);
        predDividendMapTextView.put(4, predMay);
        predJun = view.findViewById(R.id.predJune);
        predJun.setText(def);
        predDividendMapTextView.put(5, predJun);
        predJul = view.findViewById(R.id.predJuly);
        predJul.setText(def);
        predDividendMapTextView.put(6, predJul);
        predAug = view.findViewById(R.id.predAugust);
        predAug.setText(def);
        predDividendMapTextView.put(7, predAug);
        predSep = view.findViewById(R.id.predSept);
        predSep.setText(def);
        predDividendMapTextView.put(8, predSep);
        predOct = view.findViewById(R.id.predOctober);
        predOct.setText(def);
        predDividendMapTextView.put(9, predOct);
        predNov = view.findViewById(R.id.predNovember);
        predNov.setText(def);
        predDividendMapTextView.put(10, predNov);
        predDec = view.findViewById(R.id.predDecember);
        predDec.setText(def);
        predDividendMapTextView.put(11, predDec);
    }
}
