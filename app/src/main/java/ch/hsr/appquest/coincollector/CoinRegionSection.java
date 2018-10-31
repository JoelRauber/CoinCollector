package ch.hsr.appquest.coincollector;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class CoinRegionSection extends StatelessSection {

    private CoinRegion coinRegion;
    private String title;
    private Context context;

    CoinRegionSection(CoinRegion coinRegion, Context context) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.section_header)
                .build());
        this.coinRegion = coinRegion;
        this.title = coinRegion.getRegionName();
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() { return coinRegion.getCoinList().size(); }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new CoinItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerView.ViewHolder coinItemViewHolder =  holder;

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
