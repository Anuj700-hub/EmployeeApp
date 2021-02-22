package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.order.adapter.viewholder.GuestQualcommViewHolder;
import com.hungerbox.customer.order.listeners.GuestRemoveClickListener;

import java.util.ArrayList;


public class GuestListQualcommAdapter extends RecyclerView.Adapter<GuestQualcommViewHolder> {

    Activity activity;
    LayoutInflater inflater;
    private ArrayList<User> userArrayList;
    GuestRemoveClickListener guestRemoveClickListener;

    public GuestListQualcommAdapter(Activity activity , ArrayList<User> userArrayList, GuestRemoveClickListener guestRemoveClickListener){
        this.userArrayList = userArrayList;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.guestRemoveClickListener = guestRemoveClickListener;

    }

    @NonNull
    @Override
    public GuestQualcommViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GuestQualcommViewHolder(inflater.inflate(R.layout.guest_order_list_item,viewGroup,false));
    }


    @Override
    public void onBindViewHolder(@NonNull GuestQualcommViewHolder holder, final int i) {
        User user = userArrayList.get(i);
        holder.tvGuestName.setText(user.getName()+" ("+user.getWalletBalanceString()+")");


        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(guestRemoveClickListener!=null){
                    guestRemoveClickListener.onRemoveClick(i);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public void updateGuestList(ArrayList<User> userArrayList){
        this.userArrayList = userArrayList;
        notifyDataSetChanged();

    }
}
