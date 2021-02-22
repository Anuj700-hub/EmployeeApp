package com.hungerbox.customer.offline.fragmentOffline;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderSubProduct;
import com.hungerbox.customer.model.SubProduct;
import com.hungerbox.customer.offline.activityOffline.MenuOptionListAdapterOffline;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.OrderSubProductOffline;
import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.SubProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.order.listeners.UpdateRefreshListener;
import com.hungerbox.customer.util.AppUtils;

import java.util.ArrayList;

public class OptionSelectionDialog extends DialogFragment {

    OnOptionSelectionListener mListener;
    VendorOffline vendor;
    ProductOffline product;

    TextView tvTotalPrice;
    RecyclerView rvOptionList;
    MenuOptionListAdapterOffline menuOptionListAdapter;

    ArrayList<OrderSubProductOffline> options = new ArrayList<>();
    OrderProductOffline orderProduct;
    UpdateRefreshListener updateRefreshListener = new UpdateRefreshListener() {
        @Override
        public void onUpdateRefreshListener() {
            if (!product.isFree())
                tvTotalPrice.setText(orderProduct.getTotalPriceString());
            else
                tvTotalPrice.setText("FREE");

            if (menuOptionListAdapter != null)
                menuOptionListAdapter.notifyDataSetChanged();
        }
    };

    public OptionSelectionDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vendor  Parameter 1.
     * @param product Parameter 2.
     * @return A new instance of fragment CartCancelDialog.
     */

    // TODO: Rename and change types and number of parameters
    public static OptionSelectionDialog newInstance(VendorOffline vendor, ProductOffline product,
                                                    OnOptionSelectionListener listener) {
        OptionSelectionDialog fragment = new OptionSelectionDialog();
        fragment.vendor = vendor;
        fragment.product = product;
        fragment.mListener = listener;
        OrderProductOffline orderProduct = new OrderProductOffline();
        orderProduct.createDefaultFrom(product);
        fragment.orderProduct = orderProduct;
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
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_product_option_dialog, container, false);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        rvOptionList = view.findViewById(R.id.rv_option);
        TextView btNegative = view.findViewById(R.id.bt_customize_negative);
        TextView btPosotive = view.findViewById(R.id.bt_customize_positive);

        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btPosotive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentOrderProductToCart();
            }
        });


        if (!product.isFree())
            tvTotalPrice.setText(orderProduct.getTotalPriceString());
        else
            tvTotalPrice.setText("FREE");

        menuOptionListAdapter = new MenuOptionListAdapterOffline((AppCompatActivity) getActivity(), product.getOptionResponse().getSubProducts(), options, orderProduct, updateRefreshListener, product);
        rvOptionList.setAdapter(menuOptionListAdapter);
        return view;
    }

    private void addCurrentOrderProductToCart() {

        try{
            for(SubProductOffline option : product.getOptionResponse().getSubProducts()){
                OrderSubProductOffline tempOrderSubproduct = null;
                for (OrderSubProductOffline orderSubProduct : orderProduct.getSubProducts()) {
                    if (orderSubProduct.getId() == option.getId()) {
                        tempOrderSubproduct = orderSubProduct;
                        break;
                    }
                }

                if(tempOrderSubproduct != null){

                    if(tempOrderSubproduct.getOrderOptionItems().size() > option.getMaximumSelection()){
                        AppUtils.showToast("You can add Maximum of " + option.getMaximumSelection() + " " + option.getName() + " options", true, 0);
                        return;
                    }
                    else if(tempOrderSubproduct.getOrderOptionItems().size() < option.getMinimumSelection()){
                        AppUtils.showToast("You have to add " + option.getMinimumSelection() + "  Minimum options of  " +  option.getName(), true, 0);
                        return;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        if (mListener != null)
            mListener.onFragmentInteraction(orderProduct);

        dismiss();
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
    public interface OnOptionSelectionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(OrderProductOffline orderProduct);
    }
}
