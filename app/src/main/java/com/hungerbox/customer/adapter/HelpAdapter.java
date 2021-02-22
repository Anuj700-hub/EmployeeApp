package com.hungerbox.customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Help;
import com.hungerbox.customer.order.activity.HelpQuestionActivity;

import java.io.Serializable;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    private final Activity activity;
    private Help.Datum mValues;
    private String header;

    public HelpAdapter(Help.Datum items, Activity activity, String header) {
        mValues = items;
        this.header = header;
        this.activity = activity;
    }

    @Override
    public HelpAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_help_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (Build.VERSION.SDK_INT >= 24) {
            holder.itemText.setText(Html.fromHtml(mValues.getQuestionGroups().get(position).getName(), FROM_HTML_MODE_LEGACY));
        } else {
            holder.itemText.setText(Html.fromHtml(mValues.getQuestionGroups().get(position).getName()));
        }

        if(isChatAvailable(mValues.getQuestionGroups().get(position))){
            holder.chat_icon.setVisibility(View.VISIBLE);
        }else{
            holder.chat_icon.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, HelpQuestionActivity.class);
                intent.putExtra("Header", header);
                intent.putExtra("Subject",mValues.getQuestionGroups().get(position).getName());
                intent.putExtra("autoCollapse", (int) mValues.getQuestionGroups().get(position).getAuto_collapse());
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) mValues.getQuestionGroups().get(position).getQuestions());
                intent.putExtra("BUNDLE", args);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.getQuestionGroups().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemText;
        ImageView next;
        AppCompatImageView chat_icon;

        public ViewHolder(View view) {
            super(view);
            itemText = view.findViewById(R.id.textItem);
            next = view.findViewById(R.id.next);
            chat_icon = view.findViewById(R.id.chat_icon);
        }

    }


    public boolean isChatAvailable(Help.QuestionGroup qg){

        try{
            for(Help.Question q : qg.getQuestions()){
                if(q.getActionData() != null){
                    return true;
                }
            }
        }catch (Exception exp){
            exp.printStackTrace();
            return false;
        }
        return false;
    }
}
