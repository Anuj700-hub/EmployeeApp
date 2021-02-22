package com.hungerbox.customer.offline.activityOffline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOptionItemOffline;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.OrderSubProductOffline;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.QrUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderQrCodeActivityOffline extends AppCompatActivity {

    private boolean isNewOrder = false;
    TextView tvNumOfItems, tvPrice, tv_description;
    Button btSubmit;
    ImageView iv_barcode,iv_back;
    OrderOffline orderOffline;
    int cartQty;
    boolean fromHistory = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_qr_activity_offline);
        isNewOrder = getIntent().getBooleanExtra(ApplicationConstants.IS_NEW_ORDER, false);
        btSubmit = findViewById(R.id.bt_submit);
        tvPrice = findViewById(R.id.tv_price);
        tvNumOfItems = findViewById(R.id.tv_num_of_items);
        tv_description = findViewById(R.id.tv_description);
        iv_barcode = findViewById(R.id.iv_barcode);
        iv_back = findViewById(R.id.iv_back);
        orderOffline = (OrderOffline) getIntent().getSerializableExtra(ApplicationConstants.ORDER);
        cartQty = getIntent().getIntExtra("cartQty", 0);
        fromHistory = getIntent().getBooleanExtra("fromHistory", false);


        if (!fromHistory) {
            creatQrCode();
        } else {
            btSubmit.setVisibility(View.INVISIBLE);
            if (orderOffline.getQrCoder() != null) {
                setBarCode(orderOffline.getQrCoder());
            }
        }

        try {
            tvNumOfItems.setText(getTotalOrderCount() + " Items");
            tvPrice.setText("Rs " + roundToTwoDigit());
            tv_description.setText("Scan this QR code within 30 mins at " + orderOffline.vendorName + ". You will be charged only if vendor accepts the order.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });



    }

    private double roundToTwoDigit(){
        return (double) Math.round(orderOffline.getTotalPrice() * 100) / 100;
    }

    private void creatQrCode() {
        ArrayList<OrderProductOffline> products = orderOffline.getProducts();
        HungerBoxOffline.OrderItem[] menuItems = new HungerBoxOffline.OrderItem[products.size()];
        int iterator = 0;
        for (OrderProductOffline orderProductOffline : products) {
            HungerBoxOffline.OrderItem menuItem = new HungerBoxOffline.OrderItem();
            menuItem.menuId = (int) orderProductOffline.getId();
            menuItem.quantity = (int) orderProductOffline.getQuantity();

            ArrayList<OrderSubProductOffline> subProducts = orderProductOffline.getSubProducts();
            int numOptionItems = 0;
            for (OrderSubProductOffline orderSubProductOffline : subProducts) {
                ArrayList<OrderOptionItemOffline> orderOptionitems = orderSubProductOffline.getOrderOptionItems();
                for (OrderOptionItemOffline orderOptionItemOffline : orderOptionitems) {
                    numOptionItems = numOptionItems + 1;
                }
            }
            int count = 0;
            menuItem.optionIds = new int[numOptionItems];
            for (OrderSubProductOffline orderSubProductOffline : subProducts) {
                ArrayList<OrderOptionItemOffline> orderOptionitems = orderSubProductOffline.getOrderOptionItems();
                for (OrderOptionItemOffline orderOptionItemOffline : orderOptionitems) {
                    menuItem.optionIds[count] = (int) orderOptionItemOffline.getId();
                    count++;
                }
            }
            menuItems[iterator] = menuItem;
            iterator++;

        }

        HungerBoxOffline.OrderData orderData = new HungerBoxOffline.OrderData((int)orderOffline.getOccasionId(),menuItems);
        boolean isOrderCreated = false;

        try {
            MainApplication mainApplication = (MainApplication) getApplication();
            HungerBoxOffline.EmployeeApp employeeApp = mainApplication.getHungerBoxOffline();
            String qrCode = employeeApp.encode(orderData);
            orderOffline.setQrCoder(qrCode);

            try {
                isOrderCreated = DbHandler.getDbHandler(getApplicationContext()).createOrderOffline(orderOffline);
            } catch (Exception e) {
                //todo clevertap
                e.printStackTrace();
                isOrderCreated = false;
                cleverTapEvent("DB Handling error");
            }
            setBarCode(qrCode);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cleverTapEvent(e.getMessage());
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
            cleverTapEvent(e.getMessage());
        }
        catch (SignatureException e) {
            e.printStackTrace();
            cleverTapEvent(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            cleverTapEvent("QR Code generation exception");
        }

        if (!isOrderCreated) {
            showOfflineDialog();
            cleverTapEvent("Error in creating order");
        }

    }

    private void cleverTapEvent(String errorMessage){
//        try{
//            HashMap<String,Object> place_order_map = new HashMap<>();
//            place_order_map.put(CleverTapEvent.PropertiesNames.getSource(),"OrderQrCodeActivityOffline");
//            place_order_map.put(CleverTapEvent.PropertiesNames.getMessage(),errorMessage==null?"No error message":errorMessage);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getQr_error(),place_order_map,OrderQrCodeActivityOffline.this);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
    }

    private void showOfflineDialog() {
        ErrorPopFragment popUpFragment = ErrorPopFragment.Companion
                .newInstance("Sorry ! Your Order Request Failed!", "OK", true,ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        navigateBack();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });
        popUpFragment.setCancelable(false);
        popUpFragment.show(getSupportFragmentManager(), "wallet_linking");
    }

    public void setBarCode(String qr) {

        try {
            Bitmap bitmap;
            int width = Math.round(AppUtils.convertDpToPixel(175, this));
            bitmap = QrUtil.encodeAsBitmap(qr, width);


            iv_barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public int getTotalOrderCount() {
        int qty = 0;
        if (orderOffline != null) {
            for (OrderProductOffline orderProduct : orderOffline.getProducts()) {
                qty += orderProduct.getQuantity();
            }
        }
        return qty;
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack() {
        if (isNewOrder) {
            if (AppUtils.isCafeApp() && AppUtils.getConfig(this).isAuto_logout()) {
                AppUtils.doLogout(this);
            } else {
                Intent intent = AppUtils.getHomeNavigationIntentOffline(this);
                intent.putExtra("showDialog", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        finish();
    }
}
