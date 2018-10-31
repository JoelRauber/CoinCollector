package ch.hsr.appquest.coincollector;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CoinRegionHeaderViewHolder extends RecyclerView.ViewHolder {

    private final TextView sectionTitleView;

    CoinRegionHeaderViewHolder(View view) {
        super(view);
        sectionTitleView = view.findViewById(R.id.sectionTitle);
    }

    public TextView getSectionTitleView() { return sectionTitleView; }

}
