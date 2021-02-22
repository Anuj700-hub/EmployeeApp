package com.hungerbox.customer.util.view;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OnBoard;
import com.hungerbox.customer.util.ImageHandling;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HelpDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpDialog extends DialogFragment {

    ImageView ivImage;
    TextView tvText;
    OnBoard onBoard;

    public HelpDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpDialog newInstance(OnBoard onBoard) {
        HelpDialog fragment = new HelpDialog();
        fragment.onBoard = onBoard;
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
        View view = inflater.inflate(R.layout.fragment_help_dialog, container, false);
        ivImage = view.findViewById(R.id.iv_onboard);
        tvText = view.findViewById(R.id.tv_onborad);

        ImageHandling.loadLocalImage(getActivity(), ivImage, onBoard.getImageId());
        tvText.setText(onBoard.getTextId());

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
