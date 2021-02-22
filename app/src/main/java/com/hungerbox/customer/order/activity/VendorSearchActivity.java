package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.adapter.VendorSearchListAdapter;
import com.hungerbox.customer.order.listeners.VendorSelectedListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class VendorSearchActivity extends ParentActivity {



    Realm realm;
    RealmResults<Vendor> vendorRealmResults;
    RecyclerView rvVendors;
    EditText textSearchText;
    long occassionId;
    String locationName;
    ImageView ivBack;
    LinearLayout ll_search_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_search);


        //toolbar = findViewById(R.id.tb_global);

        //tvTitle = toolbar.findViewById(R.id.tv_toolbar_title);

//        ivLogo = findViewById(R.id.logo);
//        ivOcassions = findViewById(R.id.iv_ocassion);
//        ivSearch = findViewById(R.id.iv_search);
//        spLocation = findViewById(R.id.tv_location);
        rvVendors = findViewById(R.id.rv_search_list);

        textSearchText = findViewById(R.id.et_search);
        ivBack = findViewById(R.id.iv_back);

        occassionId = getIntent().getLongExtra(ApplicationConstants.OCASSION_ID, 0);
        locationName = getIntent().getStringExtra(ApplicationConstants.LOCATION_NAME);

        textSearchText.addTextChangedListener(searchtextWatcher);
        ll_search_result = findViewById(R.id.ll_search_result);


        rvVendors.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true);
        rvVendors.setLayoutManager(llm);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ll_search_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboard(VendorSearchActivity.this,null);
            }
        });
        LogoutTask.updateTime();
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());

        AppUtils.showKeyboard(VendorSearchActivity.this, textSearchText);
    }

    TextWatcher searchtextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

//            String searchString = s.toString().trim();
//            if(searchString.length()>0){
//                vendorRealmResults = realm.where(Vendor.class)
//                        .equalTo("active", 1)
//                        .contains(Vendor.VENDOR_NAME, searchString, Case.INSENSITIVE)
//                        .or()
//                        .contains(Vendor.VENDOR_DESC, searchString, Case.INSENSITIVE)
//                        .findAll();
//                createVendorList(vendorRealmResults);
//            }else{
//                vendorRealmResults = realm.where(Vendor.class).equalTo("active", 1).findAll();
//                createVendorList(vendorRealmResults);
//            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



    private void createVendorList(RealmResults<Vendor> vendorResult){
        LogoutTask.updateTime();
        if(vendorResult.size()>0){
            EventUtil.FbEventLog(VendorSearchActivity.this, EventUtil.HOME_SEARCH_SUCCESS,EventUtil.SCREEN_VENDOR_SEARCH);
        }
        if (rvVendors.getAdapter() == null) {
            VendorSearchListAdapter vendorSearchListAdapter = new VendorSearchListAdapter(this, vendorResult, new VendorSelectedListener() {
                @Override
                public void onVendorSelected(Vendor vendor) {

                    try{
                        HashMap<String,Object> map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getVendor_id(),vendor.getId());
                        map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getVendor_click(),map,getApplicationContext());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Intent intent;
                    if(vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)){
                        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
                        intent = new Intent(VendorSearchActivity.this, PaymentActivity.class);
                        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, vendor.getSdkType());
                        intent.putExtra(ApplicationConstants.BIG_BASKET, true);
                        intent.putExtra(ApplicationConstants.OCASSION_ID, MainApplication.selectedOcassionId);
                        intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);

                    }else{
                        intent = new Intent(VendorSearchActivity.this, MenuActivity.class);
                        intent.putExtra(ApplicationConstants.VENDOR_ID, vendor.getId());
                        intent.putExtra(ApplicationConstants.VENDOR_NAME, vendor.getVendorName());
                        intent.putExtra(ApplicationConstants.OCASSION_ID, occassionId);
                        intent.putExtra(ApplicationConstants.LOCATION, locationName);
                    }
                    startActivity(intent);
                    finish();
                }
            });
            rvVendors.setAdapter(vendorSearchListAdapter);
        } else {
            if (rvVendors.getAdapter() instanceof VendorSearchListAdapter) {
                VendorSearchListAdapter vendorSearchListAdapter = (VendorSearchListAdapter) rvVendors.getAdapter();
                vendorSearchListAdapter.updateList(vendorResult);
                vendorSearchListAdapter.notifyDataSetChanged();
            }
        }
    }
}
