package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
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
import com.hungerbox.customer.mvvm.view.MyAccountActivity;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.util.AppUtils;

import java.util.HashMap;

public class DeskReferenceChangeDialog extends DialogFragment {


    EditText etDeskReference;
    Button btExit,btChange;
    String deskRef = null;
    private OnFragmentInteractionListener mListener;
    boolean hideExitButton = false;
    private TextView deskTextView;
    private TextInputLayout txtInputLayout;
    String tagForApiRequest = "";


    public DeskReferenceChangeDialog(){

    }

    public static DeskReferenceChangeDialog newInstance(String deskRef,boolean hideExitButton,OnFragmentInteractionListener mListener){
        DeskReferenceChangeDialog deskReferenceChangeDialog = new DeskReferenceChangeDialog();
        deskReferenceChangeDialog.deskRef = deskRef;
        deskReferenceChangeDialog.mListener = mListener;
        deskReferenceChangeDialog.hideExitButton = hideExitButton;
        return deskReferenceChangeDialog;
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
            else if(getActivity() instanceof MyAccountActivity){
                tagForApiRequest = ((MyAccountActivity) getActivity()).getApiTag();
            }
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_desk_reference_dialog, container, false);
        etDeskReference = view.findViewById(R.id.et_desk_reference);
        txtInputLayout = view.findViewById(R.id.txtInputLayout);
        btExit = view.findViewById(R.id.bt_exit);
        btChange = view.findViewById(R.id.bt_change);
        deskTextView = view.findViewById(R.id.desk_text);

        deskTextView.setText(AppUtils.getConfig(getContext().getApplicationContext()).getWorkstation_address());
        txtInputLayout.setHint(AppUtils.getConfig(getContext().getApplicationContext()).getWorkstation_address());
        if(deskRef!=null&& !deskRef.trim().isEmpty()){
            etDeskReference.setText(deskRef);
            etDeskReference.setSelection(etDeskReference.getText().length());
        }

        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboard(getActivity(), etDeskReference);
                if(etDeskReference.getText().toString().trim().isEmpty()) {
                    etDeskReference.setError(AppUtils.getConfig(getContext()).getDesk_refrence_ask_msg());
                    return;
                }
                updateUserSetting(etDeskReference.getText().toString());
            }
        });

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.hideKeyboard(getActivity(), etDeskReference);

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
        void onPositiveInteraction(String ref);

        void onNegativeInteraction();

        void onDissmis();
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
    public void onDismiss(DialogInterface dialog) {
        AppUtils.hideKeyboard(getActivity(), etDeskReference);
        super.onDismiss(dialog);
        if(mListener != null){
            mListener.onDissmis();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        AppUtils.hideKeyboard(getActivity(), etDeskReference);
        super.onCancel(dialog);
    }
}
