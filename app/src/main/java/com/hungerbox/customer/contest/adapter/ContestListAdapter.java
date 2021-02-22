package com.hungerbox.customer.contest.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hungerbox.customer.R;
import com.hungerbox.customer.contest.activity.ContestDetailActivity;
import com.hungerbox.customer.contest.adapter.viewmodel.ContestAdapterViewHolder;
import com.hungerbox.customer.contest.adapter.viewmodel.ContestHeaderViewHolder;
import com.hungerbox.customer.contest.adapter.viewmodel.PastContestViewHolder;
import com.hungerbox.customer.contest.model.ActiveCampaigns;
import com.hungerbox.customer.contest.model.ContestListHeader;
import com.hungerbox.customer.contest.model.PastCampaigns;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.view.HbTextView;

import java.util.ArrayList;

import static com.hungerbox.customer.contest.activity.ContestActivity.REQCODEORDER;

public class ContestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<ActiveCampaigns> activeCampaigns;
    private ArrayList<Object> campaignList;
    private int VIEW_TYPE_HEADER = 0, VIEW_TYPE_ACTIVE_CAMPAIGN = 1, VIEW_TYPE_PAST_CAMPAIGN = 2;

    public ContestListAdapter(Activity activity, ArrayList<Object> campaignList) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.campaignList = campaignList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_ACTIVE_CAMPAIGN)
            return new ContestAdapterViewHolder(inflater.inflate(R.layout.contest_list_item, viewGroup, false));
        else if (viewType == VIEW_TYPE_HEADER)
            return new ContestHeaderViewHolder(inflater.inflate(R.layout.contest_list_header, viewGroup, false));
        else
            return new PastContestViewHolder(inflater.inflate(R.layout.past_contest_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof ContestHeaderViewHolder) {
            setContestListHeader((ContestListHeader) campaignList.get(i), (ContestHeaderViewHolder) viewHolder);
        } else if (viewHolder instanceof ContestAdapterViewHolder) {
            setActiveCampaigns((ActiveCampaigns) campaignList.get(i), (ContestAdapterViewHolder) viewHolder);
        } else {
            setPastCampaigns((PastCampaigns) campaignList.get(i), (PastContestViewHolder) viewHolder);
        }
    }

    private void setContestListHeader(ContestListHeader contestListHeader, ContestHeaderViewHolder viewHolder) {
        viewHolder.tvHeader.setText(contestListHeader.getHeader());
    }


    private void setActiveCampaigns(final ActiveCampaigns activeCampaign, ContestAdapterViewHolder holder) {

        try {
            holder.tvOfferText.setText(activeCampaign.getContestTitle() + " - " + activeCampaign.getContestDescription());
            holder.tvExpiryDate.setText("Expires at "+ DateTimeUtil.getDateString12Hour(activeCampaign.getContestExpiry()*1000));
            holder.tvReward.setText(activeCampaign.getReward());

            if (!activeCampaign.getContestLogo().trim().isEmpty()) {

                ImageHandling.loadRemoteImage(activeCampaign.getContestLogo(), holder.ivLogo, R.drawable.error_image,-1, activity);

            } else {
                holder.ivLogo.setBackgroundResource(R.drawable.error_image);
            }

            holder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity,    ContestDetailActivity.class);
                    intent.putExtra(ApplicationConstants.CAMPAIGN_ID, (int) activeCampaign.getContestId());
                    intent.putExtra(ApplicationConstants.TEMPLATE_NAME, activeCampaign.getTemplateName());
                    intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Campaign_List");
                    activity.startActivityForResult(intent,REQCODEORDER);
                }
            });

//            int completedTask = activeCampaign.getContestTasks().getTaskCompleted();
            int completedTask = activeCampaign.getContestMilestones().getMilestoneCompleted();
