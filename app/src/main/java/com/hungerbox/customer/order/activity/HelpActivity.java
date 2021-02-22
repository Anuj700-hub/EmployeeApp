package com.hungerbox.customer.order.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.adapter.HelpAdapter;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Help;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;

public class HelpActivity extends ParentActivity {

    private CoordinatorLayout crdl;
    private TextView headerText;
    private RecyclerView recyclerView;
    private ImageView back;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        initializeView();

        headerText.setText(getIntent().getStringExtra("Header"));
        getHelpData();
    }

    private void initializeView() {
        crdl = findViewById(R.id.crdl);
        back = findViewById(R.id.arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        headerText = findViewById(R.id.headerText);
        recyclerView = findViewById(R.id.helpList);
    }

    private void getHelpData() {

        String url = UrlConstant.GET_HELP_API;
        SimpleHttpAgent<Help> helpHttpAgent = new SimpleHttpAgent<>(this,
                url, new ResponseListener<Help>() {
            @Override
            public void response(Help responseObject) {
                if (responseObject != null) {

                    LinearLayoutManager layoutManager = new LinearLayoutManager(HelpActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    HelpAdapter adapter = new HelpAdapter(responseObject.getData(), HelpActivity.this, getIntent().getStringExtra("Header"));
                    recyclerView.setAdapter(adapter);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(crdl, "Some error occured. please try again", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getHelpData();
                                }
                            });
                    snackbar.show();
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                if (error == null || error.equals("")) {
                    error = "Some error occured. please try again";
                }
                Snackbar snackbar = Snackbar
                        .make(crdl, error, Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getHelpData();
                            }
                        });
                snackbar.show();
            }
        }, Help.class);
        helpHttpAgent.get();
    }
}
