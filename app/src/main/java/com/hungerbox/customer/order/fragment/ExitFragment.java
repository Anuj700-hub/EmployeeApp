package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.net.Uri;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExitFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExitFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "data";
    private static final String IS_CANCELABLE = "isCancelable";


    Button btPositive, btNegative;
    TextView tvTitle;

    String title, positiveButtonText, negativeButtonText;

    View.OnClickListener positiveButtonListener;
    View.OnClickListener negativeButtonListener;

    private OnFragmentInteractionListener mListener;

    public ExitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExitFragment newInstance() {
        return newInstance("Do you want to exit?", "Yes", "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public static ExitFragment newInstance(String title, String positiveButton, String negativeButton, View.OnClickListener positiveButtonListener, View.OnClickListener negativeButtonListener) {
        ExitFragment fragment = new ExitFragment();
        fragment.title = title;
        fragment.positiveButtonText = positiveButton;
        fragment.negativeButtonText = negativeButton;
        fragment.positiveButtonListener = positiveButtonListener;
        fragment.negativeButtonListener = negativeButtonListener;

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
        View view = inflater.inflate(R.layout.fragment_exit, container, false);
        btPositive = view.findViewById(R.id.tv_ok_exit);
        btNegative = view.findViewById(R.id.bt_no_exit);
        tvTitle = view.findViewById(R.id.tv_title);

        tvTitle.setText(title);
        btPositive.setText(positiveButtonText);
        btNegative.setText(negativeButtonText);

        btPositive.setOnClickListener(positiveButtonListener);
        btNegative.setOnClickListener(negativeButtonListener);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
        void onFragmentInteraction(Uri uri);
    }


}
