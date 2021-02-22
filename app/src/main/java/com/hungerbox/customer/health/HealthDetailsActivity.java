package com.hungerbox.customer.health;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.UserHealth;
import com.hungerbox.customer.model.UserHealthResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class HealthDetailsActivity extends ParentActivity {
    EditText etWeight, etHeight, etGoal;
    TextView tvBmi, tvCalorieIntake;
    Button btnUpdate;
    ImageView ivBack;
    double weight, height, goal;
    private String gender;
    private String[] exercise = {"Little or no exercise", "Light exercise", "Moderate exercise", "Very active", "Extra active"};
    private Spinner spExercise;
    private RadioButton rbMale, rbFemale;
    private ArrayList<String> exerciseList;
    private View llBmiCal;
    private TextView tvCalIntake;
    private boolean isRegister;
    private RelativeLayout rlHealthTracker;
    private TextView tvHealthTracker, linkDevice;
    private View bmiContainer, rmiContainer;
    private TextView tvDelinkTracker;
    private EditText etAge;
    private boolean dateDialogShow = false;

    private boolean clickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_details);
        AppUtils.setupUI(findViewById(R.id.healthDetailsActivityParent), HealthDetailsActivity.this);

        isRegister = getIntent().getBooleanExtra("isRegister", false);
        linkDevice = findViewById(R.id.tv_link);

        if (isRegister) {
            linkDevice.setVisibility(View.GONE);
        }
        etWeight = findViewById(R.id.et_weight);
        etAge = findViewById(R.id.et_age);
        etHeight = findViewById(R.id.et_height);
        etGoal = findViewById(R.id.et_goal);
        tvBmi = findViewById(R.id.tv_bmi);
        tvCalIntake = findViewById(R.id.tv_cal_intake);
        bmiContainer = findViewById(R.id.bmiContainer);
        rmiContainer = findViewById(R.id.rmiContainer);
        llBmiCal = findViewById(R.id.ll_bmi_cal_container);
        rbMale = findViewById(R.id.radio_male);
        rbFemale = findViewById(R.id.radio_female);
        tvCalorieIntake = findViewById(R.id.tv_cal);
        btnUpdate = findViewById(R.id.btn_update);
        ivBack = findViewById(R.id.iv_back);
        spExercise = findViewById(R.id.sp_exercise);
        rlHealthTracker = findViewById(R.id.rl_health_tracker);
        tvHealthTracker = findViewById(R.id.tv_health_tracker);
        tvDelinkTracker = findViewById(R.id.tv_delink);
        exerciseList = new ArrayList<String>();
        exerciseList.addAll(Arrays.asList(exercise));
        ArrayAdapter<String> ExerciseTypeArrayAdapter = new ArrayAdapter<String>(HealthDetailsActivity.this, android.R.layout.simple_spinner_item, exerciseList);
        ExerciseTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etWeight.requestFocus();
        spExercise.setAdapter(ExerciseTypeArrayAdapter);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickable) {
                    validateAndUpdate();
                }
            }
        });

        tvDelinkTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delinkHealthTracker();
            }
        });

        linkDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthDetailsActivity.this, ConnectDeviceActivity.class);
                intent.putExtra("isRegister", true);
                intent.putExtra("afterDashboard", true);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        getEmployeeHealth();

        etAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!dateDialogShow) {
                    dateDialogShow = true;
                    DatePickerDialog dialog = new DatePickerDialog(HealthDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int selectedYear,
                                              int selectedMonth, int selectedDay) {

                            String year = selectedYear + "";
                            String month = (selectedMonth + 1) + "";
                            String day = selectedDay + "";
                            if (selectedDay < 10) {
                                day = "0" + day;
                            }
                            if (selectedMonth < 10) {
                                month = "0" + month;
                            }

                            etAge.setText(year + "-" + month + "-" + day);
                            dateDialogShow = false;
                            etAge.setError(null);

                        }
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dateDialogShow = false;
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dateDialogShow = false;
                        }
                    });
                    try {
                        dialog.getDatePicker().getTouchables().get(0).performClick();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, -18);
                    dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                    dialog.show();
                }
            }
        });
    }

    private void delinkHealthTracker() {
        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                getApplicationContext(),
                UrlConstant.DELINK_HEALTH_TRACKER,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        getEmployeeHealth();
                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

            }
        },
                Object.class
        );

        objectSimpleHttpAgent.put("{}");
    }

    private void validateAndUpdate() {

        if (gender == null) {
            AppUtils.showToast("Please select gender.", true, 2);
            return;
        }

        if (etAge.getText().toString().equals("")) {
            etAge.setError("Enter Age");
            return;
        }

        try {
            weight = Double.parseDouble(etWeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            etWeight.setError("Enter Weight");
            return;
        }
        try {
            height = Double.parseDouble(etHeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            etHeight.setError("Enter Height");
            return;
        }
        try {
            goal = Double.parseDouble(etGoal.getText().toString().trim());
        } catch (NumberFormatException e) {
            etGoal.setError("Enter Goal");
            return;
        }
        if (!(weight > 0) || !(height > 0) || !(goal > 0) || gender == null)
            AppUtils.showToast("Please fill in all the details", true, 2);
        else
            postUpdate();
    }

    private void postUpdate() {
        clickable = false;
        long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
        UserHealth userHealth = new UserHealth();
        userHealth.setUserId(userId);
        userHealth.setWeight(weight);
        userHealth.setHeight(height);
        userHealth.setTargetWeight(goal);
        userHealth.setGender(gender);
        userHealth.setAge(etAge.getText().toString());
        userHealth.setLifestyle(spExercise.getSelectedItem().toString());

        String url = UrlConstant.USER_HEALTH + "/register";
        SimpleHttpAgent<UserHealth> userHealthAgent = new SimpleHttpAgent<UserHealth>(HealthDetailsActivity.this,
                url, new ResponseListener<UserHealth>() {
            @Override
            public void response(UserHealth responseObject) {
                clickable = true;
                if (isRegister)
                    navigateToConnectDevice();
                else {
                    AppUtils.showToast("Health details updated successfully!!", true, 1);
                    getEmployeeHealth();
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                clickable = true;
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                    AppUtils.showToast("Please check your network.", true, 0);
                } else {
                    AppUtils.showToast(error, true, 0);
                }
            }
        }, UserHealth.class);
        userHealthAgent.post(userHealth, new HashMap<String, JsonSerializer>());

    }

    private void navigateToConnectDevice() {

        SharedPrefUtil.putBoolean(ApplicationConstants.HEALTH_DETAILS, true);
        Intent intent = new Intent(HealthDetailsActivity.this, ConnectDeviceActivity.class);

        startActivity(intent);
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked) {
                    gender = "male";
                    break;
                }
            case R.id.radio_female:
                if (checked) {
                    gender = "female";
                    break;
                }
        }
    }

    double calculateBmi(double weight, double height) {
        height = height / 100;
        return (weight / (height * height));
    }

    private void getEmployeeHealth() {
        String url = UrlConstant.USER_HEALTH;
        SimpleHttpAgent<UserHealthResponse> userHealthAgent = new SimpleHttpAgent<UserHealthResponse>(HealthDetailsActivity.this, url, new ResponseListener<UserHealthResponse>() {
            @Override
            public void response(UserHealthResponse responseObject) {
                if (responseObject != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("linked", responseObject.getUserHealth().deviceLinked);
                    setResult(RESULT_OK, resultIntent);

                    llBmiCal.setVisibility(View.VISIBLE);
                    if (responseObject.getUserHealth().getAge() == null) {
                        etAge.setText("");
                    } else {
                        etAge.setText(responseObject.getUserHealth().getAge() + "");
                    }
                    etWeight.setText(responseObject.getUserHealth().getWeight() + "");
                    etHeight.setText(responseObject.getUserHealth().getHeight() + "");
                    etGoal.setText(responseObject.getUserHealth().getTargetWeight() + "");

                    if (responseObject.getUserHealth().getCalorieDataResponse() != null) {
                        tvBmi.setText(responseObject.getUserHealth().getCalorieDataResponse().getCalorieData().getBmi() + "");
                        tvCalIntake.setText(responseObject.getUserHealth().getCalorieDataResponse().getCalorieData().getIdealCalorieIntake() + "");
                    }

                    Animation scaleAnim = AnimationUtils.loadAnimation(HealthDetailsActivity.this, R.anim.bounce_in_horizontal);
                    bmiContainer.startAnimation(scaleAnim);
                    rmiContainer.startAnimation(scaleAnim);

                    if (responseObject.getUserHealth().isDeviceLinked()) {
                        rlHealthTracker.setVisibility(View.VISIBLE);
                        if (!responseObject.getUserHealth().getDeviceName().isEmpty()) {
                            tvHealthTracker.setText(responseObject.getUserHealth().getDeviceName());
                        }
                    } else {
                        rlHealthTracker.setVisibility(View.GONE);
                    }
                    if (responseObject.getUserHealth().getGender().equalsIgnoreCase("male")) {
                        rbMale.setChecked(true);
                        gender = "male";
                        onRadioButtonClicked(rbMale);
                    } else {
                        rbFemale.setChecked(true);
                        gender = "female";
                        onRadioButtonClicked(rbMale);
                    }
                    for (int i = 0; i < exerciseList.size(); i++) {
                        if (responseObject.getUserHealth().getLifestyle().equalsIgnoreCase(exerciseList.get(i))) {
                            spExercise.setSelection(i);
                            break;
                        }
                    }

                } else {
                    llBmiCal.setVisibility(View.GONE);
                }

            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                    AppUtils.showToast("Please check your network.", true, 0);
                } else {
                    AppUtils.showToast(error, true, 0);
                }
            }

        }, UserHealthResponse.class);

        userHealthAgent.get();

    }

}
