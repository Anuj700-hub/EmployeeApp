package com.hungerbox.customer.order.fragment;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.hungerbox.customer.R;
import com.hungerbox.customer.order.listeners.CancelListener;
import com.hungerbox.customer.order.listeners.RetryListener;

public class NoNetFragment extends DialogFragment {
    Button btPositive, btNegative;
    RetryListener retryListener;
    CancelListener cancelListener = null;

    private OnFragmentInteractionListener mListener;

    public NoNetFragment() {
    }

    public static NoNetFragment newInstance(RetryListener retryListener) {
        NoNetFragment fragment = new NoNetFragment();
        fragment.retryListener = retryListener;
        return fragment;
    }

    public static NoNetFragment newInstance(RetryListener retryListener , CancelListener cancelListener){
        NoNetFragment fragment = new NoNetFragment();
        fragment.retryListener = retryListener;
        fragment.cancelListener = cancelListener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_no_net, container, false);
        btPositive = view.findViewById(R.id.tv_ok_exit);
        btNegative = view.findViewById(R.id.bt_no_exit);

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (retryListener != null) {
                    retryListener.onRetry();
                }
                dismiss();
            }
        });

        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    if(cancelListener!=null){
                        cancelListener.onCancel();
                    }else {
                        getActivity().finish();
                    }
                }
                dismiss();
            }
        });
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
