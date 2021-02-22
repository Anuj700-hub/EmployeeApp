package com.hungerbox.customer.order.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.util.AppUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FreeCartErrorHandleDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FreeCartErrorHandleDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreeCartErrorHandleDialog extends DialogFragment {

    String errorMessage;

    Button btPositiveButton, btNegativeButton;
    private OnFragmentInteractionListener mListener;
    Product product;
    private Vendor vendor;

    public FreeCartErrorHandleDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static FreeCartErrorHandleDialog newInstance(String error, OnFragmentInteractionListener listener) {
        FreeCartErrorHandleDialog fragment = new FreeCartErrorHandleDialog();
        fragment.errorMessage = error;
        fragment.mListener = listener;

        return fragment;
    }

    public static FreeCartErrorHandleDialog newInstance(Product product, Vendor vendor) {
        FreeCartErrorHandleDialog fragment = new FreeCartErrorHandleDialog();
        fragment.product = product;
        fragment.vendor = vendor;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_error_dialog
                , container, false);
        btPositiveButton = view.findViewById(R.id.bt_cart_positive);
        btNegativeButton = view.findViewById(R.id.bt_cart_negative);
        btNegativeButton.setVisibility(View.GONE);
        TextView tvErrorMessage = view.findViewById(R.id.tv_error_message);
        TextView tvErrorHeader = view.findViewById(R.id.tv_error_header);

        if (errorMessage != null)
            tvErrorMessage.setText(errorMessage);

        if(vendor!=null) {
            if (vendor.isVendingMachine()) {
                if(product!=null) {
                    tvErrorHeader.setText("Out Of Stock");
                    tvErrorMessage.setText(String.format("Oops, Cannot add %d items.\nWe only have %d items available", product.getMaxQty() + 1, product.getMaxQty()));
                }
            } else if (vendor.isSlotBookingVendor()) {
                tvErrorMessage.setText(AppUtils.getConfig(getContext()).getCongestion_msg());
            } else if(vendor.isSpaceBookingVendor()){
                tvErrorMessage.setText(vendor.cart_error);
                tvErrorHeader.setText("");
            }else {
                tvErrorMessage.setText(AppUtils.getConfig(getContext()).getFree_meal_exhaust_msg());
            }
        }


        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes click
                if (mListener != null) {
                    mListener.onFragmentInteraction();
                }

//                Activity activity = getActivity();
//                Intent intent = new Intent(activity.getApplicationContext(), GlobalActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                activity.startActivity(intent);
//                activity.finish();
                dismiss();
            }
        });

        btNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO uncomment after adding activity

//                Activity activity = getActivity();
//                Intent intent = new Intent(activity, LastOrderActivity.class);
//                intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, false);
//                activity.startActivity(intent);
//                activity.finish();
                dismiss();
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
        void onFragmentInteraction();
    }
}
