package com.hungerbox.customer.contest.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hungerbox.customer.R;
import com.hungerbox.customer.contest.activity.ContestDetailActivity;
import com.hungerbox.customer.contest.activity.RewardActivity;
import com.hungerbox.customer.contest.adapter.ContestListAdapter;
import com.hungerbox.customer.contest.model.Banner;
import com.hungerbox.customer.contest.model.ContestList;
import com.hungerbox.customer.contest.model.ContestListHeader;
import com.hungerbox.customer.contest.model.ContestResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static com.hungerbox.customer.contest.activity.ContestActivity.REQCODEORDER;

public class ContestFragment extends Fragment {


    private ImageView ivBanner, ivRewards;
    private ProgressBar pbProgress;
    private boolean isRunning = false;
    private ContestListAdapter contestListAdapter;
    private RecyclerView rlActiveContest;
    private RelativeLayout rlActivityContainer, rlNoActiveContest;
    private ShimmerFrameLayout shLoading;
    private SwipeRefreshLayout refreshContest;
    private TextView tvActiveCampaign;
    private long locationId;
    private String source;


    public ContestFragment() {
    }


    public static ContestFragment newInstance(String source) {
        ContestFragment fm = new ContestFragment();
        fm.source = source;
        return fm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest, container, false);
        ivBanner = view.findViewById(R.id.iv_banner);
        pbProgress = view.findViewById(R.id.pb_progress);
        rlActiveContest = view.findViewById(R.id.rl_active_contest);
        rlActivityContainer = view.findViewById(R.id.rl_activity_container);
        shLoading = view.findViewById(R.id.shimmer_view_container);
        ivRewards = view.findViewById(R.id.iv_rewards);
        rlNoActiveContest = view.findViewById(R.id.rl_no_active_contest);
        refreshContest = view.findViewById(R.id.srl_contest);
        tvActiveCampaign = view.findViewById(R.id.tv_active_campaign);

