package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SetUpLoaderDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetUpLoaderDialog extends DialogFragment {

    String message;

    public SetUpLoaderDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetUpLoaderDialog newInstance() {
        return newInstance();
    }


    public static SetUpLoaderDialog newInstance(String message) {
        SetUpLoaderDialog fragment = new SetUpLoaderDialog();
        fragment.message = message;
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
        View view = inflater.inflate(R.layout.fragment_setup_loader, container, false);
        TextView tvMessage = view.findViewById(R.id.tv_setup_dialog_message);
        tvMessage.setText(message);


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
