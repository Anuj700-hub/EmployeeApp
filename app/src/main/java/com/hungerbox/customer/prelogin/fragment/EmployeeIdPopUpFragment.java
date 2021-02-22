package com.hungerbox.customer.prelogin.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmployeeIdPopUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmployeeIdPopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeIdPopUpFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "data";
    private static final String IS_CANCELABLE = "isCancelable";


    Button btPositive, btNegative;
    TextView tvTitle;

    String title, positiveButtonText, negativeButtonText;

    private OnFragmentInteractionListener mListener;
    private EditText etEmpId;

    public EmployeeIdPopUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionFragment.
     */

    public static EmployeeIdPopUpFragment newInstance(String title,
                                                      String positiveButton,
                                                      OnFragmentInteractionListener listener) {
        EmployeeIdPopUpFragment fragment = new EmployeeIdPopUpFragment();
        fragment.title = title;
        fragment.positiveButtonText = positiveButton;
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
        View view = inflater.inflate(R.layout.fragment_emp_id_confirm, container, false);
        btPositive = view.findViewById(R.id.tv_positive);
        etEmpId = view.findViewById(R.id.et_emp_id);
        tvTitle = view.findViewById(R.id.tv_title);

        btPositive.setText(positiveButtonText);
        tvTitle.setText(title);

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPositiveInteraction(etEmpId.getText().toString());
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
        void onPositiveInteraction(String empId);
    }
}
