package com.hungerbox.customer.payment.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.model.PaymentMethodMethod;
import com.hungerbox.customer.model.PaymentMethodMethodResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.payment.ItemSelectedListener;
import com.hungerbox.customer.payment.adapter.NetBankingOptionItemAdapter;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetbankingActivity extends AppCompatActivity {

    ImageView ivBack;
    RecyclerView rvNetbankingOptions;
    Button btnSubmit;
    PaymentMethodMethod selectedPaymentMethodMethod;
    PaymentMethod paymentMethod;
    List<PaymentMethodMethod> paymentMethodMethodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netbanking);
        ivBack = findViewById(R.id.iv_back);
        rvNetbankingOptions = findViewById(R.id.rv_netbanking_options);
        btnSubmit = findViewById(R.id.btn_submit);

//        user = (User) getIntent().getSerializableExtra(ApplicationConstants.OTP_USER);
        paymentMethod = (PaymentMethod) getIntent().getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abortGoBack();
            }
        });

        rvNetbankingOptions.setLayoutManager(new LinearLayoutManager(this));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitClicked();
            }
        });

        fetchNetBankingOptions();
        setAbort();
    }

    private void setAbort() {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.ERROR_MESSAGE, "You aborted the transaction");
        setResult(Activity.RESULT_CANCELED, intent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        abortGoBack();
    }

    private void abortGoBack() {
        setAbort();
        goBack();
    }

    private void submitClicked() {
        for (PaymentMethodMethod paymentMethodMethod : paymentMethodMethodList) {
            if (paymentMethodMethod.isSelected()) {
                selectedPaymentMethodMethod = paymentMethodMethod;
                break;
            }
        }

        if (selectedPaymentMethodMethod != null) {
            goBackWithNetBankingOption(selectedPaymentMethodMethod);
        }

    }

    private void goBackWithNetBankingOption(PaymentMethodMethod selectedPaymentMethodMethod) {
        Intent intent = new Intent();
        paymentMethod.getNetBankingMethods().getNetBankingMethods().clear();
        paymentMethod.getNetBankingMethods().getNetBankingMethods().add(selectedPaymentMethodMethod);
        intent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethod);
        setResult(Activity.RESULT_OK, intent);
        goBack();
    }


    private void fetchNetBankingOptions() {
        String url = UrlConstant.JUSPAY_FETCH_NETBANKING_OPTIONS;
        SimpleHttpAgent<PaymentMethodMethodResponse> paymentMethodMethodResponseSimpleHttpAgent = new SimpleHttpAgent<PaymentMethodMethodResponse>(
                this,
                url,
                new ResponseListener<PaymentMethodMethodResponse>() {
                    @Override
                    public void response(PaymentMethodMethodResponse responseObject) {
                        if (responseObject != null && responseObject.getNetBankingMethods().size() > 0) {
                            Collections.sort(responseObject.getNetBankingMethods(), new Comparator<PaymentMethodMethod>() {
                                @Override
                                public int compare(PaymentMethodMethod o1, PaymentMethodMethod o2) {
                                    if (o1.getPriority() > o2.getPriority())
                                        return 1;
                                    else if (o1.getPriority() < o2.getPriority()) {
                                        return -1;
                                    } else
                                        return 0;
                                }
                            });
                            Collections.reverse(responseObject.getNetBankingMethods());
                            updateNetBankingOptions(responseObject.getNetBankingMethods());
                        } else {
                            onError("Unable to fetch netbanking options");
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        onError(error);
                    }
                },
                PaymentMethodMethodResponse.class
        );

        paymentMethodMethodResponseSimpleHttpAgent.get();
    }

    private void updateNetBankingOptions(ArrayList<PaymentMethodMethod> netBankingMethods) {
        this.paymentMethodMethodList = netBankingMethods;
        NetBankingOptionItemAdapter netBankingOptionItemAdapter = new NetBankingOptionItemAdapter(this
                , netBankingMethods, new ItemSelectedListener() {
            @Override
            public void itemSelected(Object object) {
                if (object instanceof PaymentMethodMethod) {
                    onPaymentSelected((PaymentMethodMethod) object);
                }

            }
        });
        rvNetbankingOptions.setAdapter(netBankingOptionItemAdapter);
    }

    private void onPaymentSelected(PaymentMethodMethod methodMethod) {
        for (PaymentMethodMethod paymentMethodMethod : this.paymentMethodMethodList) {
            if (!methodMethod.equals(paymentMethodMethod))
                paymentMethodMethod.setSelected(false);
        }
        methodMethod.setSelected(!methodMethod.isSelected());
        rvNetbankingOptions.getAdapter().notifyDataSetChanged();
    }

    private void onError(String error) {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethod);
        intent.putExtra(ApplicationConstants.ERROR_MESSAGE, error);
        setResult(Activity.RESULT_CANCELED, intent);
        goBack();
    }

    private void goBack() {
        finish();
    }
}