        sendCleverTapEvent();
        refreshContest.setColorSchemeResources(R.color.colorAccent);
        refreshContest.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContestDetailFromServer();
            }
        });

        if(AppUtils.getConfig(getContext()).isContest_reward_visible()){
            ivRewards.setVisibility(View.VISIBLE);
        }else{
            ivRewards.setVisibility(View.GONE);
        }

        ivRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {

//                    try {
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put(CleverTapEvent.PropertiesNames.getSource(), "Campaign_List");
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getReward_click(), map, getActivity());
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }


                    Intent intent = new Intent(getActivity(), RewardActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });


        isRunning = true;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        rlActivityContainer.setVisibility(View.GONE);
        shLoading.setVisibility(View.VISIBLE);

        if (getActivity() != null) {
            locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        }


        getContestDetailFromServer();
    }

    private void sendCleverTapEvent() {
//        try {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put(CleverTapEvent.PropertiesNames.getSource(), source);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getCampaign_click(), map, getContext());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void getContestDetailFromServer() {

        String url = UrlConstant.CAMPAIGN_LIST + "?locationId=" + locationId;
        rlNoActiveContest.setVisibility(View.GONE);


        SimpleHttpAgent<ContestResponse> contestResponseSimpleHttpAgent = new SimpleHttpAgent<ContestResponse>(getActivity(),
                url,
                new ResponseListener<ContestResponse>() {
                    @Override
                    public void response(ContestResponse responseObject) {
                        shLoading.setVisibility(View.GONE);
                        refreshContest.setRefreshing(false);

                        if (responseObject != null && responseObject.getContestList() != null && responseObject.getContestList().size() > 0 && responseObject.getContestList().get(0) != null && isRunning) {

                            rlActivityContainer.setVisibility(View.VISIBLE);
                            rlNoActiveContest.setVisibility(View.GONE);
                            setContestDetails(responseObject);

                        } else {
                            rlActivityContainer.setVisibility(View.GONE);
                            rlNoActiveContest.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        shLoading.setVisibility(View.GONE);
                        refreshContest.setRefreshing(false);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showNoNetFragment(new RetryListener() {
                                @Override
                                public void onRetry() {
                                    getContestDetailFromServer();
                                }
                            });
                        } else {
                            if (error != null && !error.equals("")) {
                                AppUtils.showToast(error, true, 0);
                            }
                        }

                    }
                },
                ContestResponse.class
        );

        contestResponseSimpleHttpAgent.get();

    }

    private void setContestDetails(ContestResponse responseObject) {

        try {

            if (responseObject.getContestList() != null) {
                final ContestList contestList = responseObject.getContestList().get(0);

                int count = 0;
                if (contestList.getBannersData() != null && contestList.getBannersData().getBanners() != null) {
                    setBannerImageInView(contestList.getBannersData().getBanners());

                    ivBanner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getActivity() != null) {
                                if (contestList.getBannersData().getBanners().getContestCampaignId() != null) {
                                    Intent intent = new Intent(getActivity(), ContestDetailActivity.class);
                                    intent.putExtra(ApplicationConstants.TEMPLATE_NAME, contestList.getBannersData().getBanners().getTemplateName());
                                    intent.putExtra(ApplicationConstants.CAMPAIGN_ID, Integer.parseInt(contestList.getBannersData().getBanners().getContestCampaignId()));
                                    intent.putExtra(CleverTapEvent.PropertiesNames.getSource(), "Campaign_Banner");
                                    getActivity().startActivityForResult(intent,REQCODEORDER);
                                }
                            }
                        }
                    });

                } else {
                    try {
                        if (AppUtils.getConfig(getContext()).getContestConfiguration().getBannerUrl() != null &&
                                !AppUtils.getConfig(getContext()).getContestConfiguration().getBannerUrl().isEmpty()) {
                            Banner banner = new Banner();
                            banner.setImageUrl(AppUtils.getConfig(getContext()).getContestConfiguration().getBannerUrl());
                            setBannerImageInView(banner);
                        } else {
                            ivBanner.setVisibility(View.GONE);
                            count++;
                        }
                    } catch (Exception e) {
                        ivBanner.setVisibility(View.GONE);
                        count++;
                        e.printStackTrace();
                    }
                }


                ArrayList<Object> campaignListItem = new ArrayList<>();
                //campaignListItem.add(new ContestListHeader("Active Campaign"));

                if (contestList.getActiveCampaignsData() != null &&
                        contestList.getActiveCampaignsData().getActiveCampaigns() != null &&
                        contestList.getActiveCampaignsData().getActiveCampaigns().size() > 0) {
                    tvActiveCampaign.setText(AppUtils.getConfig(getContext()).getContestConfiguration().getActiveCampaign());
                    tvActiveCampaign.setVisibility(View.VISIBLE);
                    campaignListItem.addAll(contestList.getActiveCampaignsData().getActiveCampaigns());
                } else {
                    tvActiveCampaign.setVisibility(View.GONE);
                    if(count==0) {
                        AppUtils.showToast("No Active Campaigns Available", true, 2);
                    }
                    count++;
                }


                if (contestList.getPastCampaignsData() != null && contestList.getPastCampaignsData().getPastCampaigns() != null && contestList.getPastCampaignsData().getPastCampaigns().size() > 0) {
                    campaignListItem.add(new ContestListHeader(AppUtils.getConfig(getActivity()).getContestConfiguration().getPastCampaign()));
                    campaignListItem.addAll(contestList.getPastCampaignsData().getPastCampaigns());

                }else{
                    count++;
                }

                if(count==3){
                    rlNoActiveContest.setVisibility(View.VISIBLE);
                }

                if (contestListAdapter == null) {
                    contestListAdapter = new ContestListAdapter(getActivity(), campaignListItem);
                    rlActiveContest.setAdapter(contestListAdapter);
                    rlActiveContest.setLayoutManager(new LinearLayoutManager(getActivity()));
                } else {
                    contestListAdapter.updateContestList(campaignListItem);
                    contestListAdapter.notifyDataSetChanged();
                }


            } else {
                AppUtils.showToast("No Campaign Found", false, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setBannerImageInView(Banner banners) {

        try {
            final String img_url = banners.getImageUrl();

            ivBanner.post(new Runnable() {
                @Override
                public void run() {
                    if (isRunning && getActivity() != null) {
                        int width = ivBanner.getMeasuredWidth();
                        int height = width / 2;
                        ViewGroup.LayoutParams lp = ivBanner.getLayoutParams();
                        lp.height = height;
                        ivBanner.setLayoutParams(lp);
                        ivBanner.requestLayout();
                        ivBanner.setVisibility(View.VISIBLE);

                        if (img_url == null || img_url.isEmpty()) {
                            ivBanner.setBackgroundResource(R.drawable.error_image);
                        } else {
                            ImageHandling.loadRemoteImage(img_url, ivBanner, R.drawable.error_image, R.drawable.error_image, getActivity());
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showNoNetFragment(RetryListener retryListener) {
        if (getActivity() == null)
            return;
        NoNetFragment fragment = NoNetFragment.newInstance(retryListener);
        fragment.setCancelable(true);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().add(fragment, "exit").commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
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
