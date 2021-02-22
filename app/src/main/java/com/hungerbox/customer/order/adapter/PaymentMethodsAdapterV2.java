package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bigbasket.bbinstant.App;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ListWallet;
import com.hungerbox.customer.model.MoreOptionHeader;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.model.PaymentMethodHeader;
import com.hungerbox.customer.model.UpiMethod;
import com.hungerbox.customer.model.WalletBreakupItem;
import com.hungerbox.customer.navmenu.fragment.WalletBreakupFragment;
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.order.activity.PaymentFragment;
import com.hungerbox.customer.order.adapter.viewholder.PaymentHeaderViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.PaymentMethodViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.ViewMoreViewHolder;
import com.hungerbox.customer.order.listeners.OnPaymentSelectLisntener;
import com.hungerbox.customer.order.listeners.PaymentMethodsInterface;
import com.hungerbox.customer.payment.OnUpiClickListener;
import com.hungerbox.customer.payment.adapter.PaymentMethodAdapterUpiList;
import com.hungerbox.customer.payment.adapter.viewholder.PaymentMethodUpiViewHolder;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.GridSpacingItemDecoration;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by manas on 17/11/16.
 */

public class PaymentMethodsAdapterV2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int VIEW_TYPE_HEADER = 0;
    static final int VIEW_TYPE_NORMAL_PM = 1,VIEW_TYPE_MORE_HEADER=2,VIEW_TYPE_UPI=3;
    Activity activity;
    LayoutInflater inflater;
    ArrayList<PaymentMethod> paymentMethodsArrayList;
    ArrayList<Object> paymentMethodForDisplay;
    PaymentMethodsInterface paymentMethodsInterface;
    double externalWalletCharge = 0;
    private boolean fromNavBar = false,fromExpressCheckout=false;
    public PaymentMethodAdapterUpiList paymentMethodAdapterUpiList;
    private ArrayList<UpiMethod> upiMethods = new ArrayList<>();
    private PaymentMethod paymentMethod;
    private boolean isExpanded = false;

    public PaymentMethodsAdapterV2(Activity activity, ArrayList<PaymentMethod> paymentMethodsArrayList,
                                   ArrayList<Object> paymentMethodForDisplay,
                                   boolean fromNavBar, boolean fromExpressCheckout,
                                   double externalWalletCharge, PaymentMethodsInterface paymentMethodsInterface, Boolean isExpanded) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.paymentMethodsArrayList = paymentMethodsArrayList;
        this.paymentMethodForDisplay = paymentMethodForDisplay;
        this.paymentMethodsInterface = paymentMethodsInterface;
        this.fromNavBar = fromNavBar;
        this.externalWalletCharge = externalWalletCharge;
        this.fromExpressCheckout = fromExpressCheckout;
        this.isExpanded = isExpanded;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_HEADER) {
            return new PaymentHeaderViewHolder(inflater.inflate(R.layout.payment_header_item, parent, false));
        }
        else if(viewType == VIEW_TYPE_MORE_HEADER){
            return new ViewMoreViewHolder(inflater.inflate(R.layout.textview_more_option, parent, false));
        }
        else if(viewType == VIEW_TYPE_UPI){
            return new PaymentMethodUpiViewHolder(inflater.inflate(R.layout.payment_type_upi,parent,false));
        }
        else {
            return new PaymentMethodViewHolder(inflater.inflate(R.layout.payment_method_v2_list_item, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (paymentMethodForDisplay.get(position) instanceof PaymentMethodHeader) {
            return VIEW_TYPE_HEADER;
        }
        else if(paymentMethodForDisplay.get(position) instanceof MoreOptionHeader){
            return VIEW_TYPE_MORE_HEADER;
        }
        else {
            if(checkIfPaytmUpi(((PaymentMethod)paymentMethodForDisplay.get(position)))){
                return VIEW_TYPE_UPI;
            }else{
                return VIEW_TYPE_NORMAL_PM;
            }
        }
    }


    void onPaymentMethodDisplay(final PaymentMethod paymentData, final PaymentMethodViewHolder holder) {
        if (!isExpanded && paymentData.isPaymentTypeOthers()){
            holder.itemView.setVisibility(View.GONE);
            RecyclerView.LayoutParams relativeParams = (RecyclerView.LayoutParams) holder.rlPaymentMethodItem.getLayoutParams();
            relativeParams.height = 0;
            relativeParams.setMargins(0,0,0,0);
            holder.rlPaymentMethodItem.setVisibility(View.GONE);
        }
        else{
            RecyclerView.LayoutParams relativeParams = (RecyclerView.LayoutParams) holder.rlPaymentMethodItem.getLayoutParams();
            relativeParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            relativeParams.setMargins(Math.round(AppUtils.convertDpToPixel(16, activity)),Math.round(AppUtils.convertDpToPixel(6, activity)),Math.round(AppUtils.convertDpToPixel(16, activity)),Math.round(AppUtils.convertDpToPixel(16, activity)));
            //holder.rlPaymentMethodItem.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            holder.itemView.setVisibility(View.VISIBLE);
            holder.rlPaymentMethodItem.setVisibility(View.VISIBLE);
        }

        if (paymentData.getPaymentSource() != null &&
                paymentData.getPaymentSource().getPaymentData().getShowBalance() == 1 && paymentData.isUserLinked()) {
            if(fromNavBar){
                holder.tvPaymentMethod.setText(paymentData.getDisplayName() + "\n(₹" + paymentData.getAmount() + ")");
            }else {
                holder.tvPaymentMethod.setText(paymentData.getDisplayName());
                holder.tvOrderAmount.setVisibility(View.VISIBLE);
                holder.tvOrderAmount.setText("(₹" + paymentData.getAmount() + ")");
                holder.tvRechargeLink.setVisibility(View.INVISIBLE);
            }

        } else if (paymentData.getWalletSource().equalsIgnoreCase("internal")) {
            if (paymentData.isCompulsory()) {
                holder.tvPaymentMethod.setText(paymentData.getDisplayName() + paymentData.getInternalWalletsBalance());
                holder.tvPaymentMethod.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
            } else
                holder.tvPaymentMethod.setText(paymentData.getDisplayName() +
                        String.format(" (₹ %.2f",paymentData.getDisplayAmount(true)) + ")");
        } else {
            holder.tvPaymentMethod.setText(paymentData.getDisplayName());
            holder.tvOrderAmount.setVisibility(View.GONE);
        }

        if (paymentData.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)) {
            if (paymentData.getPaymentDetails() != null) {
                holder.tvPaymentMethod.setText(paymentData.getPaymentDetails().getCardNumber());
            }
        }

        if (paymentData.isSelected()) {
            holder.cbPayment.setChecked(true);
            holder.rlPaymentMethodItem.setBackground(activity.getResources().getDrawable(R.drawable.accent_border));
        } else {
            holder.cbPayment.setChecked(false);
            holder.rlPaymentMethodItem.setBackground(activity.getResources().getDrawable(R.drawable.white_drawable));
        }

        if (paymentData.getCard_company() == null && paymentData.getCard_type() == null) {
            holder.tvCardDetailsBox.setVisibility(View.GONE);
        }else{
            holder.tvCardDetailsBox.setVisibility(View.VISIBLE);
        }

        if (paymentData.getCard_company() != null && !paymentData.getCard_company().equals("")) {
            holder.tvCardDetailsCompany.setText(paymentData.getCard_company());
        }

        if (paymentData.getCard_type() != null && !paymentData.getCard_type().equals("")) {
            holder.tvCardDetailsType.setText(paymentData.getCard_type());
        }

        if (paymentData.getCard_company() != null && !paymentData.getCard_company().equals("") && paymentData.getCard_type() != null && !paymentData.getCard_type().equals("")) {
            holder.tvCardDetailsSeprator.setVisibility(View.VISIBLE);
        }else{
            holder.tvCardDetailsSeprator.setVisibility(View.INVISIBLE);
        }

        if (paymentData.getWalletSource().equalsIgnoreCase("internal") && paymentData.employeeCanRecharge()) {
            holder.tvRechargeLink.setText("Recharge");
            holder.tvRechargeLink.setVisibility(View.VISIBLE);
            holder.tvOrderAmount.setVisibility(View.GONE);
        } else {
            if (paymentData.isLinkingAllowed() && !paymentData.isUserLinked()) {
                holder.tvRechargeLink.setText("Link Account");
                holder.tvRechargeLink.setVisibility(View.VISIBLE);
            } else if (paymentData.isDelinkingAllowed() && paymentData.isUserLinked()) {
                if (fromNavBar) {
                    holder.tvRechargeLink.setText("Delink Account");
                    holder.tvRechargeLink.setVisibility(View.VISIBLE);
                } else {
                    holder.tvRechargeLink.setVisibility(View.GONE);
                }
            } else if (paymentData.getPaymentMethodType()
                    .equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)) {
                if (paymentData.getPaymentDetails() != null) {
                    if (fromNavBar) {
                        holder.tvPaymentMethod.setText(paymentData.getPaymentDetails().getCardNumber());
                        holder.tvRechargeLink.setText("Delete");
                        holder.tvRechargeLink.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvRechargeLink.setVisibility(View.GONE);
                    }
                } else {
                    holder.tvRechargeLink.setVisibility(View.GONE);
                }
            } else {
                holder.tvRechargeLink.setVisibility(View.GONE);
            }

        }


        if (paymentData.getWalletSource().equalsIgnoreCase("internal")) {

            ImageHandling.loadLocalImage(activity, holder.ivPaymentLogo, R.drawable.hungerbox_logo_new);

        } else {
            ImageHandling.loadRemoteImage(paymentData.getLogo(), holder.ivPaymentLogo, R.drawable.hungerbox_logo_new,R.drawable.hungerbox_logo_new,activity);

        }

        if (paymentData.getPaymentSource() != null && paymentData.isLinkingAllowed()) {
            if (!paymentData.getPaymentSource().getPaymentData().isUserLinked()) {
                holder.tvPaymentMethod.setText(paymentData.getDisplayName());
            }else{
                if (paymentData.getPaymentSource().getPaymentData().getShowBalance() == 1) {
                    if(fromNavBar){
                        holder.tvPaymentMethod.setText(paymentData.getDisplayName() + "\n" + paymentData.getAmount());
                    }else {
                        holder.tvPaymentMethod.setText(paymentData.getDisplayName());
                        holder.tvOrderAmount.setText("(₹ " + paymentData.getAmount() + ")");
                        holder.tvOrderAmount.setVisibility(View.VISIBLE);
                        holder.tvRechargeLink.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.tvPaymentMethod.setText(paymentData.getDisplayName());
                    holder.tvOrderAmount.setVisibility(View.GONE);
                }
            }
        }

        if (paymentData.isUserLinked() && paymentData.getPaymentDetails() != null) {
            if(fromNavBar){
                holder.tvPaymentMethod.setText(paymentData.getDisplayName() + "\n(₹ " + paymentData.getPaymentDetails().getCurretBalance() + ")");
            }else {
                holder.tvPaymentMethod.setText(paymentData.getDisplayName());
                holder.tvOrderAmount.setText("(₹ " + paymentData.getPaymentDetails().getCurretBalance() + ")");
                holder.tvOrderAmount.setVisibility(View.VISIBLE);
                holder.tvRechargeLink.setVisibility(View.INVISIBLE);
            }
        }

        if (paymentData.getPaymentOfferText().isEmpty()) {
            holder.tvOfferText.setVisibility(View.GONE);
            holder.ivOffersBadge.setVisibility(View.INVISIBLE);
        } else {
            holder.tvOfferText.setVisibility(View.VISIBLE);
            Spanned offerText = Html.fromHtml(paymentData.getPaymentOfferText());
            holder.tvOfferText.setText(offerText);
            holder.ivOffersBadge.setVisibility(View.VISIBLE);

            if(paymentData.getDisplayName().toLowerCase().contains("lazypay")){
                if(!paymentData.getPaymentOfferText().contains("<br>")) {
                    holder.ivOffersBadge.setVisibility(View.INVISIBLE);
                }
            }

        }

        if(paymentData.getAlertMessage() != null){
            holder.tvAlertMessage.setText(paymentData.getAlertMessage());
            holder.llAlert.setVisibility(View.VISIBLE);
            holder.tvOfferText.setVisibility(View.GONE);
            holder.ivOffersBadge.setVisibility(View.GONE);

        }else{
            holder.llAlert.setVisibility(View.GONE);
        }

        holder.tvRechargeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tvRechargeLink.getText().toString().equalsIgnoreCase("Delink Account")) {
                    paymentMethodsInterface.delinkPayment(paymentData, "delink");
                } else if (holder.tvRechargeLink.getText().toString().equalsIgnoreCase("Delete")) {
                    paymentMethodsInterface.delinkPayment(paymentData, "delete");
                } else if (holder.tvRechargeLink.getText().toString().equalsIgnoreCase("Link Account")) {
                    paymentMethodsInterface.linkPayment(paymentData);
                } else {
                    paymentMethodsInterface.rechargeWallet(paymentData);
                }
            }
        });

        holder.rlPaymentMethodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (fromNavBar) {
                    if(paymentData.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)
                        && paymentData.getPaymentDetails() == null)
                    {
                        if (activity instanceof OnPaymentSelectLisntener) {
                            ((OnPaymentSelectLisntener) activity).handlePaymentMethodSelected(paymentData, !paymentData.isSelected());
                        }
                    }
                    return;
                }

                if (activity instanceof OnPaymentSelectLisntener) {
                    ((OnPaymentSelectLisntener) activity).handlePaymentMethodSelected(paymentData, !paymentData.isSelected());
                }
            }
        });

        if (fromNavBar) {
            holder.cbPayment.setVisibility(View.GONE);
        }else{
            holder.cbPayment.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder genHolder, final int position) {


        if (genHolder instanceof PaymentMethodViewHolder) {
            onPaymentMethodDisplay((PaymentMethod) paymentMethodForDisplay.get(position), (PaymentMethodViewHolder) genHolder);
        } else if (genHolder instanceof PaymentHeaderViewHolder) {
            onHeaderBind((PaymentMethodHeader) paymentMethodForDisplay.get(position), (PaymentHeaderViewHolder) genHolder);
        }else if(genHolder instanceof ViewMoreViewHolder){
            onMoreOption((MoreOptionHeader)paymentMethodForDisplay.get(position),(ViewMoreViewHolder)genHolder);
        }else if(genHolder instanceof PaymentMethodUpiViewHolder){
            onPaymentMethodUpi((PaymentMethod)paymentMethodForDisplay.get(position),(PaymentMethodUpiViewHolder)genHolder);
        }

    }

    private void onPaymentMethodUpi(PaymentMethod paymentMethodUpi , PaymentMethodUpiViewHolder holder){
        paymentMethod = paymentMethodUpi;
        holder.tvMethodName.setText(paymentMethod.getDisplayName());
        upiMethods.clear();
        upiMethods.addAll(paymentMethod.getPaymentUpiMethods().getUpiMethods());

        RecyclerView.LayoutParams relativeParams = (RecyclerView.LayoutParams) holder.rlContainer.getLayoutParams();
        relativeParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        relativeParams.setMargins(Math.round(AppUtils.convertDpToPixel(16, activity)),Math.round(AppUtils.convertDpToPixel(6, activity)),Math.round(AppUtils.convertDpToPixel(16, activity)),Math.round(AppUtils.convertDpToPixel(16, activity)));
        //Making Nested RecyclerView with GridLayout Manager
        if(paymentMethod.isSelected()){
            if(!isUpiMethodSelected()){
                if(upiMethods.size()>0){
                    upiMethods.get(0).setSelected(true);
                }
            }
        }else{
            if(fromExpressCheckout){
                holder.rlContainer.setBackground(activity.getResources().getDrawable(R.drawable.white_drawable_without_border));
            }
            clearUpiSelected();
        }

        if(upiMethods.size()>0) {
            if(paymentMethodAdapterUpiList==null) {
                paymentMethodAdapterUpiList = new PaymentMethodAdapterUpiList(activity, upiMethods, new OnUpiClickListener() {
                    @Override
                    public void onUpiMethodSelected(int position,String pkgName,boolean isSelected) {
                        if (!fromNavBar) {
                            if (activity instanceof OnPaymentSelectLisntener) {


                                for(UpiMethod upiMethod : upiMethods){
                                    if(upiMethod.getPackageName().equalsIgnoreCase(pkgName)){
                                        upiMethod.setSelected(isSelected);
                                    }else{
                                        upiMethod.setSelected(false);
                                    }
                                }

                                ((OnPaymentSelectLisntener) activity).handlePaymentMethodSelected(paymentMethod, isUpiMethodSelected());


                                paymentMethodAdapterUpiList.notifyDataSetChanged();
                            }
                        }
                    }
                },upiMethods.size(),fromNavBar,fromExpressCheckout);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 3) {
                    @Override
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                if (upiMethods.size()==1){
                    holder.rvPaymentUpi.setLayoutManager(new LinearLayoutManager(activity));
                }
                else {
                    holder.rvPaymentUpi.addItemDecoration(new GridSpacingItemDecoration(3,40,false));
                    holder.rvPaymentUpi.setLayoutManager(gridLayoutManager);
                }
                holder.rvPaymentUpi.setAdapter(paymentMethodAdapterUpiList);
            }else{
                paymentMethodAdapterUpiList.notifyDataSetChanged();
            }

        }else{
        }

    }

    private void clearUpiSelected(){
        for(UpiMethod upiMethod : upiMethods){
            upiMethod.setSelected(false);
        }
        if(paymentMethodAdapterUpiList!=null)
            paymentMethodAdapterUpiList.notifyDataSetChanged();
    }

    private void onMoreOption(MoreOptionHeader header, ViewMoreViewHolder genHolder) {
        genHolder.tvMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (activity!=null && activity instanceof BookmarkPaymentActivity){
                        if(((PaymentFragment)((BookmarkPaymentActivity)activity).fragment).btPay.isEnabled()){
                            paymentMethodsInterface.onMorePaymentOption();
                        }else{
                            //AppUtils.showToast("Payment button disabled for now",false,2);
                        }
                    } else{
                        //AppUtils.showToast("Payment button disabled for now",false,2);
                    }
                    HashMap<String , Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getLocation_id(), SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID));
                    map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getView_more_payment_options(), map, activity);
                }catch (Exception e){
                    e.printStackTrace();
                    paymentMethodsInterface.onMorePaymentOption();
                }


            }
        });
    }

    private void onHeaderBind(PaymentMethodHeader paymentMethodHeader, final PaymentHeaderViewHolder holder) {
        if (isExpanded){
            holder.ivDropDown.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_arrow_drop_up));
        } else{
            holder.ivDropDown.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_arrow_drop_down));
        }
        if(fromExpressCheckout){
            holder.tvHeader.setVisibility(View.GONE);
            isExpanded = true;
        }else {
            holder.tvHeader.setText(paymentMethodHeader.getHeader());
            if (paymentMethodHeader.getHeader().equalsIgnoreCase("others")
                    || paymentMethodHeader.getHeader().equalsIgnoreCase("other")){
                holder.ivDropDown.setVisibility(View.VISIBLE);
                holder.tvHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpanded){
                            isExpanded = false;
                            if (activity != null) {
                                holder.ivDropDown.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_arrow_drop_down));
                            }
                            notifyDataSetChanged();
                        } else{
                            isExpanded = true;
                            if (activity != null) {
                                holder.ivDropDown.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_arrow_drop_up));
                                try {
                                    PaymentFragment fragment = (PaymentFragment) ((PaymentActivity) activity).fragment;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fragment.rvPaymentMethods.scrollToPosition(paymentMethodForDisplay.size()-1);
                                        }
                                    }, 400);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
                holder.ivDropDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.tvHeader.performClick();
                    }
                });

            }
            else{
                holder.ivDropDown.setVisibility(View.GONE);
            }
        }
    }

    private boolean isUpiMethodSelected(){
        if(upiMethods!=null && upiMethods.size()>0){
            for(UpiMethod upiMethod : upiMethods) {
                if (upiMethod.isSelected()){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIfPaytmUpi(PaymentMethod paymentMethod){
        return paymentMethod.getPaymentSource() != null && paymentMethod.getPaymentSource().getPaymentData() != null &&
                paymentMethod.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.PAYMENT_UPI_METHOD);
    }

    @Override
    public int getItemCount() {
        return paymentMethodForDisplay.size();
    }


    public void updatePaymentDetails(ArrayList<PaymentMethod> paymentMethods, ArrayList<Object> paymentMethodsForDisplay) {
        this.paymentMethodsArrayList = paymentMethods;
        this.paymentMethodForDisplay = paymentMethodsForDisplay;
    }
}
