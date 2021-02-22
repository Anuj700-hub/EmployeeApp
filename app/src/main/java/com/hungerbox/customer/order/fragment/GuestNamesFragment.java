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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderGuestInfo;
import com.hungerbox.customer.model.OrderGuestInfoView;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.EditTextCreator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuestNamesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuestNamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuestNamesFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "data";
    private static final String IS_CANCELABLE = "isCancelable";


    Button btPositive, btNegative;
    TextView tvTitle;

    String title, positiveButtonText;

    private OnFragmentInteractionListener mListener;
    private int count;
    private LinearLayout llContainer;
    private ArrayList<OrderGuestInfoView> editTextList;

    public GuestNamesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionFragment.
     */


    public static GuestNamesFragment newInstance(String title,
                                                 String positiveButton, int count
            , OnFragmentInteractionListener listener) {
        GuestNamesFragment fragment = new GuestNamesFragment();
        fragment.title = title;
        fragment.positiveButtonText = positiveButton;
        fragment.count = count;
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
        setCancelable(true);
        View view = inflater.inflate(R.layout.fragment_guest_names, container, false);
        btPositive = view.findViewById(R.id.tv_ok_exit);
        tvTitle = view.findViewById(R.id.tv_title);
        llContainer = view.findViewById(R.id.ll_guest_names);
        tvTitle.setText(title);
        btPositive.setText(positiveButtonText);
        EditTextCreator editTextCreator = new EditTextCreator();
        editTextList = editTextCreator.createGuestOrderViews(count, llContainer, getActivity());

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<OrderGuestInfo> guestNamesList = new ArrayList<>();
                for (OrderGuestInfoView orderGuestInfoView : editTextList) {
                    if (orderGuestInfoView.affiliation.getText().toString().length() > 2) {
                        OrderGuestInfo orderGuestInfo = new OrderGuestInfo();
//                        orderGuestInfo.setGuestName(orderGuestInfoView.guestName.getText().toString());
                        orderGuestInfo.setAffiliation(orderGuestInfoView.affiliation.getText().toString());
                        guestNamesList.add(orderGuestInfo);
                    } else {
                        if (orderGuestInfoView.affiliation.getText().toString().length() <= 2) {
                            AppUtils.showToast("Please enter valid Affiliation / Company name", false, 2);
                        } else
                            AppUtils.showToast("Please enter the Affiliations/Company names of all your Guests", false, 2);
                        return;
                    }

                }
                mListener.onPositiveInteraction(guestNamesList);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPositiveInteraction(ArrayList guestList);

        void onNegativeInteraction();
    }
}
