package com.hungerbox.customer.offline.activityOffline;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.NavigationDrawerLocationChangeEvent;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.navmenu.fragment.OnItemSelectedListener;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.fragmentOffline.FreeOrderErrorHandleDialogOffline;
import com.hungerbox.customer.offline.modelOffline.CityOffline;
import com.hungerbox.customer.offline.modelOffline.CompanyResponseOffline;
import com.hungerbox.customer.offline.modelOffline.LocationOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class LocationChangeActivityOffline extends AppCompatActivity implements OnItemSelectedListener {
    Button btPositiveButton;

    String locationName;
    long locationId;
    Spinner spCity, spCafe;
    long currentCartLocationId;
    LocationOffline selectedLocation;
    ArrayList<CityOffline> cities = new ArrayList<>();
    private int selectedCityIndex;
    private OnLocationChangeListener mListener;
    private ArrayList<LocationOffline> locationsList;
    private ProgressBar pbLocationLoader;
    private TextView tvCartClear;
    private ImageView ivBack;
    private Toolbar tb;
    private TextView tvCurrentLocation, tvSpCityTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_location_dialog_offline);
        tvCurrentLocation = findViewById(R.id.tv_current_location);
        tvCurrentLocation.setText("");
        tb = findViewById(R.id.tb);
        tb.setVisibility(View.VISIBLE);
        btPositiveButton = findViewById(R.id.bt_location_positive);
        pbLocationLoader = findViewById(R.id.pb_location);
        tvCartClear = findViewById(R.id.tv_cart_clear_warning);
        spCity = findViewById(R.id.sp_city);
        spCafe = findViewById(R.id.sp_cafe);
        tvSpCityTitle = findViewById(R.id.tv_sp_city_title);
        locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 1);
        currentCartLocationId = locationId;

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        tb.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LogoutTask.updateTime();
                if (!AppUtils.getConfig(LocationChangeActivityOffline.this).is_location_fixed() && selectedLocation != null) {

                    EventUtil.FbEventLog(LocationChangeActivityOffline.this, EventUtil.HOME_LOCATION_CHANGE, EventUtil.LOCATION_CHANGE);

                    String source = "Home";
                    try {
                        source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
                        if (source == null) {
                            source = "Home";
                        }
                        JSONObject jo = new JSONObject();
                        jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                        HBMixpanel.getInstance().addEvent(LocationChangeActivityOffline.this, EventUtil.MixpanelEvent.LOCATION_CHANGE, jo);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }

//                    try {
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put(CleverTapEvent.PropertiesNames.getPrevious_location(), locationName);
//                        map.put(CleverTapEvent.PropertiesNames.getNew_location(), selectedLocation.getName());
//                        map.put(CleverTapEvent.PropertiesNames.is_change(), selectedLocation.getId() != locationId);
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getLocation_change(), map, getApplicationContext());
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    if (MainApplicationOffline.isCartCreated() && selectedLocation.getId() != locationId) {
                        showCartClearPopUp();
                    } else {
                        updateLocationAndNavigateBack();
                    }
                }
            }
        });


        getLocations();
        LogoutTask.updateTime();
    }


    private void updateLocationAndNavigateBack() {
        SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, selectedLocation.id);
        SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME, selectedLocation.name);
        SharedPrefUtil.putFloat(ApplicationConstants.LOCATION_CAPACITY, (float) selectedLocation.capacity);
        SharedPrefUtil.putInt(ApplicationConstants.SSO_LOGIN_LOCATION_ASKED, 2);
        MainApplicationOffline.bus.post(new NavigationDrawerLocationChangeEvent());
        if (mListener != null) {
            mListener.onLocationChanged(selectedLocation.id, selectedLocation.name);
        }

        finish();
    }


    private void showCartClearPopUp() {
        LogoutTask.updateTime();
        FreeOrderErrorHandleDialogOffline orderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(new OrderOffline(),
                "Your cart will get cleared if you change the location.",
                new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        MainApplicationOffline.clearOrder(1);
                        MainApplicationOffline.bus.post(new OrderClear());

                        updateLocationAndNavigateBack();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                }, "OK", "CANCEL");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(orderErrorHandleDialog, "cart_clear")
                .commitAllowingStateLoss();
    }


    public void getLocations() {


        String url = UrlConstantsOffline.LOCATION + AppUtils.getConfig(LocationChangeActivityOffline.this).getCompany_id();
        pbLocationLoader.setVisibility(View.VISIBLE);
        SimpleHttpAgent<CompanyResponseOffline> locationSimpleHttpAgent = new SimpleHttpAgent<CompanyResponseOffline>(
                LocationChangeActivityOffline.this,
                url,
                new ResponseListener<CompanyResponseOffline>() {
                    @Override
                    public void response(CompanyResponseOffline responseObject) {
                        pbLocationLoader.setVisibility(View.GONE);
                        if (responseObject == null) {
                            btPositiveButton.setEnabled(false);
                            btPositiveButton.setClickable(false);
                        } else {

                            if (responseObject.companyResponse != null && responseObject.companyResponse.locationResponse != null && responseObject.companyResponse.locationResponse.locations != null && responseObject.companyResponse.locationResponse.locations.size() > 0) {
                                btPositiveButton.setEnabled(true);
                                btPositiveButton.setClickable(true);
                            } else {
                                btPositiveButton.setEnabled(false);
                                btPositiveButton.setClickable(false);
                            }
                            AppUtils.addSubdomain(LocationChangeActivityOffline.this, responseObject.companyResponse.subdomain);
                            pbLocationLoader.setVisibility(View.GONE);
                            groupCities(responseObject.companyResponse.locationResponse.locations);
                            showCitySippnerAndUpdateList(responseObject.companyResponse.locationResponse.locations);
                            int ssoLoginLocationAsked = SharedPrefUtil.getInt(ApplicationConstants.SSO_LOGIN_LOCATION_ASKED, 0);
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbLocationLoader.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION ||
                                errorCode == ContextErrorListener.TIMED_OUT) {
                            showNoNetFragment(new RetryListener() {
                                @Override
                                public void onRetry() {
                                    getLocations();
                                }
                            });
                        }

                    }
                },
                CompanyResponseOffline.class
        );

        locationSimpleHttpAgent.get();
    }

    public void showNoNetFragment(RetryListener retryListener) {
        NoNetFragment fragment = NoNetFragment.newInstance(retryListener);
        fragment.setCancelable(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, "exit")
                .commit();
    }

    private void showCitySippnerAndUpdateList(final ArrayList<LocationOffline> locations) {

        if (AppUtils.getConfig(LocationChangeActivityOffline.this).isGroup_location()) {
            spCity.setVisibility(View.VISIBLE);
            tvSpCityTitle.setVisibility(View.VISIBLE);
            ArrayAdapter<CityOffline> cityArrayAdapter = new ArrayAdapter<CityOffline>(this, android.R.layout.simple_spinner_dropdown_item, cities);
            cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spCity.setAdapter(cityArrayAdapter);

            selectedCityIndex = 0;

            for (LocationOffline location : locations) {
                if (location.id == locationId) {
                    for (int ci = 0; ci < cities.size(); ci++) {
                        if (cities.get(ci).getCityId() == location.getCityId()) {
                            selectedCityIndex = ci;
                        }
                    }
                }
            }

            spCity.setSelection(selectedCityIndex);

            spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LogoutTask.updateTime();
                    CityOffline city = cities.get(i);
                    updateLocationsWithCity(city, locations);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            spCity.setVisibility(View.GONE);
            tvSpCityTitle.setVisibility(View.GONE);
            updateSpinnerWithLocations(locations);
        }
    }

    private void updateLocationsWithCity(CityOffline city, ArrayList<LocationOffline> locations) {
        ArrayList<LocationOffline> newLocations = new ArrayList<>();
        for (LocationOffline location : locations) {
            if (location.getCityId() == city.getCityId()) {
                newLocations.add(location);
            }
        }
        updateSpinnerWithLocations(newLocations);

    }

    private void groupCities(ArrayList<LocationOffline> locations) {
        if (AppUtils.getConfig(LocationChangeActivityOffline.this).isGroup_location()) {
            TreeSet<CityOffline> citySet = new TreeSet<>();
            for (LocationOffline location : locations) {
                CityOffline city = new CityOffline();
                city.setCityName(location.getCityName());
                city.setCityId(location.getCityId());
                citySet.add(city);
            }

            cities.clear();
            cities.addAll(citySet);
        }
    }

    private void updateSpinnerWithLocations(final ArrayList<LocationOffline> locations) {
        if (LocationChangeActivityOffline.this == null || locations == null)
            return;
        locationsList = locations;
        long locationIndex = 0;
        int selectedCafeIndex = 0;

        for (int i = 0; i < locations.size(); i++) {
            LocationOffline location = locations.get(i);
            if (location.id == locationId) {

                locationIndex = location.id;
                selectedCafeIndex = i;

                if (tvCurrentLocation.getText().toString().equals("")) {
                    tvCurrentLocation.setText(location.getName());
                }
                break;
            }
        }


        ArrayAdapter<LocationOffline> cafeArrayAdapter = new ArrayAdapter<LocationOffline>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        cafeArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spCafe.setAdapter(cafeArrayAdapter);
        spCafe.setSelection(selectedCafeIndex);

        spCafe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelectedListener(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setSelectedLocation(LocationOffline location) {
        this.selectedLocation = location;


    }

    @Override
    public void onItemSelectedListener(int position) {
        if (locationsList != null) {
            LocationOffline location = locationsList.get(position);
//            if(location.id != currentCartLocationId)
//                tvCartClear.setVisibility(View.VISIBLE);
//            else
//                tvCartClear.setVisibility(View.GONE);
            setSelectedLocation(location);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLocationChangeListener {
        // TODO: Update argument type and name
        void onLocationChanged(long locationId, String locationName);
    }


}
