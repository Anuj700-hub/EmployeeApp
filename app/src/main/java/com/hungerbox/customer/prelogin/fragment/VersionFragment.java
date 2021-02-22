package com.hungerbox.customer.prelogin.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.event.VersionViewCloseEvent;
import com.hungerbox.customer.util.AppUtils;

public class VersionFragment extends DialogFragment {

    private String title, desc, redirectURL;
    private boolean isHard;

    public VersionFragment() {
    }

    public static VersionFragment newInstance() {
        VersionFragment fragment = new VersionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            desc = getArguments().getString("desc");
            isHard = getArguments().getBoolean("isHard");
            if (AppUtils.isCafeApp()) {
                redirectURL = getArguments().getString("redirectURL");
            } else {
                redirectURL = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_version, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getDialog().setCanceledOnTouchOutside(false);
        ((TextView) (view.findViewById(R.id.title))).setText(title);
        ((TextView) (view.findViewById(R.id.desc))).setText(desc);
        ((TextView) (view.findViewById(R.id.title))).setText(title);


        if (isHard) {
            (view.findViewById(R.id.bt_later)).setVisibility(View.GONE);
            setCancelable(false);
        } else {
            setCancelable(true);
        }

        view.findViewById(R.id.bt_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        view.findViewById(R.id.bt_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        MainApplication.bus.post(new VersionViewCloseEvent());
        super.onDismiss(dialog);
    }

    private void update() {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(redirectURL));
        startActivity(viewIntent);
    }
}
