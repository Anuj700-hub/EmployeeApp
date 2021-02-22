package com.hungerbox.customer.health;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OnBoard;

public class HealthOnBoardingFragment extends Fragment {

    TextView tvText;
    OnBoard onBoard;
    private com.hungerbox.customer.prelogin.fragment.OnBoardingFragment.OnFragmentInteractionListener mListener;
    private ImageView ivIcon;

    public HealthOnBoardingFragment() {
    }

    public static HealthOnBoardingFragment newInstance(int position, int length, OnBoard onBoard) {
        HealthOnBoardingFragment fragment = new HealthOnBoardingFragment();
        fragment.onBoard = onBoard;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_boarding_health, container, false);
        tvText = view.findViewById(R.id.tv_onborad);
        ivIcon = view.findViewById(R.id.iv_onboarding_icon);

        ivIcon.setImageResource(onBoard.getImageId());
        tvText.setText(onBoard.getTitle());
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
