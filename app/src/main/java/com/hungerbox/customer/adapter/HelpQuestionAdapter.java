package com.hungerbox.customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Help;
import com.hungerbox.customer.order.activity.OrderCancellationChatActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.ChatBoatScreen;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class HelpQuestionAdapter extends RecyclerView.Adapter<HelpQuestionAdapter.ViewHolder> {

    private final Activity activity;
    private ArrayList<Help.Question> mValues;
    private int autoCollapse;

    public HelpQuestionAdapter(ArrayList<Help.Question> question, Activity activity, int autoCollapse) {
        mValues = question;
        this.activity = activity;
        this.autoCollapse = autoCollapse;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_help_question_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (autoCollapse == 1) {
            holder.answerBox.setVisibility(View.VISIBLE);
            holder.next.setImageResource(R.drawable.ic_up_arrow);
        } else if (mValues.get(position).getAutoCollapse() == 1) {
            holder.answerBox.setVisibility(View.VISIBLE);
            holder.next.setImageResource(R.drawable.ic_up_arrow);
        } else {
            holder.answerBox.setVisibility(View.GONE);
            holder.next.setImageResource(R.drawable.ic_down_arrow);
        }

        holder.itemTextQuestion.setText(mValues.get(position).getQuestion());

        if (Build.VERSION.SDK_INT >= 24) {
            holder.itemTextQuestion.setText(Html.fromHtml(mValues.get(position).getQuestion(), FROM_HTML_MODE_LEGACY));
        } else {
            holder.itemTextQuestion.setText(Html.fromHtml(mValues.get(position).getQuestion()));
        }

        if (Build.VERSION.SDK_INT >= 24) {
            holder.itemTextAnswer.setText(Html.fromHtml(mValues.get(position).getAnswer(), FROM_HTML_MODE_LEGACY));
        } else {
            holder.itemTextAnswer.setText(Html.fromHtml(mValues.get(position).getAnswer()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleAutoCollapse(holder.next, holder.answerBox);
            }
        });

        if(isChatAvailable(mValues.get(position))){
            holder.chat_icon.setVisibility(View.VISIBLE);
        }else{
            holder.chat_icon.setVisibility(View.GONE);
        }

        if(mValues.get(position).getActionData() != null){
            holder.chat.setVisibility(View.VISIBLE);
            holder.chat.setText(mValues.get(position).getActionData().getActionLabel());
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        HashMap<String,Object> map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                        map.put(CleverTapEvent.PropertiesNames.getSource(),"Help");
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getCancel_order_click(),map,activity);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(activity, OrderCancellationChatActivity.class);
                    intent.putExtra("header",mValues.get(position).getActionData().getActionHeader());
                    intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Help_Screen");
                    activity.startActivity(intent);
                }
            });
        }else{
            holder.chat.setVisibility(View.GONE);
        }
    }

    private void handleAutoCollapse(ImageView next, final View answerBox) {
        if (answerBox.getVisibility() == View.VISIBLE) {
            answerBox.setVisibility(View.GONE);
            next.setImageResource(R.drawable.ic_down_arrow);
        } else {
            answerBox.setVisibility(View.VISIBLE);
            next.setImageResource(R.drawable.ic_up_arrow);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTextQuestion, itemTextAnswer;
        ImageView next;
        LinearLayout answerBox;
        Button chat;
        AppCompatImageView chat_icon;

        public ViewHolder(View view) {
            super(view);
            itemTextQuestion = view.findViewById(R.id.textItem);
            chat = view.findViewById(R.id.chat);
            itemTextAnswer = view.findViewById(R.id.answer);
            answerBox = view.findViewById(R.id.answerBox);
            next = view.findViewById(R.id.next);
            chat_icon = view.findViewById(R.id.chat_icon);
        }

    }

    public boolean isChatAvailable(Help.Question q){

        try{
            if(q.getActionData() != null){
                    return true;
                }
        }catch (Exception exp){
            exp.printStackTrace();
            return false;
        }
        return false;
    }
}

