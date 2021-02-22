package com.hungerbox.customer.util.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WastagePopUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WastagePopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WastagePopUpFragment extends DialogFragment {


    private OnFragmentInteractionListener mListener;
    ImageView ivWaste, btBack;
    private Drawable resource;
    Activity activity;
    String link, name;
    public WastagePopUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment WastagePopUpFragment.
     */
    public static WastagePopUpFragment newInstance(Drawable resource, Activity activity, String link, String name,OnFragmentInteractionListener onFragmentInteractionListener) {
        WastagePopUpFragment fragment = new WastagePopUpFragment();
        fragment.resource = resource;
        fragment.activity = activity;
        fragment.link = link;
        fragment.name = name;
        fragment.mListener = onFragmentInteractionListener;
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
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view =  inflater.inflate(R.layout.fragment_wastage_pop_up, container, false);
        ivWaste = view.findViewById(R.id.iv_waste);
        btBack = view.findViewById(R.id.bt_back);


        ivWaste.post(new Runnable() {
            @Override
            public void run() {
                int width = ivWaste.getMeasuredWidth();
                int height = 3*width / 2;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivWaste.getLayoutParams();
                lp.height = height;
                ivWaste.setLayoutParams(lp);
                ivWaste.requestLayout();
                ivWaste.setBackground(resource);
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });
        if(link!=null && !link.trim().equals("")){
            ivWaste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        HashMap map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                        map.put(CleverTapEvent.PropertiesNames.getBanner_name(), name);
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBanner_click(), map, activity);
                    }catch (Exception exp){
                        exp.printStackTrace();
                    }
                    dismissAllowingStateLoss();
                    handleClickListener(link,name);
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void handleClickListener(String uriString,String name){
        EventUtil.FbEventLog(getActivity(), EventUtil.BANNER_BTN_CLICK,name);

        try {
            JSONObject mixPanelValue = new JSONObject();
            mixPanelValue.put(EventUtil.MixpanelEvent.BannerClick.BANNER_NAME, name);
            HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.BannerClick.EVENT_NAME, mixPanelValue);
        } catch (Exception exp) {
        }
        UrlNavigationHandler urlNavigationHandler = new UrlNavigationHandler(activity);
        Intent intent = urlNavigationHandler.getUrlNavPath(uriString);
        if (intent != null)
            startActivity(intent);
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
        void onNegativeInteraction();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mListener != null){
            mListener.onNegativeInteraction();
        }
    }
}
