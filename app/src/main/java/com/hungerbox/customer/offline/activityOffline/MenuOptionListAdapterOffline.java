package com.hungerbox.customer.offline.activityOffline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.offline.modelOffline.OptionItemOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOptionItemOffline;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.OrderSubProductOffline;
import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.SubProductOffline;
import com.hungerbox.customer.order.adapter.viewholder.MenuOptionViewHolder;
import com.hungerbox.customer.order.listeners.UpdateRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MenuOptionListAdapterOffline extends RecyclerView.Adapter<MenuOptionViewHolder> {

    AppCompatActivity activity;
    List<SubProductOffline> options;
    LayoutInflater inflater;
    MainApplication mainApplication;
    ArrayList<OrderSubProductOffline> orderOptions;
    OrderProductOffline orderProduct;
    UpdateRefreshListener updateRefreshListener;
    ProductOffline product;
    boolean ignoreCheckedChange = false;

    private boolean isShown = false;

    public MenuOptionListAdapterOffline(AppCompatActivity activity, List<SubProductOffline> options,
                                 ArrayList<OrderSubProductOffline> orderSubProducts, OrderProductOffline orderProduct,
                                 UpdateRefreshListener updateRefreshListener, ProductOffline product) {
        this.activity = activity;
        this.options = options;
        this.orderOptions = orderSubProducts;
        this.orderProduct = orderProduct;
        this.updateRefreshListener = updateRefreshListener;
        this.product = product;
        mainApplication = (MainApplication) activity.getApplication();
        inflater = LayoutInflater.from(activity);
    }


    @Override
    public MenuOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuOptionViewHolder(inflater.inflate(R.layout.menu_option_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MenuOptionViewHolder holder, int position) {
        final SubProductOffline option = options.get(position);


        holder.tvName.setText(option.getName());

        OrderSubProductOffline tempOrderSubproduct = null;
        for (OrderSubProductOffline orderSubProduct : orderProduct.getSubProducts()) {
            if (orderSubProduct.getId() == option.getId()) {
                tempOrderSubproduct = orderSubProduct;
                break;
            }
        }
//        if(tempOrderSubproduct!=null) {
//            holder.tvItemsSelected.setText(tempOrderSubproduct.getItemsSelectedText());
//            holder.tvPrice.setText(tempOrderSubproduct.getSelectedItemPriceText());
//        }else{
//            holder.tvItemsSelected.setText("");
//            holder.tvPrice.setText("₹ 0.0");
//        }
        if (tempOrderSubproduct == null) {
            tempOrderSubproduct = new OrderSubProductOffline();
            tempOrderSubproduct.setId(option.getId());
            tempOrderSubproduct.setName(option.getName());
            orderProduct.getSubProducts().add(tempOrderSubproduct);
        }
        showOptionItemDialog(holder, option, tempOrderSubproduct);
        LogoutTask.updateTime();
    }

    private void showOptionItemDialog(final MenuOptionViewHolder holder, final SubProductOffline option, final OrderSubProductOffline orderSubProduct) {
        holder.llOptionItemContainer.removeAllViews();
        for (final OptionItemOffline optionItem : option.getMenuOptionsItems()) {
            LinearLayout optionItemView;
//            if(option.getMinimumSelection()>1)
//                optionItemView = (LinearLayout) inflater.inflate(R.layout.option_item_layout_check, holder.llOptionItemContainer, false);
//            else{
//                optionItemView = (LinearLayout) inflater.inflate(R.layout.option_item_layout_check, holder.llOptionItemContainer, false);
//            }
            optionItemView = (LinearLayout) inflater.inflate(R.layout.option_item_layout_check, holder.llOptionItemContainer, false);
            TextView tvName = optionItemView.findViewById(R.id.tv_name);
            TextView tvCal = optionItemView.findViewById(R.id.tv_cal);
            CheckBox cbSelect = optionItemView.findViewById(R.id.cb_select);
            cbSelect.setTag(optionItem);
            for (OrderOptionItemOffline orderOptionItem : orderSubProduct.getOrderOptionItems()) {
                if (orderOptionItem.getId() == optionItem.getId())
                    cbSelect.setChecked(true);
            }

            final OptionItemOffline finalOptionItem = optionItem;
            cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    onOptionItemChecked(holder, option, orderSubProduct, b, finalOptionItem);
                }
            });
            tvName.setText(optionItem.getName());
            tvCal.setText(optionItem.getPriceStr());
            holder.llOptionItemContainer.addView(optionItemView);
        }
    }

    private CheckBox getFirstNonSelected(LinearLayout container, OptionItemOffline optionItemToIgnore) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof LinearLayout) {
                if (((LinearLayout) view).getChildCount() == 3 && ((LinearLayout) view).getChildAt(2) instanceof CheckBox) {
                    CheckBox cbSelect = (CheckBox) ((LinearLayout) view).getChildAt(2);
                    OptionItemOffline checkBoxOptionItem = (OptionItemOffline) cbSelect.getTag();
                    if (checkBoxOptionItem.getId() == optionItemToIgnore.getId())
                        continue;
                    if (!cbSelect.isChecked())
                        return cbSelect;
                }
            }
        }
        return null;
    }


    private CheckBox getFirstSelected(LinearLayout container, OptionItemOffline optionItemToIgnore) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof LinearLayout) {
                if (((LinearLayout) view).getChildCount() == 3 && ((LinearLayout) view).getChildAt(2) instanceof CheckBox) {
                    CheckBox cbSelect = (CheckBox) ((LinearLayout) view).getChildAt(2);
                    OptionItemOffline checkBoxOptionItem = (OptionItemOffline) cbSelect.getTag();
                    if (checkBoxOptionItem.getId() == optionItemToIgnore.getId())
                        continue;
                    if (cbSelect.isChecked())
                        return cbSelect;
                }
            }
        }
        return null;
    }

    private void onOptionItemChecked(final MenuOptionViewHolder holder, final SubProductOffline subProduct,
                                     final OrderSubProductOffline orderSubProduct, boolean checked, final OptionItemOffline optionItem) {


        if (ignoreCheckedChange) {
            return;
        }

        ignoreCheckedChange = true;

        if (checked) {
            if (orderSubProduct.getOrderOptionItems().size() >= subProduct.getMaximumSelection()) {
                //REMOVE FIRST SELECTED FROM THE LIST
                CheckBox checkBox = getFirstSelected(holder.llOptionItemContainer, optionItem);
                if(checkBox!=null) {
                    final OptionItemOffline checkBoxOptionItem = (OptionItemOffline) checkBox.getTag();
                    orderSubProduct.removeOptionItem(checkBoxOptionItem);
                    checkBox.setSelected(false);
                    checkBox.setChecked(false);
                }
            }
            OrderOptionItemOffline orderOptionItem = new OrderOptionItemOffline();
            orderOptionItem.copy(optionItem);
            orderSubProduct.getOrderOptionItems().add(orderOptionItem);
        } else {
            if (orderSubProduct.getOrderOptionItems().size() <= subProduct.getMinimumSelection()) {
                //ADD FIRST SELECTED FROM THE LIST
                CheckBox checkBox = getFirstNonSelected(holder.llOptionItemContainer, optionItem);
                if(checkBox!=null) {
                    final OptionItemOffline checkBoxOptionItem = (OptionItemOffline) checkBox.getTag();
                    orderSubProduct.addOptionItem(checkBoxOptionItem);
                    checkBox.setChecked(true);
                }
            }
            orderSubProduct.removeOptionItem(optionItem);

        }

        ignoreCheckedChange = false;
    }


    @Override
    public int getItemCount() {
        if (options != null) {
//            AppUtils.HbLog("Peeyush", "products valid");
            return options.size();
        } else {
//            AppUtils.HbLog("Peeyush", "products invalid");
            return 0;
        }
    }

}
