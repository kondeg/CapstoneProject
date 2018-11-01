package edu.udacity.project.dividendpayout.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import edu.udacity.project.divdendpayout.R;
import edu.udacity.project.dividendpayout.database.PortfolioEntry;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>{

    List<PortfolioEntry> entries;

    final Context mContext;

    private final static String LOG_TAG = PortfolioAdapter.class.getSimpleName();

    public PortfolioAdapter(List<PortfolioEntry> pEntries, Context context) {
        this.entries = pEntries;
        this.mContext = context;
    }

    @NonNull
    @Override
    public PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_cardview, parent, false);
        PortfolioViewHolder pvh = new PortfolioViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioViewHolder holder, final int position) {
        holder.ticker.setText(entries.get(position).getTicker());
        holder.ticker.setContentDescription(entries.get(position).getTicker());
        holder.shares.setText(Integer.toString(entries.get(position).getShares()));
        holder.shares.setContentDescription(Integer.toString(entries.get(position).getShares()));
        holder.portfolioPrice.setText(entries.get(position).getPortfolioPrice());
        holder.portfolioPrice.setContentDescription(entries.get(position).getPortfolioPrice());
        holder.portfolioYield.setText(entries.get(position).getPortfolioYield());
        holder.portfolioYield.setText(entries.get(position).getPortfolioYield());
        holder.portfolioAmount.setText(entries.get(position).getPortfolioAmount());
        holder.portfolioAmount.setContentDescription(entries.get(position).getPortfolioAmount());
        holder.portfolioGain.setText(entries.get(position).getPortfolioGain());
        holder.portfolioGain.setContentDescription(entries.get(position).getPortfolioGain());
        holder.cv.setContentDescription(String.format(mContext.getResources().getString(R.string.clickToTradeTicker), entries.get(position).getTicker()));
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,TradeActivity.class);
                intent.putExtra(mContext.getString(R.string.positionId), entries.get(position).getPositionId());
                intent.putExtra(mContext.getString(R.string.selectedTabIndex), ((MyPortfolio)mContext).getSelectedTabIndex());
                Log.d(LOG_TAG, entries.get(position).getPositionId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public class PortfolioViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView ticker;
        TextView shares;
        TextView portfolioPrice;
        TextView portfolioYield;
        TextView portfolioAmount;
        TextView portfolioGain;

        PortfolioViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvPortfolio);
            ticker = (TextView)itemView.findViewById(R.id.pTicker);
            shares = (TextView)itemView.findViewById(R.id.pShares);
            portfolioPrice = (TextView)itemView.findViewById(R.id.pPrice);
            portfolioYield = (TextView)itemView.findViewById(R.id.pYield);
            portfolioAmount = (TextView)itemView.findViewById(R.id.pAmount);
            portfolioGain = (TextView)itemView.findViewById(R.id.pGain);
        }
    }
}
