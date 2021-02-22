package com.hungerbox.customer.booking;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.booking.fragment.BookingDetailFragment;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;


public class BookingDetailActvity extends ParentActivity{


    public TextView tvTitle, tvDone;
    public ImageView ivLogo, ivOcassions, ivSearch;
    protected Toolbar toolbar;
    protected TextView spLocation;
    long bookingId;
    BookingDetailFragment fragment;
    private String historyType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail_actvity);


        toolbar = findViewById(R.id.tb_global);

        tvTitle = toolbar.findViewById(R.id.tv_toolbar_title);

        ivLogo = findViewById(R.id.logo);
        ivOcassions = findViewById(R.id.iv_ocassion);
        ivSearch = findViewById(R.id.iv_search);
        spLocation = findViewById(R.id.tv_location);

        tvDone = findViewById(R.id.tv_done);
        tvDone.setVisibility(View.GONE);

        bookingId = getIntent().getLongExtra(ApplicationConstants.BOOKING_ID, 0L);
        historyType = getIntent().getStringExtra("historyType");
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        createBaseContainer();
    }


    protected void createBaseContainer() {
        if (fragment == null) {
            fragment = BookingDetailFragment.newInstance(bookingId, historyType);
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.fl_container, fragment, "booking_detail")
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.fl_container, fragment, "booking_detail")
                        .commit();
            }
        }
        ivLogo.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(View.GONE);
        ivOcassions.setVisibility(View.INVISIBLE);
        spLocation.setVisibility(View.INVISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Booking Detail");
    }
}
