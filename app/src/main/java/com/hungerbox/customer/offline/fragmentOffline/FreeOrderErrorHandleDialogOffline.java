package com.hungerbox.customer.offline.fragmentOffline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;

public class FreeOrderErrorHandleDialogOffline extends DialogFragment {

    OrderOffline order;
    String errorMessage, positiveButtonText, negativeButtonText;

    Button btPositiveButton, btNegativeButton;
    private OnFragmentInteractionListener mListener;

    public FreeOrderErrorHandleDialogOffline() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static FreeOrderErrorHandleDialogOffline newInstance(OrderOffline order,
                                                         String error, OnFragmentInteractionListener listener) {
        FreeOrderErrorHandleDialogOffline fragment = new FreeOrderErrorHandleDialogOffline();
        fragment.order = order;
        fragment.errorMessage = error;
        fragment.mListener = listener;

        return fragment;
    }

    public static FreeOrderErrorHandleDialogOffline newInstance(OrderOffline order, String error, OnFragmentInteractionListener listener, String positiveButtonText) {
        FreeOrderErrorHandleDialogOffline fragment = new FreeOrderErrorHandleDialogOffline();
        fragment.order = order;
        fragment.errorMessage = error;
        fragment.mListener = listener;
        fragment.positiveButtonText = positiveButtonText;
        return fragment;
    }


    public static FreeOrderErrorHandleDialogOffline newInstance(OrderOffline order, String error, OnFragmentInteractionListener listener,
                                                         String positiveButtonText, String negativeButtonText) {
        FreeOrderErrorHandleDialogOffline fragment = newInstance(order, error, listener, positiveButtonText);
        fragment.negativeButtonText = negativeButtonText;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_free_error_dialog, container, false);
        btPositiveButton = view.findViewById(R.id.bt_cart_positive);
        btNegativeButton = view.findViewById(R.id.bt_cart_negative);
        TextView tvErrorMessage = view.findViewById(R.id.tv_error_message);

        if (errorMessage != null)
            tvErrorMessage.setText(errorMessage);

        if (positiveButtonText != null)
            btPositiveButton.setText(positiveButtonText);

        if (negativeButtonText == null) {
            btNegativeButton.setVisibility(View.GONE);
        } else {
            btNegativeButton.setVisibility(View.VISIBLE);
            btNegativeButton.setText(negativeButtonText);
        }


        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes click
                if (mListener != null) {
                    mListener.onPositiveButtonClicked();
                    dismiss();
                } else {
                    Activity activity = getActivity();
//                Intent intent = new Intent(activity.getApplicationContext(), GlobalActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                activity.startActivity(intent);
                    activity.finish();
                    dismiss();
                }
            }
        });

        btNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNegativeButtonClicked();
                    dismiss();
                } else {
                    //TODO Uncomment after adding OrderView Activity
//                    Activity activity = getActivity();
//                    Intent intent = new Intent(activity, OrderViewActivity.class);
//                    intent.putExtra(ApplicationConstants.ORDER, order);
//                    intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, false);
//                    activity.startActivity(intent);
//                    activity.finish();
//                    dismiss();
                }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPositiveButtonClicked();

        void onNegativeButtonClicked();
    }
}
