package com.hungerbox.customer.order.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.NavigationDrawerLocationChangeEvent;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.model.City;
import com.hungerbox.customer.model.CompanyResponse;
import com.hungerbox.customer.model.DataHandlerExtras;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.LocationUpdate;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.navmenu.fragment.OnItemSelectedListener;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.DataHandler;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.fragment.FreeOrderErrorHandleDialog;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;


public class LocationChangeActivity extends ParentActivity implements OnItemSelectedListener {


    Button btPositiveButton;

    String locationName,cityName = null;
    long locationId;
    Spinner spCity ,spCafe;
    long currentCartLocationId;
    Location selectedLocation;
    ArrayList<City> cities = new ArrayList<>();
    private int selectedCityIndex;
    private OnLocationChangeListener mListener;
    private ArrayList<Location> locationsList;
    private ArrayList<Location> bookingLocations;
    private ProgressBar pbLocationLoader;
    private TextView tvCartClear;
    private ImageView ivBack;
    private CheckBox cbDefaultLocation;
    private Toolbar tb;
    private TextView tvCurrentLocation,tvSpCityTitle;
    public DisplayMetrics displayMetrics;
    private RelativeLayout rlCafe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_location_dialog);
        displayMetrics = getResources().getDisplayMetrics();
        tvCurrentLocation = findViewById(R.id.tv_current_location);
        tvCurrentLocation.setText("");
        tb = findViewById(R.id.tb);
        tb.setVisibility(View.VISIBLE);
        btPositiveButton = findViewById(R.id.bt_location_positive);
        pbLocationLoader = findViewById(R.id.pb_location);
        tvCartClear = findViewById(R.id.tv_cart_clear_warning);
        spCity = findViewById(R.id.sp_city);
        spCafe = findViewById(R.id.sp_cafe);
        rlCafe = findViewById(R.id.rv_sp_cafe);
        tvSpCityTitle = findViewById(R.id.tv_sp_city_title);
        cbDefaultLocation = findViewById(R.id.cb_location_default);
        locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 1);
        currentCartLocationId = locationId;

        bookingLocations =  (ArrayList<Location>) (getIntent().getSerializableExtra("bookingLocations"));

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        tb.setNavigationIcon(R.drawable.ic_accent_back_arrow);

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
                if (!AppUtils.getConfig(LocationChangeActivity.this).is_location_fixed()&&selectedLocation != null) {

                    EventUtil.FbEventLog(LocationChangeActivity.this, EventUtil.HOME_LOCATION_CHANGE, EventUtil.LOCATION_CHANGE);

                    String source = "Home";
                    try {
                        source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
                        if (source == null) {
                            source = "Home";
                        }
                        JSONObject jo = new JSONObject();
                        jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                        HBMixpanel.getInstance().addEvent(LocationChangeActivity.this, EventUtil.MixpanelEvent.LOCATION_CHANGE, jo);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }

//                    try{
//                        HashMap<String,Object> map = new HashMap<>();
//                        map.put(CleverTapEvent.PropertiesNames.getPrevious_location(),locationName);
//                        map.put(CleverTapEvent.PropertiesNames.getNew_location(),selectedLocation.getName());
//                        map.put(CleverTapEvent.PropertiesNames.is_default(),cbDefaultLocation.isChecked());
//                        map.put(CleverTapEvent.PropertiesNames.is_change(), selectedLocation.getId() != locationId);
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getLocation_change(),map,getApplicationContext());
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

                    if (MainApplication.isCartCreated()&& selectedLocation.getId() != locationId) {
                        showCartClearPopUp();
                    } else {
                        if (cbDefaultLocation.isChecked()) {
                            updateCurrentLocation(selectedLocation);
                        }

                        updateLocationAndNavigateBack(false);
                    }
                }
            }
        });


        getLocations();
        LogoutTask.updateTime();
    }


    private void updateLocationAndNavigateBack(boolean shouldClearCart) {
        SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, selectedLocation.id);
        SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME, selectedLocation.name);
        SharedPrefUtil.putLong(ApplicationConstants.LOCATION_CITY_ID, selectedLocation.getCityId());
        SharedPrefUtil.putFloat(ApplicationConstants.LOCATION_CAPACITY, (float) selectedLocation.capacity);
        SharedPrefUtil.putInt(ApplicationConstants.SSO_LOGIN_LOCATION_ASKED, 2);
        SharedPrefUtil.putInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, selectedLocation.getDeskOrderingEnabled());
        SharedPrefUtil.putStringArrayList(ApplicationConstants.OTHER_TYPE_LOCATION,selectedLocation.getOtherLocationTypes());
        SharedPrefUtil.putInt(ApplicationConstants.ENFORCED_CAPACITY, selectedLocation.getEnforcedCapacity());
        MainApplication.bus.post(new NavigationDrawerLocationChangeEvent());
        if (mListener != null) {
            mListener.onLocationChanged(selectedLocation.id, selectedLocation.name);
        }

        Intent returnIntent;
        if(bookingLocations != null){
            returnIntent = new Intent(getApplicationContext(), GlobalActivity.class);
            if(shouldClearCart)
                setResult(Activity.RESULT_OK,returnIntent);
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(returnIntent);
        }
        else{
            returnIntent = new Intent();
            if(shouldClearCart)
                setResult(Activity.RESULT_OK,returnIntent);
        }
        finish();
    }


    private void showCartClearPopUp() {
        LogoutTask.updateTime();
        FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                "Your cart will get cleared if you change the location.",
                new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        MainApplication mainApplication = (MainApplication) getApplication();
                        mainApplication.clearOrder();
                        MainApplication.bus.post(new OrderClear());

                        if (cbDefaultLocation.isChecked())
                            updateCurrentLocation(selectedLocation);

                        updateLocationAndNavigateBack(true);
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

    private void updateCurrentLocation(Location location) {

        String url = UrlConstant.LOCATION_UPDATE;
        SimpleHttpAgent<Object> locationUpdateRequest = new SimpleHttpAgent<Object>(
                LocationChangeActivity.this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {

                        SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID_PERMANENT, selectedLocation.getId());
                        SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME_PERMANENT, selectedLocation.getName());
                        SharedPrefUtil.putFloat(ApplicationConstants.LOCATION_CAPACITY, (float) selectedLocation.capacity);

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                Object.class
        );

        locationUpdateRequest.post(new LocationUpdate().setLocationId(location.id).setLocationName(location.name), new HashMap<String, JsonSerializer>());
    }


    public void getLocations() {

        String url = UrlConstant.COMPANY_LOCATION_GET_V3 + AppUtils.getConfig(LocationChangeActivity.this).getCompany_id();

        HashMap<String , String> objectIds = new HashMap<>();
        objectIds.put(ApplicationConstants.OBJECT_ID_1,AppUtils.getConfig(LocationChangeActivity.this).getCompany_id()+"");
        objectIds.put(ApplicationConstants.OBJECT_ID_2,"");

        pbLocationLoader.setVisibility(View.VISIBLE);

        DataHandler<CompanyResponse> userSimpleHttpAgent = new DataHandler<CompanyResponse>(
                this,
                url,
                new ResponseListener<CompanyResponse>() {
                    @Override
                    public void response(CompanyResponse responseObject) {
                        pbLocationLoader.setVisibility(View.GONE);
                        if (responseObject == null) {
                            btPositiveButton.setEnabled(false);
                            btPositiveButton.setClickable(false);
                        } else {

                            if(responseObject.companyResponse != null && responseObject.companyResponse.locationResponse != null && responseObject.companyResponse.locationResponse.locations != null){
                                ArrayList<Location> filteredLocations = new ArrayList<>();
                                if(bookingLocations != null){
                                    filteredLocations = getFilteredLocations(responseObject.companyResponse.locationResponse.locations);
                                }
                                else{
                                    filteredLocations = responseObject.companyResponse.locationResponse.locations;
                                }

                                if (filteredLocations.size() > 0) {
                                    btPositiveButton.setEnabled(true);
                                    btPositiveButton.setClickable(true);
                                } else {
                                    btPositiveButton.setEnabled(false);
                                    btPositiveButton.setClickable(false);
                                }
                                AppUtils.addSubdomain(LocationChangeActivity.this, responseObject.companyResponse.subdomain);
                                pbLocationLoader.setVisibility(View.GONE);
                                groupCities(filteredLocations);
                                showCitySippnerAndUpdateList(filteredLocations);


                                int ssoLoginLocationAsked = SharedPrefUtil.getInt(ApplicationConstants.SSO_LOGIN_LOCATION_ASKED, 0);
                                if(ssoLoginLocationAsked == 1){
                                    cbDefaultLocation.setChecked(true);
                                    cbDefaultLocation.setEnabled(false);
                                }
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbLocationLoader.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION ||
                                errorCode == ContextErrorListener.TIMED_OUT) {
                            showError(ApplicationConstants.NO_NET_STRING,ApplicationConstants.NO_INTERNET_IMAGE);
                        }else{
                            if (error != null && !error.equals("")) {
                                showError(error,ApplicationConstants.SOME_WRONG);
                            }else{
                                showError(ApplicationConstants.SOME_WRONG,ApplicationConstants.SOME_WRONG);
                            }
                        }
                    }
                },
                CompanyResponse.class,
                objectIds,
                ApplicationConstants.CRUD.GET,
                new DataHandlerExtras()
        );

    }

    private ArrayList<Location> getFilteredLocations(ArrayList<Location> locations){
        ArrayList<Location> filteredLocations = new ArrayList<>();

        for(Location bookingLocation: bookingLocations){
            for(Location location : locations){
                if(bookingLocation.getId() == location.getId()){
                    filteredLocations.add(location);
                    break;
                }
            }
        }
        return filteredLocations;
    }
    private void showError(String error,String imageType) {

        ErrorPopFragment genericPopUpFragment = ErrorPopFragment.Companion.newInstance(error, "RETRY",true,imageType,
                new ErrorPopFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        getLocations();
                    }

                    @Override
                    public void onNegativeInteraction() {
                        finish();

                    }
                });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "error")
                .commitAllowingStateLoss();
    }


    private void showCitySippnerAndUpdateList(final ArrayList<Location> locations) {

        if (AppUtils.getConfig(LocationChangeActivity.this).isGroup_location()) {
            spCity.setVisibility(View.VISIBLE);
            tvSpCityTitle.setVisibility(View.VISIBLE);
            ArrayAdapter<City> cityArrayAdapter = new ArrayAdapter<City>(this, R.layout.simple_spinner_dropdown_accent, cities);
            cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spCity.setAdapter(cityArrayAdapter);

            selectedCityIndex = 0;

            for (Location location : locations) {
                if (location.id == locationId) {
                    for (int ci = 0; ci < cities.size(); ci++) {
                        if (cities.get(ci).getCityId() == location.getCityId()) {
                            selectedCityIndex = ci;
                        }
                    }
                }
            }

            spCity.setSelection(selectedCityIndex);
            //spCity.setDropDownWidth(spCity.getWidth());
            spCity.setDropDownVerticalOffset(spCity.getHeight());

            spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LogoutTask.updateTime();
                    City city = cities.get(i);
                    cityName = city.getCityName();
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

    private void updateLocationsWithCity(City city, ArrayList<Location> locations) {
        ArrayList<Location> newLocations = new ArrayList<>();
        for (Location location : locations) {
            if (location.getCityId() == city.getCityId()) {
                newLocations.add(location);
            }
        }
        updateSpinnerWithLocations(newLocations);

    }

    private void groupCities(ArrayList<Location> locations) {
        if (AppUtils.getConfig(LocationChangeActivity.this).isGroup_location()) {
            TreeSet<City> citySet = new TreeSet<>();
            for (Location location : locations) {
                City city = new City();
                city.setCityName(location.getCityName());
                city.setCityId(location.getCityId());
                citySet.add(city);
            }

            cities.clear();
            cities.addAll(citySet);
        }
    }

    private void updateSpinnerWithLocations(final ArrayList<Location> locations) {
        if (LocationChangeActivity.this == null || locations == null)
            return;
        locationsList = locations;
        long locationIndex = 0;
        int selectedCafeIndex=0;

        for (int i=0;i<locations.size();i++) {
            Location location = locations.get(i);
            if (location.id == locationId) {

                locationIndex = location.id;
                selectedCafeIndex = i;

                if (tvCurrentLocation.getText().toString().equals("")) {
                    tvCurrentLocation.setText(location.getName());
                }
                break;
            }
        }


        ArrayAdapter<Location> cafeArrayAdapter = new ArrayAdapter<Location>(this, R.layout.simple_spinner_dropdown_accent, locations);
        cafeArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spCafe.setAdapter(cafeArrayAdapter);
        spCafe.setSelection(selectedCafeIndex);
        //spCafe.setDropDownWidth(spCafe.getWidth());
        spCafe.setDropDownVerticalOffset(rlCafe.getHeight());

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

    public void setSelectedLocation(Location location) {
        this.selectedLocation = location;
        if (location!=null && location.getName()!=null) {
            String name = "";
            if(cityName!=null && cityName.length()>0){
                name = cityName +" - ";
            }
            name  = name + location.getName();
            tvCurrentLocation.setText(name);
        }


    }

    @Override
    public void onItemSelectedListener(int position) {
        if (locationsList != null) {
            Location location = locationsList.get(position);
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
