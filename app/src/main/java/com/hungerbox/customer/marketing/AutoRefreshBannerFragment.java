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
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.HomeBannerItem;
import com.hungerbox.customer.model.MatchScore;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AutoRefreshBannerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AutoRefreshBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoRefreshBannerFragment extends Fragment {

    TextView tvOfferTitle;
    ImageView ivOffer;
    ImageView ivM1T1, ivM1T2, ivM2T1, ivM2T2;
    boolean m1, m2;
    private OnFragmentInteractionListener mListener;
    private HomeBannerItem bannerBase;
    private TextView tvPoints, tvM1T1, tvM2T1;
    private TextView vs1, vs2;
    private RelativeLayout rlM2;
    private Timer myTimer;
    private Button btnView;
    private TextView tvScore;

    public AutoRefreshBannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AutoRefreshBannerFragment newInstance(HomeBannerItem bannerBase) {
        AutoRefreshBannerFragment fragment = new AutoRefreshBannerFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auto_refresh_banner, container, false);
        tvOfferTitle = view.findViewById(R.id.tv_offer_title);
        tvScore = view.findViewById(R.id.tv_score);
        tvPoints = view.findViewById(R.id.tv_points);
        tvM1T1 = view.findViewById(R.id.tv_m1_score);
        tvM2T1 = view.findViewById(R.id.tv_m2_score);
        ivM1T1 = view.findViewById(R.id.iv_m1_t1);
        ivM1T2 = view.findViewById(R.id.iv_m1_t2);
        ivM2T1 = view.findViewById(R.id.iv_m2_t1);
        ivM2T2 = view.findViewById(R.id.iv_m2_t2);
        vs1 = view.findViewById(R.id.vs1);
        vs2 = view.findViewById(R.id.vs2);
        rlM2 = view.findViewById(R.id.rl_m2);

        ivOffer = view.findViewById(R.id.iv_offers);
        btnView = view.findViewById(R.id.btn_offers);

        updateScoreCard();

        if (bannerBase.getButtonText().length() > 0) {
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
        } else
            btnView.setVisibility(View.GONE);
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

    @Override
    public void onPause() {
        super.onPause();
        if (myTimer != null)
            myTimer.cancel();
        AppUtils.HbLog("timer", "timer_cancel");
    }

    @Override
    public void onResume() {
        super.onResume();
        OfferTimer myTask = new OfferTimer();
        myTimer = new Timer();
        myTimer.schedule(myTask, 10000, 30000);
        AppUtils.HbLog("timer", "timer_start");

    }

    private void updateScoreCard() {

        ImageHandling.loadRemoteImage(bannerBase.getImage(), ivOffer,R.drawable.error_image,R.drawable.loader_image,getActivity());

        tvOfferTitle.setText(Html.fromHtml(bannerBase.getName()));
        btnView.setText(bannerBase.getButtonText());
        tvPoints.setText("You have earned \n" + ((int) bannerBase.getPoints().getPoints()) + " Hunger Points");
        if (bannerBase.getMatches() != null && bannerBase.getMatches().size() > 0) {

            ImageHandling.loadRemoteImage(bannerBase.getMatches().get(0).getTeam1Logo(), ivM1T1,R.drawable.health_4,-1,getActivity());

            ImageHandling.loadRemoteImage(bannerBase.getMatches().get(0).getTeam2Logo(), ivM1T2,R.drawable.health_4,-1,getActivity());

            if (bannerBase.getMatches().get(0).getLiveScore() == null) {
                tvM1T1.setText("Match Starts At -" + bannerBase.getMatches().get(0).getStartTime()
                        + " " + bannerBase.getMatches().get(0).getMatchDate());
                m1 = true;
            } else {
                try {
                    tvM1T1.setText(Html.fromHtml(bannerBase.getMatches().get(0).getLiveScore()));
                } catch (Exception e) {
                    tvM1T1.setText("--/-");
                }
                m1 = true;
            }
            if (bannerBase.getMatches().size() > 1) {
                ImageHandling.loadRemoteImage(bannerBase.getMatches().get(0).getTeam2Logo(), ivM1T2,R.drawable.health_4,-1,getActivity());

                ImageHandling.loadRemoteImage(bannerBase.getMatches().get(0).getTeam2Logo(), ivM1T2,R.drawable.health_4,-1,getActivity());

                if (bannerBase.getMatches().get(1).getLiveScore() == null) {
                    tvM2T1.setText("Match Starts At -" + bannerBase.getMatches().get(1).getStartTime()
                            + " " + bannerBase.getMatches().get(1).getMatchDate());
                    m2 = true;
                } else {
                    try {
                        tvM2T1.setText(Html.fromHtml(bannerBase.getMatches().get(1).getLiveScore()));
                    } catch (Exception e) {
                        tvM2T1.setText("--/-");
                    }

                    m2 = true;
                }

            } else {
                rlM2.setVisibility(View.GONE);
            }
        } else {
            tvScore.setText("No Matches Available");
        }
    }

    public void refreshScore(long matchId, final int matchNo) {
        String url = UrlConstant.GET_MATCH_SCORE + matchId;
        SimpleHttpAgent<MatchScore> categoryHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
                url,
                new ResponseListener<MatchScore>() {
                    @Override
                    public void response(MatchScore responseObject) {

                        if (matchNo == 0) {
                            if (responseObject.getMatch().getLiveScore() == null) {
                                tvM1T1.setText("Match Starts At -" + bannerBase.getMatches().get(0).getStartTime()
                                        + " " + bannerBase.getMatches().get(0).getMatchDate());
                            } else {
                                try {
                                    tvM1T1.setText(Html.fromHtml(responseObject.getMatch().getLiveScore()));
                                } catch (Exception e) {
                                    tvM1T1.setText("--/-");
                                }
                            }
                        } else {
                            if (responseObject.getMatch().getLiveScore() == null) {
                                tvM2T1.setText("Match Starts At -" + bannerBase.getMatches().get(1).getStartTime()
                                        + " " + bannerBase.getMatches().get(1).getMatchDate());
                            } else {
                                try {
                                    tvM2T1.setText(Html.fromHtml(responseObject.getMatch().getLiveScore()));
                                } catch (Exception e) {
                                    tvM2T1.setText("--/-");
                                }
                            }
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                MatchScore.class
        );

        categoryHttpAgent.get();
    }

    private void handleClickListener(String uriString) {

        EventUtil.FbEventLog(getActivity(), EventUtil.OFFER_BANNER_BTN_CLICK, bannerBase.getName());
        try {
            JSONObject mixPanelValue = new JSONObject();
            mixPanelValue.put(EventUtil.MixpanelEvent.BannerClick.BANNER_NAME, bannerBase.getName());
            HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.BannerClick.EVENT_NAME, mixPanelValue);
        } catch (Exception exp) {
        }
        if (bannerBase.isAllowUser()) {
            UrlNavigationHandler urlNavigationHandler = new UrlNavigationHandler(getActivity());
            Intent intent = urlNavigationHandler.getUrlNavPath(uriString);
            if (intent != null) {
                intent.putExtra("Title", bannerBase.getName());
                startActivity(intent);
            }
        } else {
            GenericPopUpFragment errorPopUp = GenericPopUpFragment.newInstance(bannerBase.getErrorText(), "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {
                    EventUtil.FbEventLog(getActivity(), EventUtil.OFFER_BANNER_INELIGIBILITY_DIALOG, bannerBase.getName());

                    try {
                        JSONObject mixPanelValue = new JSONObject();
                        mixPanelValue.put(EventUtil.MixpanelEvent.BannerIneligible.BANNER_NAME, bannerBase.getName());
                        HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.BannerIneligible.EVENT_NAME, mixPanelValue);
                    } catch (Exception exp) {

                    }
                }

                @Override
                public void onNegativeInteraction() {

                }
            });
            errorPopUp.setCancelable(true);
            errorPopUp.show(getChildFragmentManager(), "error");
        }
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

    class OfferTimer extends TimerTask {

        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (m1)
                            refreshScore(bannerBase.getMatches().get(0).getId(), 0);
                        if (m2)
                            refreshScore(bannerBase.getMatches().get(1).getId(), 1);
                        AppUtils.HbLog("refresh", "refreshing");
                    }
                });
            }
        }
    }
}
