package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.DeskReferenceSetting;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.util.AppUtils;

import java.util.HashMap;
import java.util.regex.Pattern;

public class EmailActivationDialog extends DialogFragment {


    private OnFragmentInteractionListener mListener;
    private TextInputLayout txtInputLayout;
    private EditText etEmail;
    private TextView tvMessage;
    private Button btPositive, btNegative;
    private String email;
    private String message;
    private String positiveButtonText;
    private String negativeButtonText;
    String tagForApiRequest = "";


    public EmailActivationDialog(){

    }

    public static EmailActivationDialog newInstance(String email,OnFragmentInteractionListener mListener){
        EmailActivationDialog emailActivationDialog = new EmailActivationDialog();
        emailActivationDialog.mListener = mListener;
        emailActivationDialog.email = email;
        return emailActivationDialog;
    }

    public static EmailActivationDialog newInstance(String email,String message,String positiveButtonText,String negativeButtonText,OnFragmentInteractionListener mListener){
        EmailActivationDialog emailActivationDialog = new EmailActivationDialog();
        emailActivationDialog.mListener = mListener;
        emailActivationDialog.email = email;
        emailActivationDialog.message = message;
        emailActivationDialog.positiveButtonText = positiveButtonText;
        emailActivationDialog.negativeButtonText = negativeButtonText;
        return emailActivationDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getActivity() != null){
            if(getActivity() instanceof GlobalActivity){
                tagForApiRequest = ((GlobalActivity) getActivity()).getApiTag();
            }
        }
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //View view = inflater.inflate(R.layout.fragment_email_act_dialog, container, false);
        View view = inflater.inflate(R.layout.fragment_email_act_dialog_rl, container, false);
        txtInputLayout = view.findViewById(R.id.txtInputLayout);
        tvMessage = view.findViewById(R.id.tv_message);
        etEmail = view.findViewById(R.id.et_email);
        btPositive = view.findViewById(R.id.bt_positive);
        btNegative = view.findViewById(R.id.bt_negative);

        if (txtInputLayout!=null){
            txtInputLayout.setHint("Email");
        }

        if(email!=null&& !email.trim().isEmpty()){
            etEmail.setText(email);
            etEmail.setSelection(etEmail.getText().length());
        }
        if (message!=null){
            tvMessage.setText(message);
        }
        if (positiveButtonText!=null){
            btPositive.setText(positiveButtonText);
        }
        if (negativeButtonText!=null){
            btNegative.setText(negativeButtonText);
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(!AppUtils.getConfig(getActivity()).getEmail_pattern().equals("")){
            pattern = Pattern.compile(AppUtils.getConfig(getActivity()).getEmail_pattern(), Pattern.CASE_INSENSITIVE);
        }

        final Pattern finalPattern = pattern;
        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboard(getActivity(), etEmail);
                if(etEmail.getText().toString().trim().isEmpty()) {
                    etEmail.setError(AppUtils.getConfig(getContext()).getEmail_verification_ask_msg());
                    return;
                }
                if (!finalPattern.matcher(etEmail.getText()).matches()){
                    etEmail.setError(AppUtils.getConfig(getContext()).getEmail_verification_ask_msg());
                    return;
                }
                btPositive.setEnabled(false);
                mListener.onPositiveInteraction(etEmail.getText().toString());
            }
        });

        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.hideKeyboard(getActivity(), etEmail);

                dismissAllowingStateLoss();

                if(mListener!=null)
                    mListener.onNegativeInteraction();
            }
        });

        return view;
    }

    private void updateUserSetting(final String ref) {
        DeskReferenceSetting userSettings = new DeskReferenceSetting();
        userSettings.setDeskReference(ref);

        SimpleHttpAgent<DeskReferenceSetting> settingsHttpAgent = new SimpleHttpAgent<DeskReferenceSetting>(getContext(),
                UrlConstant.SET_USER_SETTINGS, new ResponseListener<DeskReferenceSetting>() {
            @Override
            public void response(DeskReferenceSetting responseObject) {
                dismissAllowingStateLoss();
                if(mListener!=null){
                    mListener.onPositiveInteraction(ref);
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                if(error!= null && !error.equals("")){
                    AppUtils.showToast(error,true, 0);
                }else{
                    AppUtils.showToast("Some error occured",true, 0);
                }
            }
        }, DeskReferenceSetting.class);
        settingsHttpAgent.post(userSettings,new HashMap<String, JsonSerializer>(), tagForApiRequest);
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
        void onPositiveInteraction(String email);

        void onNegativeInteraction();

        void onDismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        AppUtils.hideKeyboard(getActivity(), etEmail);
        super.onDismiss(dialog);
        if(mListener != null){
            mListener.onDismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        AppUtils.hideKeyboard(getActivity(), etEmail);
        super.onCancel(dialog);
    }
}

