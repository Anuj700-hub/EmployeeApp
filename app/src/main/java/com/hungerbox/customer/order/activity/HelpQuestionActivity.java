package com.hungerbox.customer.order.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.adapter.HelpQuestionAdapter;
import com.hungerbox.customer.model.Help;
import com.hungerbox.customer.prelogin.activity.ParentActivity;

import java.util.ArrayList;

public class HelpQuestionActivity extends ParentActivity {

    private CoordinatorLayout crdl;
    private TextView headerText, subjectText;
    private RecyclerView recyclerView;
    private ArrayList<Help.Question> questions;
    private ImageView back;
    private int autoCollapse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        initializeView();

        headerText.setText(getIntent().getStringExtra("Header"));

        subjectText.setText(getIntent().getStringExtra("Subject"));
        Bundle args = getIntent().getBundleExtra("BUNDLE");

        autoCollapse = getIntent().getIntExtra("autoCollapse", 0);
        questions = (ArrayList<Help.Question>) args.getSerializable("ARRAYLIST");

        renderHelp();
    }

    private void initializeView() {
        crdl = findViewById(R.id.crdl);
        back = findViewById(R.id.arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        headerText = findViewById(R.id.headerText);
        findViewById(R.id.subject).setVisibility(View.VISIBLE);
        subjectText = findViewById(R.id.subjectText);
        recyclerView = findViewById(R.id.helpList);
    }

    private void renderHelp() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(HelpQuestionActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        HelpQuestionAdapter adapter = new HelpQuestionAdapter(questions, HelpQuestionActivity.this, autoCollapse);
        recyclerView.setAdapter(adapter);
    }
}

