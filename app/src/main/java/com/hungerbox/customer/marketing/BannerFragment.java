package com.hungerbox.customer.marketing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.HomeBannerItem;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

public class BannerFragment extends Fragment {

    TextView tvOffers;
    ImageView ivOffer;
    private OnFragmentInteractionListener mListener;
    private HomeBannerItem bannerBase;

    public BannerFragment() {
    }

    public static BannerFragment newInstance(HomeBannerItem bannerBase) {
        BannerFragment fragment = new BannerFragment();
        fragment.bannerBase = bannerBase;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banner, container, false);
        tvOffers = view.findViewById(R.id.tv_offers);
        ivOffer = view.findViewById(R.id.iv_offers);
        Button btnView = view.findViewById(R.id.btn_offers);

        ImageHandling.loadRemoteImage(bannerBase.getImage(), ivOffer,R.drawable.error_image,R.drawable.error_image,getActivity());

        if (bannerBase.getText() == null || bannerBase.getText().equals("")) {
            tvOffers.setVisibility(View.GONE);
        } else {
            tvOffers.setText(Html.fromHtml(bannerBase.getText()));
        }

        if(bannerBase.getLink() != null && !bannerBase.getLink().trim().equals("")){
            RelativeLayout rlBanner = view.findViewById(R.id.rl_banner);
            rlBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        HashMap map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                        map.put(CleverTapEvent.PropertiesNames.getBanner_name(), bannerBase.getName());
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBanner_click(), map, getActivity());
                    }catch (Exception exp){
                        exp.printStackTrace();
                    }
                    handleClickListener(bannerBase.getLink());
                }
            });
        }


        if(bannerBase.getButtonText() != null && !bannerBase.getButtonText().trim().equals("") && AppUtils.getConfig(getActivity()).isBtn_on_banner()){
            btnView.setText(bannerBase.getButtonText());
        }else{
            btnView.setVisibility(View.GONE);
        }


        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    HashMap map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getBanner_name(), bannerBase.getName());
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBanner_click(), map, getActivity());
                }catch (Exception exp){
                    exp.printStackTrace();
                }
                handleClickListener(bannerBase.getLink());
            }
        });

        return view;
    }

    private void handleClickListener(String uriString) {
        EventUtil.FbEventLog(getActivity(), EventUtil.BANNER_BTN_CLICK, bannerBase.getName());

        try {
            JSONObject mixPanelValue = new JSONObject();
            mixPanelValue.put(EventUtil.MixpanelEvent.BannerClick.BANNER_NAME, bannerBase.getName());
            HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.BannerClick.EVENT_NAME, mixPanelValue);
        } catch (Exception exp) {
        }
        UrlNavigationHandler urlNavigationHandler = new UrlNavigationHandler(getActivity());
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