//            int totalTask = activeCampaign.getContestTasks().getTotalTask();
            int totalTask = activeCampaign.getContestMilestones().getMilestoneTotal();
            //int remainingTask = activeCampaign.getContestTasks().getTaskRemaining();
            int remainingTask = activeCampaign.getContestMilestones().getMilestoneTotal() - activeCampaign.getContestMilestones().getMilestoneCompleted();
            int maxTimelineActiveView = Math.min(6, completedTask);
            int timeLineInactiveView = Math.min(remainingTask, 6 - maxTimelineActiveView);

            holder.llTimelineContainer.removeAllViews();
            for (int i = 0; i < maxTimelineActiveView; i++) {
                createTimeLineView(true, holder.llTimelineContainer, totalTask);
            }

            boolean questionExpired = false;
            if(totalTask != (remainingTask+completedTask)){
                timeLineInactiveView = Math.min(6-maxTimelineActiveView,totalTask-completedTask);
                questionExpired = true;
            }
            for (int i = 0; i < timeLineInactiveView; i++) {
                createTimeLineView(false, holder.llTimelineContainer, totalTask);
            }

            addTextViewEnd(holder.llTimelineContainer, remainingTask,questionExpired);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createTimeLineView(boolean success, LinearLayout llTimelineContainer, int totalTask) {

        int width = Math.round(AppUtils.convertDpToPixel(18, activity));
        int height = Math.round(AppUtils.convertDpToPixel(18, activity));

        ImageView imageView = new ImageView(activity);
        LinearLayout.LayoutParams params;
        if (success) {
            imageView.setBackgroundResource(R.drawable.status_success);
            params = new LinearLayout.LayoutParams(width, height);
        } else {
            imageView.setBackgroundResource(R.drawable.grey_circle_fill);
            params = new LinearLayout.LayoutParams(width, height);
        }
        params.setMargins(0, 5, 0, 5);
        imageView.setLayoutParams(params);
        llTimelineContainer.addView(imageView);

        addLine(success, llTimelineContainer, totalTask);
    }

    private void addLine(boolean success, LinearLayout llTimelineContainer, int totalTask) {

        try {
            int width;
            if (totalTask > 4) {
                width = Math.round(AppUtils.convertDpToPixel(16, activity));
            } else {
                width = Math.round(AppUtils.convertDpToPixel(20, activity));
            }
            int height = Math.round(AppUtils.convertDpToPixel(1, activity));
            ImageView line = new ImageView(activity);
            if (success)
                line.setBackgroundResource(R.color.green);
            else
                line.setBackgroundResource(R.color.bg);

            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(width, height);
            line.setLayoutParams(lineParams);
            llTimelineContainer.addView(line);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addTextViewEnd(LinearLayout llTimelineContainer, int remainingTask,boolean questionExpired) {

        try {
            int top = Math.round(AppUtils.convertDpToPixel(4, activity));
            int bottom = Math.round(AppUtils.convertDpToPixel(4, activity));
            int left = Math.round(AppUtils.convertDpToPixel(8, activity));
            int right = Math.round(AppUtils.convertDpToPixel(8, activity));


            HbTextView hbTextView = new HbTextView(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 0, 5);
            hbTextView.setTextSize(12);
            hbTextView.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hbTextView.setElevation(0f);
            }
            if(questionExpired){
                hbTextView.setBackgroundResource(R.drawable.accent_border_red);
                hbTextView.setTextColor(Color.parseColor("#ff2c2c"));
            }else {
                if (remainingTask > 0) {
                    hbTextView.setBackgroundResource(R.drawable.accent_border_grey);
                    hbTextView.setTextColor(Color.parseColor("#99000000"));
                } else {
                    hbTextView.setBackgroundResource(R.drawable.accent_border_green);
                    hbTextView.setTextColor(Color.parseColor("#7ed321"));
                }
            }
            hbTextView.setPadding(left, top, right, bottom);
            hbTextView.setText(remainingTask + " Milestones left");
            llTimelineContainer.addView(hbTextView);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void setPastCampaigns(final PastCampaigns pastCampaign, PastContestViewHolder pastContestViewHolder) {
        try {
            pastContestViewHolder.tvOfferText.setText(pastCampaign.getContestTitle() + " - " + pastCampaign.getContestDescription());

            if (!pastCampaign.getContestLogo().trim().isEmpty()) {
                ImageHandling.loadRemoteImage(pastCampaign.getContestLogo(), pastContestViewHolder.ivLogo, R.drawable.error_image,-1, activity);

            } else {
                pastContestViewHolder.ivLogo.setBackgroundResource(R.drawable.error_image);
            }

            if(!AppUtils.getConfig(activity).getContestConfiguration().getOfferExpired().isEmpty() ){
                pastContestViewHolder.rl_offer_expired_container.setVisibility(View.VISIBLE);
                pastContestViewHolder.tvOfferExpired.setText(AppUtils.getConfig(activity).getContestConfiguration().getOfferExpired());
            }else{
                pastContestViewHolder.rl_offer_expired_container.setVisibility(View.INVISIBLE);
            }


                pastContestViewHolder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ContestDetailActivity.class);
                    intent.putExtra(ApplicationConstants.TEMPLATE_NAME, pastCampaign.getTemplateName());
                    intent.putExtra(ApplicationConstants.CAMPAIGN_ID, (int) pastCampaign.getContestId());
                    intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Campaign_List");
                    activity.startActivityForResult(intent,REQCODEORDER);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (campaignList.get(position) instanceof ContestListHeader)
            return VIEW_TYPE_HEADER;
        else if (campaignList.get(position) instanceof ActiveCampaigns)
            return VIEW_TYPE_ACTIVE_CAMPAIGN;
        else
            return VIEW_TYPE_PAST_CAMPAIGN;
    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public void updateContestList(ArrayList<Object> campaignList) {
        this.campaignList = campaignList;
    }
}
