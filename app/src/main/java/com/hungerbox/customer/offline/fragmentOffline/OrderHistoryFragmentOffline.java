package com.hungerbox.customer.offline.fragmentOffline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.navmenu.PaginationHandler;
import com.hungerbox.customer.navmenu.fragment.HistoryFragment;
import com.hungerbox.customer.navmenu.listeners.UpdateListener;
import com.hungerbox.customer.offline.adapterOffline.OrderHistoryViewAdapterOffline;
import com.hungerbox.customer.order.activity.OrderDetailNewActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;

import java.util.ArrayList;
import java.util.List;


public class OrderHistoryFragmentOffline extends Fragment implements PaginationHandler, UpdateListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    int currentPage = 1;
    boolean pageOver = false;
    private int mColumnCount = 1;
    private HistoryFragment.OnListFragmentInteractionListener mListener;
    private OrderHistoryViewAdapterOffline historyAdapter;
    private View view;
    private RecyclerView recyclerView;
    private TextView tvNoBookings;
    private String historyType = "";
    private ProgressBar pbHistory;
    private ArrayList<Order> orders = new ArrayList<>();
    private String value = "";

    public OrderHistoryFragmentOffline() {
    }

    public static OrderHistoryFragmentOffline newInstance(int columnCount, String historyType, String value) {
        OrderHistoryFragmentOffline fragment = new OrderHistoryFragmentOffline();
        fragment.historyType = historyType;
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        fragment.value = value;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_list_food_offline, container, false);
        recyclerView = view.findViewById(R.id.list);
        tvNoBookings = view.findViewById(R.id.tv_no_bookings);

        tvNoBookings.setText("No Orders Yet");
        pbHistory = view.findViewById(R.id.pb_history);
        tvNoBookings.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        currentPage = 1;
        manageOrders(currentPage, false);
    }

    private void manageOrders(final int page, final boolean showDialog) {

        DbHandler db = DbHandler.getDbHandler(getActivity());
        List<Order> ordersList = db.getAllOrder();

        for (Order order : ordersList) {
            if ((System.currentTimeMillis() - order.getCreatedAt() * 1000) > ApplicationConstants.THIRTY_MIN_MILLIS_OFFLINE) {
                DbHandler.getDbHandler(getContext()).deleteOrder(order);
            }
        }

        setuplastOrderList(db.getAllOrder(), page);
    }

    private void setuplastOrderList(List<Order> ordersList, int page) {
        if (page == 1) {
            this.orders.clear();
        }
        if (this.orders != null) {
            this.orders.addAll(ordersList);
        } else {
            this.orders = new ArrayList<>();
            this.orders.addAll(ordersList);
        }

        if(value != null && value.equals("last_order") && this.orders.size() >0){
            Intent intent = new Intent(getActivity(), OrderDetailNewActivity.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Shortcut");
            intent.putExtra(ApplicationConstants.BOOKING_ID, this.orders.get(0).getId());
            getActivity().startActivity(intent);
            value = null;
        }

        if (currentPage == page - 1) {
            currentPage = page;
        }
        pageOver = ordersList.size() < 10;

        if (recyclerView.getAdapter() == null) {
            historyAdapter = new OrderHistoryViewAdapterOffline(getActivity(),
                    this.orders, this, mListener, historyType);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(historyAdapter);
        } else {
            if (recyclerView.getAdapter() instanceof OrderHistoryViewAdapterOffline) {
                historyAdapter.udpateOrders(this.orders);
                historyAdapter.notifyDataSetChanged();
            }
        }
        if (orders.size() == 0)
            tvNoBookings.setVisibility(View.VISIBLE);
        else
            tvNoBookings.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HistoryFragment.OnListFragmentInteractionListener) {
            mListener = (HistoryFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void update() {
        currentPage = 1;
        manageOrders(currentPage, false);
    }

    @Override
    public void onLastItemReached() {
        AppUtils.showToast("No more orders to show.", true, 2);

    }

    @Override
    public void updateData() {
        update();
    }
}
