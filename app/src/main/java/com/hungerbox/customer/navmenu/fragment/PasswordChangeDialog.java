package com.hungerbox.customer.navmenu.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.ChangePassword;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.LoginActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PasswordChangeDialog.OnPasswordChangeListener} interface
 * to handle interaction events.
 * Use the {@link PasswordChangeDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordChangeDialog extends DialogFragment {

    Button btPositiveButton, btNegativeButton;
    TextInputEditText etOldPassword, etNewPassword, etNewConfirmPassword;
    TextInputLayout tilOldPassword, tilNewPassword, tilNewConfirmPassword;
    TextView tvOldPasswordTitle;
    Config config;
    private OnPasswordChangeListener mListener;
    ProgressBar pbLoad;

    public PasswordChangeDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordChangeDialog newInstance(OnPasswordChangeListener listener) {
        PasswordChangeDialog fragment = new PasswordChangeDialog();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_password_change, container, false);
        btPositiveButton = view.findViewById(R.id.bt_positive);
        btNegativeButton = view.findViewById(R.id.bt_negative);
        tvOldPasswordTitle = view.findViewById(R.id.tv_old_password_title);
        etOldPassword = view.findViewById(R.id.et_old_password);
        etNewPassword = view.findViewById(R.id.et_new_pass1);
        etNewConfirmPassword = view.findViewById(R.id.et_new_pass2);
        tilOldPassword = view.findViewById(R.id.til_old_password);
        tilNewPassword = view.findViewById(R.id.til_new_pass1);
        tilNewConfirmPassword = view.findViewById(R.id.til_new_pass2);
        pbLoad = view.findViewById(R.id.pb_reset_password);

        config = AppUtils.getConfig(getActivity());

        if(!config.isSkip_reset()){
            btNegativeButton.setVisibility(View.GONE);
            setCancelable(false);
        }

        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes clickbtPositiveButton
                performPasswordChange();
            }
        });

        btNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setResetRequired(false);
                if (mListener != null)
                    mListener.onCancel();
                dismiss();
            }
        });

        etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                    tilNewPassword.setPasswordVisibilityToggleTintList(colorStateList);
                }
                else
                {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black_40));
                    tilNewPassword.setPasswordVisibilityToggleTintList(colorStateList);
                }
            }
        });
        etNewConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                    tilNewConfirmPassword.setPasswordVisibilityToggleTintList(colorStateList);
                }
                else{
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                    tilNewConfirmPassword.setPasswordVisibilityToggleTintList(colorStateList);
                }
            }
        });
        if (config.isPassword_change_on_nfc()) {
            tvOldPasswordTitle.setVisibility(View.GONE);
            etOldPassword.setVisibility(View.GONE);
        }

        return view;
    }

    private void setResetRequired(boolean b) {
        String url = UrlConstant.CHANGE_PASSWORD;
        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorResponse != null && errorResponse.message != null) {
                            etNewConfirmPassword.setError(errorResponse.message);
                        }
                    }
                },
                Object.class
        );

        objectSimpleHttpAgent.post(new ChangePassword().setResetRequired(false), new HashMap<String, JsonSerializer>());

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mListener!=null) {
            mListener.onCancel();
        }
    }

    private void performPasswordChange() {

        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();

        if (isInputValid()){
            long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
            performPasswordChange(userId, oldPassword, newPassword);
        }


    }

    private boolean isInputValid(){
        boolean isInputValid = true;
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String newConfirmedPassword = etNewConfirmPassword.getText().toString();

        if ((oldPassword == null || oldPassword.isEmpty()) && !config.isPassword_change_on_nfc()) {
            tilOldPassword.setErrorEnabled(true);
            tilOldPassword.setError("Please fill your old password");
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.vibrate);
            tilOldPassword.startAnimation(shake);
            isInputValid = false;
            return isInputValid;
        } else{
            tilOldPassword.setErrorEnabled(false);
        }

        if (newPassword == null || newPassword.isEmpty()) {
            tilNewPassword.setErrorEnabled(true);
            tilNewPassword.setError("Please fill your new password");
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.vibrate);
            tilNewPassword.startAnimation(shake);
            isInputValid = false;
            return isInputValid;
        } else{
            tilNewPassword.setErrorEnabled(false);
        }

        if (newPassword != null && newPassword.length() < 6) {
            tilNewPassword.setErrorEnabled(true);
            etNewPassword.setText("");
            tilNewPassword.setError("The password length should be more than 6 digits");
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.vibrate);
            tilNewPassword.startAnimation(shake);
            isInputValid = false;
            return isInputValid;
        } else{
            tilNewPassword.setErrorEnabled(false);
        }

        if (newConfirmedPassword == null || newConfirmedPassword.isEmpty()) {
            tilNewConfirmPassword.setErrorEnabled(true);
            tilNewConfirmPassword.setError("Please fill your new password");
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.vibrate);
            tilNewConfirmPassword.startAnimation(shake);
            isInputValid = false;
            return isInputValid;
        } else{
            tilNewConfirmPassword.setErrorEnabled(false);
        }

        if (newConfirmedPassword != null && newConfirmedPassword.length() < 6) {
            etNewConfirmPassword.setText("");
            tilNewConfirmPassword.setErrorEnabled(true);
            tilNewConfirmPassword.setError("The password length should be more than 6 digits");
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.vibrate);
            tilNewConfirmPassword.startAnimation(shake);
            isInputValid = false;
            return isInputValid;
        } else{
            tilNewConfirmPassword.setErrorEnabled(false);
        }

        if (!newPassword.equals(newConfirmedPassword)) {
            etNewConfirmPassword.setText("");
            tilNewConfirmPassword.setErrorEnabled(true);
            tilNewConfirmPassword.setError("Your new password did not match");
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.vibrate);
            tilNewConfirmPassword.startAnimation(shake);
            isInputValid = false;
            return isInputValid;
        } else{
            tilNewConfirmPassword.setErrorEnabled(false);
        }

        return isInputValid;


    }

    private void performPasswordChange(long userId, String oldPassword, String newPassword) {
        String url = UrlConstant.CHANGE_PASSWORD;
        pbLoad.setVisibility(View.VISIBLE);
        btPositiveButton.setEnabled(false);
        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        pbLoad.setVisibility(View.GONE);
                        if (mListener != null)
                            mListener.onPasswordChanged();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbLoad.setVisibility(View.GONE);
                        if (errorResponse != null && errorResponse.message != null) {
                            btPositiveButton.setEnabled(true);
                            AppUtils.showToast(errorResponse.message,true, 0);
                        }
                    }
                },
                Object.class
        );

        objectSimpleHttpAgent.post(new ChangePassword().setUserId(userId).setOldPassword(oldPassword).setPassword(newPassword).setPasswordConfirmation(newPassword), new HashMap<String, JsonSerializer>());

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPasswordChangeListener {
        // TODO: Update argument type and name
        void onPasswordChanged();

        void onCancel();
    }
}
