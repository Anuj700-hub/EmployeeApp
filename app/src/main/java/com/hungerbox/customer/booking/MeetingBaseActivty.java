package com.hungerbox.customer.booking;

import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.hungerbox.customer.R;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.OrderSuccessActivity;
import com.hungerbox.customer.util.ApplicationConstants;


public class MeetingBaseActivty extends GlobalActivity {


    boolean webCanGoBack = false;
    private boolean isHistory;

    @Override
    protected void createBaseContainer() {
        String urlPart = getIntent().getStringExtra(ApplicationConstants.MEETING_URL);
        isHistory = getIntent().getBooleanExtra(ApplicationConstants.IS_HISTORY, false);
        if (fragment == null) {
            fragment = MeetingFragment.newInstance(isHistory, urlPart);
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            }
        }
        ivLogo.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(View.GONE);
        ivOcassions.setVisibility(View.INVISIBLE);
        spLocation.setVisibility(View.INVISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Shared Economy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getOccassionList();
        getLocations();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return false;
//        }
        return super.onOptionsItemSelected(item);
    }


    public void toggleToolbar(final boolean active) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Your code to run in GUI thread here

                if (active) {
                    toolbar.setVisibility(View.VISIBLE);
                } else {
                    toolbar.setVisibility(View.GONE);
                }
                webCanGoBack = !active;
            }//public void run() {


        });


    }


    public void navigateToCheckoutView(final String message) {


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MeetingBaseActivty.this, OrderSuccessActivity.class);
                intent.putExtra("message", message);
                startActivity(intent);
            }


        });

    }


    @Override
    public void onBackPressed() {

        if (webCanGoBack) {
            if (fragment != null && fragment instanceof MeetingFragment) {
                MeetingFragment meetingFragment = (MeetingFragment) fragment;
                meetingFragment.goBackInWebView();
            }
            toolbar.setVisibility(View.VISIBLE);
            webCanGoBack = false;
        } else {
            finish();
        }
    }

    public void getBookingFeedback(long id) {

    }

}
