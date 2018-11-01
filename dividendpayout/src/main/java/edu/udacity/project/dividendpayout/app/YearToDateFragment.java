package edu.udacity.project.dividendpayout.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.util.Currency;
import java.util.Date;

import edu.udacity.project.divdendpayout.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YearToDateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YearToDateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearToDateFragment extends Fragment implements PortfolioFragment.OnGainSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BigDecimal totalGainLoss = new BigDecimal(0);
    private BigDecimal totalReceived = new BigDecimal(0);
    private BigDecimal totalProjected = new BigDecimal(0);

    private TextView ytdDividends;
    private TextView gainLoss;
    private TextView projectedDivs;
    private TextView ytdLabel;
    private AdView mAdView;
    private Tracker mTracker;

    private final static String LOG_TAG = YearToDateFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public YearToDateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YearToDateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YearToDateFragment newInstance(String param1, String param2) {
        YearToDateFragment fragment = new YearToDateFragment();
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
        View view = inflater.inflate(R.layout.fragment_year_to_date, container, false);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ytdDividends = (TextView) view.findViewById(R.id.ytdDivs);
        gainLoss = (TextView) view.findViewById(R.id.unrealizedGainLoss);
        projectedDivs= (TextView) view.findViewById(R.id.totalDivsPredicted);
        ytdLabel = (TextView) view.findViewById(R.id.yearToDateHeader);
        if (savedInstanceState!=null) {
            totalReceived = (BigDecimal) savedInstanceState.getSerializable(getString(R.string.ytdDividendsReceived));
            totalGainLoss = (BigDecimal) savedInstanceState.getSerializable(getString(R.string.ytdUnrealizedGainLoss));
            totalProjected = (BigDecimal) savedInstanceState.getSerializable(getString(R.string.ytdTotalDividendsForYear));
        }
        ytdDividends.setText(NumberFormat.getCurrencyInstance().format(totalReceived));
        ytdDividends.setContentDescription(NumberFormat.getCurrencyInstance().format(totalReceived));
        gainLoss.setText(NumberFormat.getCurrencyInstance().format(totalGainLoss));
        gainLoss.setContentDescription(NumberFormat.getCurrencyInstance().format(totalGainLoss));
        projectedDivs.setText(NumberFormat.getCurrencyInstance().format(totalProjected));
        projectedDivs.setContentDescription(NumberFormat.getCurrencyInstance().format(totalProjected));

        ytdLabel.setText(String.format(getString(R.string.ytdHeader), Integer.toString(Calendar.getInstance().get(Calendar.YEAR))));
        ytdLabel.setContentDescription(String.format(getString(R.string.ytdHeader), Integer.toString(Calendar.getInstance().get(Calendar.YEAR))));
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "save instance state fragment");
        savedInstanceState.putSerializable(getString(R.string.ytdDividendsReceived), totalReceived);
        savedInstanceState.putSerializable(getString(R.string.ytdUnrealizedGainLoss), totalGainLoss);
        savedInstanceState.putSerializable(getString(R.string.ytdTotalDividendsForYear), totalProjected);
        super.onSaveInstanceState(savedInstanceState);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(LOG_TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void setTotalGainLoss(BigDecimal gainLossD, BigDecimal dividendsReceived, BigDecimal projectedDividends) {
        this.totalGainLoss = gainLossD;
        this.totalReceived = dividendsReceived;
        this.totalProjected = projectedDividends;
        if (ytdDividends!=null) {
            ytdDividends.setText(NumberFormat.getCurrencyInstance().format(totalReceived));
        }
        if (gainLoss!=null) {
            gainLoss.setText(NumberFormat.getCurrencyInstance().format(totalGainLoss));
        }
        if (projectedDivs!=null) {
            projectedDivs.setText(NumberFormat.getCurrencyInstance().format(totalProjected));
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

}
