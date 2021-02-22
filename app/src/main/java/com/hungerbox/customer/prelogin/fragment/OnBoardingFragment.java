package com.hungerbox.customer.prelogin.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
 * {@link OnBoardingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OnBoardingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingFragment extends Fragment {


    ImageView ivImage;
    TextView tvText,tvTitleBody;
    OnBoard onBoard;
    private int position, length;
    private OnFragmentInteractionListener mListener;
    private ImageView ivIcon;

    public OnBoardingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnBoardingFragment.
     */
    public static OnBoardingFragment newInstance(int position, int length, OnBoard onBoard) {
        OnBoardingFragment fragment = new OnBoardingFragment();
        fragment.position = position;
        fragment.length = length;
        fragment.onBoard = onBoard;
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_on_boarding, container, false);
        ivImage = view.findViewById(R.id.iv_onboard);
        tvText = view.findViewById(R.id.tv_onborad);
        ivIcon = view.findViewById(R.id.iv_onboarding_icon);
        tvTitleBody = view.findViewById(R.id.tv_title_body);

        if (onBoard != null) {
            if (onBoard.getImageId() > 0)
                ImageHandling.loadLocalImage(getActivity().getApplicationContext(), ivImage, onBoard.getImageId());
            if (onBoard.getIconId() > 0)
                ImageHandling.loadLocalImage(getActivity().getApplicationContext(), ivIcon, onBoard.getIconId());

            tvText.setText(onBoard.getText());
            tvTitleBody.setText(onBoard.getHeader());
            if (onBoard.getCompanyLogoUrl()!=null){
                if (!onBoard.getCompanyLogoUrl().equalsIgnoreCase("")) {
                    ImageHandling.loadRemoteImage(onBoard.getCompanyLogoUrl(), ivIcon, -1, -1, getActivity().getApplicationContext());
                }
            }
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
