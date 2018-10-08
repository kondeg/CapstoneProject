package edu.udacity.project.dividendpayout.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import edu.udacity.project.divdendpayout.R;

public class TradeActivity extends AppCompatActivity {

    int selectedTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        selectedTabIndex = getIntent().getExtras().getInt(getString(R.string.selectedTabIndex));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MyPortfolio.class);
        intent.putExtra(getString(R.string.selectedTabIndex), selectedTabIndex);
        this.startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

}
