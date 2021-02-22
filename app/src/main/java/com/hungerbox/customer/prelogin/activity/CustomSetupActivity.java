package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.deviceinfo.DeviceRegister;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.CardClearTask;
import com.hungerbox.customer.model.CompaniesReposne;
import com.hungerbox.customer.model.Company;
import com.hungerbox.customer.model.CompanyResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.fragment.VersionFragment;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class CustomSetupActivity extends ParentActivity {

    TextInputEditText tetTabPasscode;
    Spinner spLocations, spCompany;
    Button btSubmit, btReloadCompanies;
    CheckBox cbCompanyLoaded;
    LinearLayout progressBarLayout;

    Location selectedLocation;
    Company selectedCompany;
    boolean navigateToLogin = false;
    boolean isResumed = false;

    public static final int RESULT_FROM_DEVICE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_setup);
        tetTabPasscode = findViewById(R.id.tet_tab_passcode);
        spLocations = findViewById(R.id.sp_location_setup);
        spCompany = findViewById(R.id.sp_company_setup);
        progressBarLayout = findViewById(R.id.ll_load_config_container);
        btSubmit = findViewById(R.id.bt_submit);
        cbCompanyLoaded = findViewById(R.id.cb_load_company);
        btReloadCompanies = findViewById(R.id.bt_relaod);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPasscode();
            }
        });
        progressBarLayout.setVisibility(View.VISIBLE);

        long companyId = SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0);
        progressBarLayout.setVisibility(View.VISIBLE);
        if (companyId != 0) {
            progressBarLayout.setVisibility(View.VISIBLE);
            navigateToLogin = true;
            checkAndGetCompanyListFromServer(companyId);
        } else {

            getCompanies();
        }


        btReloadCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarLayout.setVisibility(View.VISIBLE);
                getCompanies();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    private void getCompanies() {
        btSubmit.setEnabled(false);
        String url = UrlConstant.COMPANY_LOCATION_GET;
        SimpleHttpAgent<CompaniesReposne> locationSimpleHttpAgent = new SimpleHttpAgent<CompaniesReposne>(
                this,
                url,
                new ResponseListener<CompaniesReposne>() {
                    @Override
                    public void response(CompaniesReposne responseObject) {
                        //TODO for null size
                        if (responseObject != null && responseObject.companies != null &&
                                responseObject.companies.size() > 0) {

                            updateComapanySpinner(responseObject.companies);
                            cbCompanyLoaded.setChecked(true);
                        } else {
                            progressBarLayout.setVisibility(View.GONE);
                            AppUtils.showToast("Unable to load companies", false, 2);


                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        cbCompanyLoaded.setChecked(false);
                        progressBarLayout.setVisibility(View.GONE);
                        AppUtils.showToast(error, true, 0);
                    }
                },
                CompaniesReposne.class
        );

        locationSimpleHttpAgent.get();
    }

    private void updateComapanySpinner(final ArrayList<Company> companies) {

        Collections.sort(companies, new Comparator<Company>() {
            @Override
            public int compare(Company c1, Company c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
        Company c = new Company();
        c.setName("Select Company");
        companies.add(0, c);
        ArrayAdapter<Company> locationArrayAdapter = new ArrayAdapter<Company>(this, android.R.layout.simple_spinner_item, companies);
        // Drop down layout style - list view with radio button
        locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spCompany.setAdapter(locationArrayAdapter);
        spCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (isResumed) {
                    Company company = companies.get(position);
                    setSelectedCompany(company, position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        progressBarLayout.setVisibility(View.GONE);
        if (isResumed)
            spCompany.performClick();
    }

    private void setSelectedCompany(Company company, int position) {
        if (position > 0) {
            this.selectedCompany = company;
            setupLocations();
            String configUrl = UrlConstant.CONFIG_URL_GET + company.id + "/cafe";
            getConfigFromServer(configUrl);
        } else {
            spLocations.setAdapter(null);
        }
    }


    private void setupLocations() {
        String url = UrlConstant.COMPANY_LOCATION_GET + selectedCompany.id;

        SimpleHttpAgent<CompanyResponse> locationSimpleHttpAgent = new SimpleHttpAgent<CompanyResponse>(
                this,
                url,
                new ResponseListener<CompanyResponse>() {
                    @Override
                    public void response(CompanyResponse responseObject) {
                        //TODO null check and size
                        AppUtils.addSubdomain(CustomSetupActivity.this, responseObject.companyResponse.subdomain);
                        if (responseObject.companyResponse != null &&
                                responseObject.companyResponse.locationResponse != null &&
                                responseObject.companyResponse.locationResponse.locations != null &&
                                responseObject.companyResponse.locationResponse.locations.size() > 0) {
                            updateSpinnerWithLocations(responseObject.companyResponse.locationResponse.locations);
                            storeLocations(responseObject.companyResponse.locationResponse.locations);
                        } else {
                            AppUtils.showToast("Unable to load company locations", false, 2);
                        }
                    }

                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        cbCompanyLoaded.setChecked(false);
                        progressBarLayout.setVisibility(View.GONE);
                        AppUtils.showToast(error, true, 0);

                    }
                },
                CompanyResponse.class
        );

        locationSimpleHttpAgent.get();
    }

    private void updateSpinnerWithLocations(final ArrayList<Location> locations) {
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location l1, Location l2) {
                return l1.getName().compareToIgnoreCase(l2.getName());
            }
        });
        Location l = new Location();
        l.setName("Select Location of the company");
        locations.add(0, l);
        ArrayAdapter<Location> locationArrayAdapter = new ArrayAdapter<Location>(this, android.R.layout.simple_spinner_item, locations);

        // Drop down layout style - list view with radio button
        locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spLocations.setAdapter(locationArrayAdapter);
        spLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Location location = locations.get(position);
                setSelectedLocation(location, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setSelectedLocation(Location location, int position) {
        if (position > 0) {
            this.selectedLocation = location;
        }
    }

    private void navigateToLoginScreen() {
        if (AppUtils.getConfig(this).isDevice_registration_allowed() && AppUtils.isGuestApp()) {
            if (false) {
                showRootedDeviceDialog();
            } else {
                if (SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID) == -1) {
                    progressBarLayout.setVisibility(View.VISIBLE);
                    DeviceRegister.startRegistration(this, "cafeteria",
                            AppUtils.getVersionName(), SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID),
                            SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID), RESULT_FROM_DEVICE,UrlConstant.BASE_URL);
                } else {
                    goToLoginScreen();
                }
            }
        } else {
            goToLoginScreen();
        }
    }

    @Override
    protected void onStop() {
        progressBarLayout.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_FROM_DEVICE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    long hbDeviceId = data.getLongExtra("hb_device_id", -1);
                    String peripheralDeviceRef = data.getStringExtra("peripheral_device_ref");
                    String unique_device_ref = data.getStringExtra("unique_device_ref");
                    if (hbDeviceId != -1) {
                        SharedPrefUtil.putLong(ApplicationConstants.HB_DEVICE_ID, hbDeviceId);
                        SharedPrefUtil.putString(ApplicationConstants.PERIPHERAL_DEIVCE_REF,peripheralDeviceRef);
                        SharedPrefUtil.putString(ApplicationConstants.PERIPHERAL_DEIVCE_ID , unique_device_ref);
                        goToLoginScreen();
                    }

                }
            } else {
                finish();
            }
        }
    }

    private void goToLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), HBWelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showRootedDeviceDialog() {
        GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance("Rooted device is not allowed.Please contact Hungerbox Tech Support Team", "Ok", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                finish();
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        genericPopUpFragment.setCancelable(false);
        genericPopUpFragment.show(getSupportFragmentManager(), "root_dialog");
    }

    private void submitPasscode() {
//        String android_id = Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//        storeTabCodeAndGoForLogin(android_id);
        if (selectedLocation != null && selectedCompany != null) {
            storeTabCodeAndGoForLogin(selectedLocation, selectedCompany);
            navigateToLoginScreen();
        } else {
            Toast.makeText(this, "Please select a company and location", Toast.LENGTH_LONG).show();
        }
    }


    private void storeTabCodeAndGoForLogin(Location location, Company company) {

        SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, location.id);
        SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME, location.name);
        SharedPrefUtil.putFloat(ApplicationConstants.LOCATION_CAPACITY, (float) selectedLocation.capacity);
        SharedPrefUtil.putLong(ApplicationConstants.COMPANY_ID, company.id);
        SharedPrefUtil.putInt(ApplicationConstants.ENFORCED_CAPACITY, selectedLocation.getEnforcedCapacity());

    }


    private void checkAndGetCompanyListFromServer(long companyId) {
        String configUrl = UrlConstant.CONFIG_URL_GET + companyId + "/cafe";
        long lastLocationTime = SharedPrefUtil.getLong(ApplicationConstants.LAST_LOCATION_UPDATED_TIME, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastLocationTime > ApplicationConstants.ONE_DAY_MILLIS) {
            //getCompanyDataFromServer(companyId);
            getConfigFromServer(configUrl);
        } else {
            getConfigFromServer(configUrl);
        }
    }


    private void getCompanyDataFromServer(final long companyId) {
        //TODO
        btSubmit.setEnabled(false);
        String url = UrlConstant.COMPANY_LOCATION_GET + companyId;
        final SimpleHttpAgent<CompaniesReposne> locationSimpleHttpAgent = new SimpleHttpAgent<CompaniesReposne>(
                this,
                url,
                new ResponseListener<CompaniesReposne>() {
                    @Override
                    public void response(CompaniesReposne responseObject) {
                        //TODO update view
                        try {
                            storeLocations(responseObject.companies.get(0).locationResponse.locations);
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }

                        String configUrl = UrlConstant.CONFIG_URL_GET + companyId + "/cafe";
                        getConfigFromServer(configUrl);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        cbCompanyLoaded.setChecked(false);
                        progressBarLayout.setVisibility(View.GONE);
                        AppUtils.showToast(error, true, 0);
                    }
                },
                CompaniesReposne.class
        );

        locationSimpleHttpAgent.get();
    }

    private void getConfigFromServer(final String url) {
        progressBarLayout.setVisibility(View.VISIBLE);
        SimpleHttpAgent<Config> configSimpleHttpAgent = new SimpleHttpAgent<Config>(
                this,
                url,
                new ResponseListener<Config>() {
                    @Override
                    public void response(Config responseObject) {
                        btSubmit.setEnabled(true);
                        setConfigAndCheckForVersion(responseObject);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        progressBarLayout.setVisibility(View.GONE);
                        AppUtils.showToast(error, true, 0);

                        GenericPopUpFragment configError = GenericPopUpFragment.newInstance(error,
                                "retry",
                                new GenericPopUpFragment.OnFragmentInteractionListener() {
                                    @Override
                                    public void onPositiveInteraction() {
                                        getConfigFromServer(url);
                                    }

                                    @Override
                                    public void onNegativeInteraction() {

                                    }
                                });
                        if (isResumed)
                            configError.show(getSupportFragmentManager(), "config_error");

                    }
                },
                Config.class
        );
        configSimpleHttpAgent.get();
    }

    private void checkAndShowDialog(String title, String desc, String redirectURL, boolean isHard) {

        progressBarLayout.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        VersionFragment versionFragment = VersionFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("desc", desc);
        bundle.putBoolean("isHard", isHard);
        bundle.putString("redirectURL", redirectURL);
        versionFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(versionFragment, "version")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commitAllowingStateLoss();

    }

    private void setConfigAndCheckForVersion(Config config) {
        new ConfigStorehandler(config).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void storeLocations(ArrayList<Location> locations) {
        new LocationStoreHandler(locations).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class ConfigStorehandler extends AsyncTask<Void, Integer, Config> {
        Config config;

        public ConfigStorehandler(Config config) {
            this.config = config;
        }

        @Override
        protected Config doInBackground(Void... voids) {
            MainApplication mainApplication = (MainApplication) getApplication();
            mainApplication.setConfig(config);
            CardClearTask.getInstance(getApplicationContext()).startTimer();
            return config;
        }

        @Override
        protected void onPostExecute(Config config) {
            super.onPostExecute(config);

            if (config.getApp_update_cafe() != null && config.getApp_update_cafe().update_redirect_url != null && !config.getApp_update_cafe().update_redirect_url.equals("") && config.getApp_update_cafe().getHard_version() >= BuildConfig.VERSION_CODE) {
                checkAndShowDialog(config.getApp_update_cafe().getTitle(), config.getApp_update_cafe().getHard_desc(), config.getApp_update_cafe().getUpdate_redirect_url(), true);
            } else {
                if (navigateToLogin)
                    navigateToLoginScreen();
            }

        }
    }

    class LocationStoreHandler extends AsyncTask<Void, Integer, Boolean> {
        ArrayList<Location> locations;

        public LocationStoreHandler(ArrayList<Location> locations) {
            this.locations = locations;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (!DbHandler.isStarted())
                DbHandler.start(getApplicationContext());
            DbHandler dbHandler = DbHandler.getDbHandler(getApplicationContext());
            boolean locationStored = dbHandler.createLocations(locations);
            return locationStored;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            SharedPrefUtil.putLong(ApplicationConstants.LAST_LOCATION_UPDATED_TIME, Calendar.getInstance().getTimeInMillis());
            progressBarLayout.setVisibility(View.GONE);
        }
    }
}
