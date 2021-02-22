package com.hungerbox.customer.payment.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.UpiMethod;
import com.hungerbox.customer.payment.OnUpiClickListener;
import com.hungerbox.customer.payment.adapter.viewholder.PaymentMethodUpiListViewHolder;
import com.hungerbox.customer.util.ImageHandling;

import java.util.ArrayList;

public class PaymentMethodAdapterUpiList extends RecyclerView.Adapter<PaymentMethodUpiListViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<UpiMethod> upiMethods;
    private Activity activity;
    private OnUpiClickListener onUpiClickListener;
    private int count;
    private boolean fromNavBar,fromExpressCheckout;

    public PaymentMethodAdapterUpiList(Activity activity, ArrayList<UpiMethod> upiMethods, OnUpiClickListener onUpiClickListener,int count,boolean fromNavBar,boolean fromExpressCheckout) {
        this.upiMethods = upiMethods;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.onUpiClickListener = onUpiClickListener;
        this.count = count;
        this.fromExpressCheckout = fromExpressCheckout;
        this.fromNavBar = fromNavBar;
    }

    @NonNull
    @Override
    public PaymentMethodUpiListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,  int i) {
        if (count==1){
            return new PaymentMethodUpiListViewHolder(inflater.inflate(R.layout.payment_method_single_upi_list,viewGroup,false));
        }
        return new PaymentMethodUpiListViewHolder(inflater.inflate(R.layout.payment_method_upi_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodUpiListViewHolder holder, final int i) {
        final UpiMethod upiMethod = upiMethods.get(i);
        holder.tvUpiAppName.setText(upiMethod.getAppDisplayName());

        if(!upiMethod.getAppIcon().equals("")) {

            ImageHandling.loadRemoteImage(upiMethod.getAppIcon(), holder.ivIcon,R.drawable.hb_logo_new,R.drawable.hb_logo_new,activity);

        }else{
            holder.ivIcon.setImageResource(R.drawable.hb_logo_new);
        }
        if (count==1) {
            if (fromNavBar) {
                holder.cbSelect.setVisibility(View.GONE);
            }else{
                holder.cbSelect.setVisibility(View.VISIBLE);
            }
        }

        if(upiMethod.isSelected()){
            if (count==1) {
                holder.cbSelect.setChecked(true);
            }
            holder.rlContainer.setBackground(activity.getResources().getDrawable(R.drawable.accent_border));
        }else{
            holder.rlContainer.setBackground(activity.getResources().getDrawable(R.drawable.accent_border_light_grey));

            if(count==1) {
                holder.cbSelect.setChecked(false);
            }

        }

        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onUpiClickListener!=null){
                    onUpiClickListener.onUpiMethodSelected(i,upiMethod.getPackageName(),!upiMethod.isSelected());
                }
            }
        });

        if(upiMethod.getPaymentOfferText().isEmpty()){
            holder.tvOfferText.setVisibility(View.GONE);
            if(count==1){
                holder.offerBadge.setVisibility(View.GONE);
            }
        }else{
            holder.tvOfferText.setVisibility(View.VISIBLE);
            Spanned offerText = Html.fromHtml(upiMethod.getPaymentOfferText());
            holder.tvOfferText.setText(offerText);
            if(count==1){
                holder.offerBadge.setVisibility(View.VISIBLE);
            }
        }

        if(upiMethod.getAlertMessage() != null){
            holder.tvAlertMessage.setText(upiMethod.getAlertMessage());
            holder.llAlert.setVisibility(View.VISIBLE);
            holder.tvOfferText.setVisibility(View.GONE);
            if(count==1){
                holder.offerBadge.setVisibility(View.GONE);
            }
        }else{
            holder.llAlert.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return upiMethods.size();
    }

    public void updateUpiList(ArrayList<UpiMethod> upiMethods) {
        this.upiMethods = upiMethods;
    }
}
