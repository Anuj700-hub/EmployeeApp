package com.hungerbox.customer.navmenu.fragment;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.UserFieldUpdate;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountDetailsUpdateDialog.OnFieldChangeListener} interface
 * to handle interaction events.
 * Use the {@link AccountDetailsUpdateDialog#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AccountDetailsUpdateDialog extends DialogFragment {

    Button btPositiveButton;
    TextInputEditText etField;
    TextView tvFieldTitle,tvheader;
    Config config;
    private OnFieldChangeListener mListener;
    private String field;
    private long userId;
    TextInputLayout etFieldBox;

    public AccountDetailsUpdateDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountDetailsUpdateDialog newInstance(OnFieldChangeListener listener, String field, long userId) {
        AccountDetailsUpdateDialog fragment = new AccountDetailsUpdateDialog();
        fragment.mListener = listener;
        fragment.field = field;
        fragment.userId = userId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_field_change, container, false);
        btPositiveButton = view.findViewById(R.id.bt_positive);
        tvFieldTitle = view.findViewById(R.id.tv_old_password_title);
        tvheader = view.findViewById(R.id.tv_header);
        etField = view.findViewById(R.id.et_old_password);
        etFieldBox = view.findViewById(R.id.et_old_passwordBox);

        if (field.equalsIgnoreCase(ApplicationConstants.PHONE_NUMBER)) {
            tvheader.setText("Enter Mobile Number");
            etField.setHint("Enter Mobile Number");
        } else if (field.equalsIgnoreCase(ApplicationConstants.EMAIL)) {
            tvFieldTitle.setText("Email");
            etField.setHint("Enter Email Address");
        }

        if (field.equalsIgnoreCase(ApplicationConstants.PHONE_NUMBER)) {
            tvheader.setText("Enter Mobile Number");
        } else if (field.equalsIgnoreCase(ApplicationConstants.EMAIL)) {
            tvFieldTitle.setText("Email");
            etFieldBox.setHint("Enter Email Address");
        } else {
            tvFieldTitle.setText("Name");
            etFieldBox.setHint("Enter Name");
        }


        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes click
                validateFields();
            }
        });
        etField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etField.setError(null);
            }
        });


        config = AppUtils.getConfig(getActivity());


        return view;

    }

    private void validateFields() {

        UserFieldUpdate updatedUser = new UserFieldUpdate();
        String value = etField.getText().toString();
        if (field.equalsIgnoreCase(ApplicationConstants.PHONE_NUMBER)) {
            value = value.trim();
            if (value.length() == 10) {
                updatedUser.setMobileNum(value);
            } else {
                etField.setError("Invalid Phone Number");
                return;
            }
        } else if (field.equalsIgnoreCase(ApplicationConstants.EMAIL)) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                updatedUser.setEmail(value);
            } else {
                etField.setError("Invalid Email");
                return;
            }
        } else if (field.equalsIgnoreCase(ApplicationConstants.PREF_NAME)) {
            if (!value.isEmpty())
                updatedUser.setName(value);
            else {
                etField.setError("Name Can't be Empty");
                return;
            }
        }
        performFieldChange(userId, updatedUser);

    }


    private void performFieldChange(long userId, final UserFieldUpdate fieldToUpdate) {
        String url = UrlConstant.CHANGE_EMPLOYEE_DETAILS + userId;
        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        if (mListener != null)
                        {
                            mListener.onPasswordChanged(fieldToUpdate.mobileNum);
                        }
                        dismissAllowingStateLoss();

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorResponse != null && errorResponse.message != null) {
                            etField.setError(errorResponse.message);
                        }
                    }
                },
                Object.class
        );

        objectSimpleHttpAgent.put(fieldToUpdate, new HashMap<String, JsonSerializer>());

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mListener!= null)
            mListener.onDismiss();

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
    public interface OnFieldChangeListener {
        // TODO: Update argument type and name
        void onPasswordChanged(String mobileNum);
        void onDismiss ();
    }
}
