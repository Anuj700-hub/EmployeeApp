package com.hungerbox.customer.order.adapter.viewholder;

import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.hungerbox.customer.R;
import com.rd.PageIndicatorView;

/**
 * Created by manas on 3/5/17.
 */

public class RecommendedListViewHolder extends RecyclerView.ViewHolder {


    public final ViewPager vpRecommendations;
    public final RelativeLayout viewPagerBox;
    public final PageIndicatorView pageIndicatorView;

    public RecommendedListViewHolder(View itemView) {
        super(itemView);
        vpRecommendations = itemView.findViewById(R.id.vp_recommendations);
        viewPagerBox = itemView.findViewById(R.id.viewPagerBox);
        pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);
    }
}
