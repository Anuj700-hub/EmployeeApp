package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.content.DialogInterface;
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
 * {@link AddGuestDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddGuestDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGuestDialog extends DialogFragment {


    Button btPositiveButton;
    TextView tvAddGuest;
    private OnFragmentInteractionListener mListener;

    public AddGuestDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGuestDialog newInstance(OnFragmentInteractionListener listener) {
        AddGuestDialog fragment = new AddGuestDialog();
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
        View view = inflater.inflate(R.layout.fragment_add_guest, container, false);
        btPositiveButton = view.findViewById(R.id.bt_continue);
        tvAddGuest = view.findViewById(R.id.tv_add_guest);
        tvAddGuest.setText("Your Request has been sent for approval \n" +
                "You will be notified on when it is approved.");

        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes click
                if (mListener != null) {
                    mListener.onFragmentInteraction();
                }

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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }

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
