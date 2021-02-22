package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;

public class OrderCancellationReasonDialog extends DialogFragment {

    EditText etReason;
    Button btOk;
    private OnFragmentInteractionListener mListener;
    private TextView reasonTextView;
    private TextInputLayout txtInputLayout;

    public OrderCancellationReasonDialog(){

    }

    public static OrderCancellationReasonDialog newInstance(OnFragmentInteractionListener mListener){
        OrderCancellationReasonDialog orderCancellationReasonDialog = new OrderCancellationReasonDialog();
        orderCancellationReasonDialog.mListener = mListener;
        return orderCancellationReasonDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = inflater.inflate(R.layout.fragment_order_cancellation_dialog, container, false);

        etReason = view.findViewById(R.id.reason);
        txtInputLayout = view.findViewById(R.id.txtInputLayout);
        btOk = view.findViewById(R.id.bt_ok);
        reasonTextView = view.findViewById(R.id.reason_txt);
        reasonTextView.setText("Could you please state the reason for cancellation?");

        btOk.setOnClickListener(v -> {
            AppUtils.hideKeyboard(getActivity(), etReason);
            String reason = etReason.getText().toString().trim();
            if(reason.equalsIgnoreCase("")){
                AppUtils.showToast("Please Enter a reason", false, 0);
            }
            else{
                dismissAllowingStateLoss();
                if(mListener!=null)
                    mListener.onPositiveInteraction(reason);
            }

        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onPositiveInteraction(String reason);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        AppUtils.hideKeyboard(getActivity(), etReason);
        super.onCancel(dialog);
    }
}
