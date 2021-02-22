package com.hungerbox.customer.navmenu.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.City;
import com.hungerbox.customer.model.CompanyResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.prelogin.activity.SignUpActivityBasic;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationDialog.OnLocationChangeListener} interface
 * to handle interaction events.
 * Use the {@link LocationDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDialog extends DialogFragment implements OnItemSelectedListener {


    TextView tvCurrentLocation;
    Button btPositiveButton;

    String locationName,cityName = null;
    long locationId;
    CheckBox cbDefaultLocation;
    Spinner spCity , spCafe;

    long currentCartLocationId;
    Location selectedLocation;
    ArrayList<City> cities = new ArrayList<>();
    boolean locationSelected = false;
    Config config;
    private OnLocationChangeListener mListener;
    private ArrayList<Location> locationsList;
    private ProgressBar pbLocationLoader;
    private TextView tvCartClear,tvSpCityTitle;
    private int selectedCityIndex;
    public DisplayMetrics displayMetrics;
    private RelativeLayout rlCafe;

    public LocationDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationDialog newInstance(OnLocationChangeListener listener) {
        LocationDialog fragment = new LocationDialog();
        fragment.mListener = listener;
        return fragment;
    }

    public static LocationDialog newInstance(OnLocationChangeListener listener, boolean isPublic) {
        LocationDialog fragment = new LocationDialog();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_location_dialog, container, false);
        tvCurrentLocation = view.findViewById(R.id.tv_current_location);
        btPositiveButton = view.findViewById(R.id.bt_location_positive);
        pbLocationLoader = view.findViewById(R.id.pb_location);
        cbDefaultLocation = view.findViewById(R.id.cb_location_default);
        tvCartClear = view.findViewById(R.id.tv_cart_clear_warning);
        spCity = view.findViewById(R.id.sp_city);
        spCafe = view.findViewById(R.id.sp_cafe);
        tvSpCityTitle = view.findViewById(R.id.tv_sp_city_title);
        rlCafe = view.findViewById(R.id.rv_sp_cafe);

        cbDefaultLocation.setVisibility(View.GONE);
        config = AppUtils.getConfig(getActivity());

        btPositiveButton.setText("Confirm");

        locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 1);
        currentCartLocationId = locationId;
        displayMetrics = getResources().getDisplayMetrics();

        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedLocation != null) {

                    locationSelected = true;
                    SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, selectedLocation.id);
                    SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME, selectedLocation.name);
                    SharedPrefUtil.putFloat(ApplicationConstants.LOCATION_CAPACITY, (float) selectedLocation.capacity);

                    if (mListener != null) {
                        mListener.onLocationChanged(selectedLocation.id, selectedLocation.name);
                    }

                    dismissAllowingStateLoss();
                }
            }
        });

        try {
            getDialog().setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && getActivity() != null) {
                        getActivity().finish();
                        dismiss();
                    }
                    return true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        getLocations();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public void getLocations() {
        if (getActivity() == null)
            return;

        String url = UrlConstant.COMPANY_LOCATION_GET + AppUtils.getConfig(getActivity()).getCompany_id();

        SimpleHttpAgent<CompanyResponse> locationSimpleHttpAgent = new SimpleHttpAgent<CompanyResponse>(
                getActivity(),
                url,
                new ResponseListener<CompanyResponse>() {
                    @Override
                    public void response(CompanyResponse responseObject) {
                        pbLocationLoader.setVisibility(View.GONE);
                        groupCities(responseObject.companyResponse.locationResponse.locations);
                        showCitySippnerAndUpdateList(responseObject.companyResponse.locationResponse.locations);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbLocationLoader.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            AppUtils.showToast("Please check your network.", true, 0);
                        }

                    }
                },
                CompanyResponse.class
        );

        locationSimpleHttpAgent.get();
    }

    private void showCitySippnerAndUpdateList(final ArrayList<Location> locations) {
        if (getActivity() == null)
            return;

        if (config.isGroup_location()) {
            spCity.setVisibility(View.VISIBLE);
            tvSpCityTitle.setVisibility(View.VISIBLE);
            ArrayAdapter<City> cityArrayAdapter = new ArrayAdapter<City>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cities);
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
        if (config.isGroup_location()) {
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
        if (getActivity() == null || locations == null)
            return;
        locationsList = locations;
        long locationIndex = 0;
        int selectedCafeIndex=0;

        for (int i=0;i<locations.size();i++) {
            Location location = locations.get(i);
            if (location.id == locationId) {
                locationIndex = location.id;
                selectedLocation = location;
                tvCurrentLocation.setText(location.getName());
                break;
            }
        }

        if (selectedLocation == null && locations != null && locations.size() > 0) {
            selectedLocation = locations.get(0);
            tvCurrentLocation.setText(locations.get(0).getName());
        }


        ArrayAdapter<Location> cafeArrayAdapter = new ArrayAdapter<Location>(getActivity(), android.R.layout.simple_spinner_dropdown_item, locations);
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelectedListener(int position) {
        if (locationsList != null) {
            Location location = locationsList.get(position);
            setSelectedLocation(location);

            AppUtils.HbLog("selected_location_dialog",location.getName());

        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (locationSelected) {
            super.onDismiss(dialog);
        } else {
            try {
                ((SignUpActivityBasic) getActivity()).navigateBack();
            } catch (NullPointerException npe) {

            }
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
