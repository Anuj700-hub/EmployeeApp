package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.ApplicationConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuestPopUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuestPopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuestPopUpFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "data";
    private static final String IS_CANCELABLE = "isCancelable";


    Button btPositive, btNegative;
    TextView tvTitle, tvError;

    String title, positiveButtonText, negativeButtonText;

    private OnFragmentInteractionListener mListener;
    private RadioGroup rgGuestType;


    public GuestPopUpFragment() {
        // Required empty public constructor
    }


    public static GuestPopUpFragment newInstance(OnFragmentInteractionListener listener) {
        GuestPopUpFragment fragment = new GuestPopUpFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        View view = inflater.inflate(R.layout.fragment_guest_dialog, container, false);
        btPositive = view.findViewById(R.id.tv_ok_exit);
        btNegative = view.findViewById(R.id.bt_no_exit);
        rgGuestType = view.findViewById(R.id.rg_guest_type);
        tvError = view.findViewById(R.id.tv_error);
//        rgGuestType.check(R.id.rb_company_guest);
        tvError.setVisibility(View.GONE);
        rgGuestType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rgGuestType.findViewById(checkedId);
                int idx = rgGuestType.indexOfChild(radioButton);
                tvError.setVisibility(View.VISIBLE);
                tvError.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                if (idx == 0)
                    tvError.setText(getString(R.string.official_guest_text_msg));
                else if (idx > 0)
                    tvError.setText(getString(R.string.personal_guest_text_msg));
            }
        });
        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String guestType;
                int radioButtonID = rgGuestType.getCheckedRadioButtonId();
                View radioButton = rgGuestType.findViewById(radioButtonID);
                int idx = rgGuestType.indexOfChild(radioButton);
                if (idx == 0)
                    guestType = ApplicationConstants.GUEST_TYPE_OFFICIAL;
                else if (idx > 0)
                    guestType = ApplicationConstants.GUEST_TYPE_PERSONAL;
                else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(getString(R.string.guest_selection_error));
                    tvError.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    return;
                }

                if (mListener != null)
                    mListener.onPositiveInteraction(guestType);
                dismiss();

            }
        });

        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mListener.onNegativeInteraction();
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPositiveInteraction(String guestType);

        void onNegativeInteraction();
    }
}
