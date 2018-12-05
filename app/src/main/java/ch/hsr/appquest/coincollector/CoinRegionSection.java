package ch.hsr.appquest.coincollector;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class CoinRegionSection extends StatelessSection {

    private CoinRegion coinRegion;
    private String title;
    private Context context;
    private CoinManager coinManager;

    CoinRegionSection(CoinRegion coinRegion, Context context, CoinManager coinManager) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.section_header)
                .build());
        this.coinRegion = coinRegion;
        this.title = coinRegion.getRegionName();
        this.context = context;
        this.coinManager = coinManager;
    }

    @Override
    public int getContentItemsTotal() { return coinRegion.getCoinList().size(); }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new CoinItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        CoinItemViewHolder coinItemViewHolder = (CoinItemViewHolder) holder;

        List<Coin> coins = coinRegion.getCoinList();

        while(coins.iterator().hasNext()){
            Coin coin = coins.iterator().next();
        }

        /**for(Coin coin : coins){
            if(coin.getMajor() <= 5 && coin.getMinor() != 0) coinItemViewHolder.getCoinImageView().setImageResource(R.drawable.lakeside_coin);
            else if(coin.getMajor() > 5 && coin.getMajor() <= 8 && coin.getMinor() != 0) coinItemViewHolder.getCoinImageView().setImageResource(R.drawable.island_coin);
            else if(coin.getMajor() > 8 && coin.getMajor() <= 11 && coin.getMinor() != 0) coinItemViewHolder.getCoinImageView().setImageResource(R.drawable.cafeteria_coin);
            else if(coin.getMajor() > 11 && coin.getMajor() <= 13 && coin.getMinor() != 0) coinItemViewHolder.getCoinImageView().setImageResource(R.drawable.bicyclestand_coin);
            else if(coin.getMajor() > 13 && coin.getMajor() <= 15 && coin.getMinor() != 0) coinItemViewHolder.getCoinImageView().setImageResource(R.drawable.researchbuilding_coin);
            else if(coin.getMajor() == 0 && coin.getMinor() != 0) coinItemViewHolder.getCoinImageView().setImageResource(R.drawable.sample_coin);
         }*/
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new CoinRegionHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        CoinRegionHeaderViewHolder headerHolder = (CoinRegionHeaderViewHolder) holder;
        headerHolder.getSectionTitleView().setText(title);
    }

}
