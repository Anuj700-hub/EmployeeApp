package com.hungerbox.customer.order.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.GuestList;
import com.hungerbox.customer.model.Guests;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.GuestListAdapter;
import com.hungerbox.customer.order.fragment.AddGuestDialog;
import com.hungerbox.customer.order.fragment.GuestListDialog;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddGuestActivity extends ParentActivity {

    //Toolbar toolbar;
    ImageView ivBack;
    TextView tvTitle, tvSelectCafeTitle;
    EditText etWalletAmount, etName;
    Button btnSubmit, btViewGuest;
    long selectedLocationId = 0;
    Guests currentGuest = new Guests();
    ArrayList<Guests> guestsArrayList = new ArrayList<>();
    RadioGroup rgCafe;
    RadioButton rbReg, rbGda;
    String cafe;
    private TextView tvValidFrom, tvValidTill;
    private Button btnAddMore;
    private RecyclerView rvGuestList;
    private GuestListAdapter guestListAdapter;
    private Calendar calender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);
       // toolbar = findViewById(R.id.tb_global);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        etName = findViewById(R.id.et_name);
        btnSubmit = findViewById(R.id.bt_create_guest);
        btnSubmit.setVisibility(View.GONE);
        tvValidFrom = findViewById(R.id.tv_valid_from);
        tvValidTill = findViewById(R.id.tv_valid_till);
        tvSelectCafeTitle = findViewById(R.id.tv_select_cafe_title);
        btnAddMore = findViewById(R.id.btn_add_more_guests);
        btViewGuest = findViewById(R.id.bt_view_guest);
        rvGuestList = findViewById(R.id.rv_guests);
        rvGuestList.setLayoutManager(new LinearLayoutManager(AddGuestActivity.this));
        guestListAdapter = new GuestListAdapter(AddGuestActivity.this, guestsArrayList,
                new GuestListAdapter.OnGuestRemoveInterface() {
                    @Override
                    public void onGuestRemoveInterface(int position) {
                        guestsArrayList.remove(position);
                        guestListAdapter.notifyItemRemoved(position);
                        guestListAdapter.notifyDataSetChanged();
                        if (guestsArrayList.size() <= 0)
                            btnSubmit.setVisibility(View.GONE);
                        if (guestsArrayList.size() < 8) {
                            rgCafe.setVisibility(View.INVISIBLE);
                            tvSelectCafeTitle.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        rvGuestList.setAdapter(guestListAdapter);
        tvTitle.setText("Guest Registration");
        calender = Calendar.getInstance();
        resetDates();
//        tvValidFrom.setText(calender.get(Calendar.DAY_OF_MONTH)
//                + "-" + (calender.get(Calendar.MONTH) + 1) + "-" + calender.get(Calendar.YEAR));
//        tvValidTill.setText(calender.get(Calendar.DAY_OF_MONTH)
//                + "-" + (calender.get(Calendar.MONTH) + 1) + "-" + calender.get(Calendar.YEAR));
//        currentGuest.setValidFrom(calender.getTimeInMillis() / 1000);
//        currentGuest.setValidTill(calender.getTimeInMillis() / 1000);
        rgCafe = findViewById(R.id.rg_order_place);
        rbReg = findViewById(R.id.rb_reg);
        rbGda = findViewById(R.id.rb_gda);
        rbGda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cafe = "gda";
            }
        });

        rbReg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cafe = "reg";
            }
        });
        tvValidFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddGuestActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar tempCalendar = Calendar.getInstance();
                                tempCalendar.set(year, monthOfYear, dayOfMonth);
                                currentGuest.setValidFrom(tempCalendar.getTimeInMillis() / 1000);
                                tvValidFrom.setText(tempCalendar.get(Calendar.DAY_OF_MONTH)
                                        + "-" + (tempCalendar.get(Calendar.MONTH) + 1) + "-" + tempCalendar.get(Calendar.YEAR));
                            }
                        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        tvValidTill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddGuestActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar tempCalendar = Calendar.getInstance();
                                tempCalendar.set(year, monthOfYear, dayOfMonth);
                                currentGuest.setValidTill(tempCalendar.getTimeInMillis() / 1000);
                                tvValidTill.setText(tempCalendar.get(Calendar.DAY_OF_MONTH)
                                        + "-" + (tempCalendar.get(Calendar.MONTH) + 1)
                                        + "-" + tempCalendar.get(Calendar.YEAR));
                            }
                        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

        btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGuest.setName(etName.getText().toString());
                addGuestToGuestList(currentGuest);
                guestListAdapter.notifyDataSetChanged();
                etName.setText("");
                resetDates();
                if (guestsArrayList.size() >= 8) {
                    rgCafe.setVisibility(View.VISIBLE);
                    tvSelectCafeTitle.setVisibility(View.VISIBLE);
                } else {
                    rgCafe.setVisibility(View.INVISIBLE);
                    tvSelectCafeTitle.setVisibility(View.INVISIBLE);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cafe != null || guestsArrayList.size() < 10)
                    handleSubmit();
                else
                    AppUtils.showToast("Please select a Dining area", true, 2);
            }
        });
        btViewGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGuestListDialog();
            }
        });
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LogoutTask.updateTime();
    }

    private void resetDates(){
        tvValidFrom.setText(calender.get(Calendar.DAY_OF_MONTH)
                + "-" + (calender.get(Calendar.MONTH) + 1) + "-" + calender.get(Calendar.YEAR));
        tvValidTill.setText(calender.get(Calendar.DAY_OF_MONTH)
                + "-" + (calender.get(Calendar.MONTH) + 1) + "-" + calender.get(Calendar.YEAR));
        currentGuest.setValidFrom(calender.getTimeInMillis() / 1000);
        currentGuest.setValidTill(calender.getTimeInMillis() / 1000);
    }


    private void showGuestListDialog() {
        GuestListDialog guestListDialog = GuestListDialog.newInstance();
        guestListDialog.show(getSupportFragmentManager(), "guest_list");
    }

    private void addGuestToGuestList(Guests guest) {
        if (guest.getName() == null || guest.getName().isEmpty()) {
            AppUtils.showToast("Name can't be empty", true, 2);
            return;
        }
        if (guest.getValidFrom() == 0) {
            AppUtils.showToast("Valid From can't be empty", true, 2);
            return;
        }
        if (guest.getValidTill() == 0) {
            AppUtils.showToast("Valid Till can't be empty", true, 2);
            return;
        }
        if (guest.getValidTill()<guest.getValidFrom()) {
            AppUtils.showToast("Valid Till can't be less than Valid From", true, 2);
            return;
        }


        guestsArrayList.add(new Guests(guest.getName(), guest.getValidFrom(), guest.getValidTill()));
        if (guestsArrayList.size() == 1) {
            btnSubmit.setVisibility(View.VISIBLE);
        }

    }

    private void handleSubmit() {
        LogoutTask.updateTime();
        createGuest();

    }

    private void createGuest() {
        btnSubmit.setEnabled(false);
        GuestList guestList = new GuestList();
        guestList.setCafe(cafe);
        guestList.setGuestList(guestsArrayList);
        String url = UrlConstant.ADD_GUEST;
        SimpleHttpAgent<GuestList> guestSimpleHttpAgent = new SimpleHttpAgent<GuestList>(
                this,
                url,
                new ResponseListener<GuestList>() {
                    @Override
                    public void response(GuestList responseObject) {
                        AddGuestDialog addGuestDialog = AddGuestDialog.newInstance(
                                new AddGuestDialog.OnFragmentInteractionListener() {
                                    @Override
                                    public void onFragmentInteraction() {
                                        finish();
                                    }
                                });
                        addGuestDialog.setCancelable(false);
                        addGuestDialog.show(getSupportFragmentManager(), "Add Guest");

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AddGuestDialog addGuestDialog = AddGuestDialog.newInstance(
                                new AddGuestDialog.OnFragmentInteractionListener() {
                                    @Override
                                    public void onFragmentInteraction() {
                                        finish();
                                    }
                                });
                        addGuestDialog.setCancelable(false);
                        addGuestDialog.show(getSupportFragmentManager(), "Add Guest");
                    }
                },
                GuestList.class
        );
        guestSimpleHttpAgent.post(guestList, new HashMap<String, JsonSerializer>());
    }

}
