package com.hungerbox.customer.order.adapter.viewholder;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by manas on 13/11/17.
 */
public class VendorHeaderItemHolder extends RecyclerView.ViewHolder {

    public final GifImageView gifExpressCheckout;
    public TextView vendorText,tvSpaceTitle,tvSpaceSubtitle;
    public RelativeLayout rlSearch;
    public EditText etSearch;
    public AppCompatImageView btFilter,qr;
    public RecyclerView rvTrending,rvFavourites;
    public FrameLayout layoutFavourites, layoutTrending;
    public CardView cvVendingListHeader,cvSpaceBooking;
    public CardView cvSlot;
    public TextView tvSlotBookingHeader,tvSlotBookingSubTitle, tvSlotTime;
    public Button btBookSlot,btSpaceBook;
    public ImageView ivSpaceBooking;

    public VendorHeaderItemHolder(View itemView) {
        super(itemView);
        gifExpressCheckout = itemView.findViewById(R.id.gif_express_checkout);
        gifExpressCheckout.setVisibility(View.GONE);
        vendorText = itemView.findViewById(R.id.vendor_header_text);
        rlSearch = itemView.findViewById(R.id.rl_search);
        etSearch = itemView.findViewById(R.id.et_search);
        btFilter = itemView.findViewById(R.id.bt_filter);
        qr = itemView.findViewById(R.id.iv_qr_slot);
        rvTrending = itemView.findViewById(R.id.rv_trending);
        rvFavourites = itemView.findViewById(R.id.rv_favourite);
        layoutFavourites = itemView.findViewById(R.id.favourite_items);
        layoutTrending = itemView.findViewById(R.id.trending_items);
        cvVendingListHeader = itemView.findViewById(R.id.cv_vending_list_header);
        cvSlot = itemView.findViewById(R.id.cv_slot);
        tvSlotBookingHeader = itemView.findViewById(R.id.tv_title);
        tvSlotBookingSubTitle = itemView.findViewById(R.id.tv_subtitle);
        btBookSlot = itemView.findViewById(R.id.bt_book);
        tvSlotTime = itemView.findViewById(R.id.tv_time);
        cvSpaceBooking = itemView.findViewById(R.id.cv_space_booking);
        tvSpaceTitle = itemView.findViewById(R.id.tv_space_title);
        tvSpaceSubtitle = itemView.findViewById(R.id.tv_space_subtitle);
        ivSpaceBooking = itemView.findViewById(R.id.iv_space_booking);
        btSpaceBook = itemView.findViewById(R.id.bt_space_book);
    }
}
