package com.hungerbox.customer.offline.fragmentOffline;

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
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.offline.adapterOffline.OrderHistoryViewAdapterOrderOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragmentOrderOffline extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    int currentPage = 1;
    boolean pageOver = false;
    private int mColumnCount = 1;
    private OrderHistoryViewAdapterOrderOffline historyAdapter;
    private View view;
    private RecyclerView recyclerView;
    private TextView tvNoBookings;
    private String historyType = "";
    private ProgressBar pbHistory;
    private ArrayList<OrderOffline> orders = new ArrayList<>();
    private String value = "";

    public OrderHistoryFragmentOrderOffline() {
    }

    public static OrderHistoryFragmentOrderOffline newInstance(String historyType) {
        OrderHistoryFragmentOrderOffline fragment = new OrderHistoryFragmentOrderOffline();
        fragment.historyType = historyType;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_list_food_order_offline, container, false);
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
        manageOrderList();
    }


    private void manageOrderList() {

        DbHandler db = DbHandler.getDbHandler(getActivity());
        List<OrderOffline> ordersList = db.getAllOrderOffline();

        for (OrderOffline orderOffline : ordersList) {
            if ((System.currentTimeMillis() - orderOffline.getCreatedAt() * 1000) > ApplicationConstants.THIRTY_MIN_MILLIS_OFFLINE) {
                DbHandler.getDbHandler(getContext()).deleteOrderOffline(orderOffline);
            }
        }

        setuplastOrderList(db.getAllOrderOffline());

    }

    private void setuplastOrderList(List<OrderOffline> ordersList) {


        if (ordersList != null && ordersList.size() > 0) {
            tvNoBookings.setVisibility(View.GONE);

            historyAdapter = new OrderHistoryViewAdapterOrderOffline(getActivity(),
                    ordersList, historyType);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(historyAdapter);


        } else {
            tvNoBookings.setVisibility(View.VISIBLE);
        }

    }


}

