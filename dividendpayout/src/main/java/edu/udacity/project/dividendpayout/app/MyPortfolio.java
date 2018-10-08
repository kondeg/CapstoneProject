package edu.udacity.project.dividendpayout.app;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import edu.udacity.project.divdendpayout.R;

public class MyPortfolio extends AppCompatActivity implements PortfolioFragment.OnFragmentInteractionListener, CashFlowFragment.OnFragmentInteractionListener,
YearToDateFragment.OnFragmentInteractionListener{

    private Fragment myPortfolio;
    private Fragment cashFlow;
    private Fragment yearToDate;

    private final static String LOG_TAG = MyPortfolio.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_portfolio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.setOffscreenPageLimit(2);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (savedInstanceState != null) {
            myPortfolio = getSupportFragmentManager().getFragment(savedInstanceState, getString(R.string.myPortfolioTab));
            cashFlow = getSupportFragmentManager().getFragment(savedInstanceState, getString(R.string.cashFlowTab));
            yearToDate = getSupportFragmentManager().getFragment(savedInstanceState, getString(R.string.ytdTab));
        } else {
            myPortfolio = new PortfolioFragment();
            cashFlow = new CashFlowFragment();
            yearToDate = new YearToDateFragment();
        }

        adapter.addFragment(myPortfolio, getString(R.string.myPortfolioTab));
        adapter.addFragment(cashFlow, getString(R.string.cashFlowTab));
        adapter.addFragment(yearToDate, getString(R.string.ytdTab));
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        Intent intent = getIntent();
        if (savedInstanceState!=null) {
            TabLayout.Tab tab = tabLayout.getTabAt(savedInstanceState.getInt(getString(R.string.selectedTabIndex)));
            tab.select();
        }else if (intent!=null && intent.hasExtra(getString(R.string.selectedTabIndex))) {
            TabLayout.Tab tab = tabLayout.getTabAt(intent.getIntExtra(getString(R.string.selectedTabIndex),0));
            tab.select();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "activityOnSaveInstanceState");
        if(myPortfolio!=null) {
            getSupportFragmentManager().putFragment(savedInstanceState, getString(R.string.myPortfolioTab), myPortfolio);
        }
        if(cashFlow!=null) {
            getSupportFragmentManager().putFragment(savedInstanceState, getString(R.string.cashFlowTab), cashFlow);
        }
        if (yearToDate!=null) {
            getSupportFragmentManager().putFragment(savedInstanceState, getString(R.string.ytdTab), yearToDate);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout!=null) {
            savedInstanceState.putInt(getString(R.string.selectedTabIndex) , tabLayout.getSelectedTabPosition());
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_trade:
                Intent intent = new Intent(this, TradeActivity.class);
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                if (tabLayout!=null) {
                    intent.putExtra(getString(R.string.selectedTabIndex), tabLayout.getSelectedTabPosition());
                }
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
