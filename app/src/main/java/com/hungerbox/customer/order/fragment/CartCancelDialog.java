package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartCancelDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartCancelDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartCancelDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button btPositiveButton, btNegativeButton;
    // TODO: Rename and change types of parameters
    private Vendor vendor;
    private Product product;
    private boolean isbuffet;
    private OnFragmentInteractionListener mListener;

    public CartCancelDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vendor  Parameter 1.
     * @param product Parameter 2.
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static CartCancelDialog newInstance(Vendor vendor, Product product,
                                               OnFragmentInteractionListener listener,
                                               boolean isbuffet) {
        CartCancelDialog fragment = new CartCancelDialog();
        fragment.vendor = vendor;
        fragment.product = product;
        fragment.mListener = listener;
        fragment.isbuffet = isbuffet;

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
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_cart_cancel_dialog, container, false);
        btPositiveButton = view.findViewById(R.id.bt_cart_positive);
        btNegativeButton = view.findViewById(R.id.bt_cart_negative);

        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes click
                if (mListener != null) {
                    mListener.onFragmentInteraction(vendor, product, isbuffet);
                }

                dismiss();
            }
        });

        btNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        void onFragmentInteraction(Vendor vendor, Product product, boolean isBuffet);
    }
}
