package com.hungerbox.customer.util.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenericPopUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenericPopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenericPopUpFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "data";
    private static final String IS_CANCELABLE = "isCancelable";


    Button btPositive, btNegative;
    TextView tvTitle, tvPolicy;

    String title, positiveButtonText, negativeButtonText;

    private OnFragmentInteractionListener mListener;
    private boolean hideNegativeButton = false, hasPolicyCheck = false;
    private String headerText = "";
    private TextView popupHeader;
    private Button btNeutral;
    private LinearLayout llBtns;
    private int textAlignment = Gravity.CENTER;

    public GenericPopUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenericPopUpFragment newInstance(String message, String positiveButtonText,
                                                   OnFragmentInteractionListener listener) {
        return newInstance(message, positiveButtonText, "Cancel", listener);
    }


    public static GenericPopUpFragment newInstance(String title,
                                                   String positiveButton, String negativeButton,
                                                   OnFragmentInteractionListener listener) {
        GenericPopUpFragment fragment = new GenericPopUpFragment();
        fragment.title = title;
        fragment.positiveButtonText = positiveButton;
        fragment.negativeButtonText = negativeButton;
        fragment.mListener = listener;
        return fragment;
    }

    public static GenericPopUpFragment newInstance(String title,
                                                   String positiveButton, boolean hideNegativeButton,
                                                   OnFragmentInteractionListener listener) {
        GenericPopUpFragment fragment = new GenericPopUpFragment();
        fragment.title = title;
        fragment.positiveButtonText = positiveButton;
        fragment.hideNegativeButton = hideNegativeButton;
        fragment.mListener = listener;
        return fragment;
    }

    public static GenericPopUpFragment newInstance(String title,
                                                   String positiveButton, boolean hideNegativeButton, int textAlignment,
                                                   OnFragmentInteractionListener listener) {
        GenericPopUpFragment fragment = new GenericPopUpFragment();
        fragment.title = title;
        fragment.positiveButtonText = positiveButton;
        fragment.hideNegativeButton = hideNegativeButton;
        fragment.mListener = listener;
        fragment.textAlignment = textAlignment;
        return fragment;
    }

    public static GenericPopUpFragment newInstance(String title,
                                                   String positiveButton, boolean hideNegativeButton, boolean hasPolicyCheck, String headerText,
                                                   OnFragmentInteractionListener listener) {
        GenericPopUpFragment fragment = new GenericPopUpFragment();
        fragment.title = title;
        fragment.positiveButtonText = positiveButton;
        fragment.hideNegativeButton = hideNegativeButton;
        fragment.mListener = listener;
        fragment.hasPolicyCheck = hasPolicyCheck;
        fragment.headerText = headerText;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_generic_pop_up, container, false);
        btPositive = view.findViewById(R.id.tv_ok_exit);
        btNegative = view.findViewById(R.id.bt_no_exit);
        btNeutral = view.findViewById(R.id.neutral_button);
        tvTitle = view.findViewById(R.id.tv_title);
        llBtns = view.findViewById(R.id.ll_btns);
        tvPolicy = view.findViewById(R.id.tv_privacy_policy);
        popupHeader = view.findViewById(R.id.popup_header);

        tvTitle.setText(title);

        tvTitle.setGravity(textAlignment);

        if(hasPolicyCheck){
            tvPolicy.setVisibility(View.VISIBLE);
            SpannableString content = new SpannableString("Read our Privacy Policy here");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tvPolicy.setText(content);

            tvPolicy.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(UrlConstant.PRIVACY_POLICY));
                startActivity(i);
            });
        }

        if(headerText != null && !headerText.isEmpty()){
            popupHeader.setVisibility(View.VISIBLE);
            popupHeader.setText(headerText);
        }

        btPositive.setText(positiveButtonText);
        btNegative.setText(negativeButtonText);
        if (hideNegativeButton) {
            llBtns.setVisibility(View.GONE);
            btNeutral.setVisibility(View.VISIBLE);
        }

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
                if(mListener!=null)
                    mListener.onPositiveInteraction();
            }
        });

        btNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (mListener != null)
                    mListener.onPositiveInteraction();
            }
        });

        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
                if (mListener != null)
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
        void onPositiveInteraction();

        void onNegativeInteraction();
    }
}
