package com.hungerbox.customer.util.view;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FatLiDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FatLiDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FatLiDialog extends DialogFragment {

    Button btPositiveButton;
    TextView tvErrorMessage;
    private int status;
    private Object payload;
    private String erroMessage;
    private OnFragmentInteractionListener mListener;
    private Button btCancel;
    private TextView tvErrorHeader;
    private String header;

    public FatLiDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static FatLiDialog newInstance(int status, Object payload, OnFragmentInteractionListener listener, String errorMessage) {
        FatLiDialog fragment = new FatLiDialog();
        fragment.status = status;
        fragment.payload = payload;
        fragment.mListener = listener;
        fragment.erroMessage = errorMessage;
        return fragment;
    }

    public static FatLiDialog newInstance(int status, Object payload, OnFragmentInteractionListener listener, String errorMessage, String header) {
        FatLiDialog fragment = new FatLiDialog();
        fragment.status = status;
        fragment.payload = payload;
        fragment.mListener = listener;
        fragment.erroMessage = errorMessage;
        fragment.header = header;
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
        View view = inflater.inflate(R.layout.fat_li_dialog, container, false);
        btPositiveButton = view.findViewById(R.id.bt_cart_positive);
        btCancel = view.findViewById(R.id.bt_cart_negative);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        tvErrorHeader = view.findViewById(R.id.tv_error_header);


        if (erroMessage != null)
            tvErrorMessage.setText(erroMessage);

        if (header != null)
            tvErrorHeader.setText(header);

        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes click
                if (mListener != null) {
                    mListener.onFragmentInteraction(status, payload);
                }
                dismissAllowingStateLoss();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
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
        void onFragmentInteraction(int status, Object payload);
    }
}
