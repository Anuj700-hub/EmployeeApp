package com.hungerbox.customer.navmenu.fragment;

import android.content.Context;
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
import com.hungerbox.customer.model.BookingHistory;
import com.hungerbox.customer.model.BookingHistoryResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.navmenu.listeners.UpdateListener;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistoryFragment extends Fragment implements UpdateListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<BookingHistory> bookingHistories = new ArrayList<>();
    private HistoryRecyclerViewAdapter historyAdapter;
    private View view;
    private RecyclerView recyclerView;
    private TextView tvNoBookings;
    private String historyType = "";
    private ProgressBar pbHistory;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public HistoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistoryFragment newInstance(int columnCount, String historyType) {
        HistoryFragment fragment = new HistoryFragment();
        fragment.historyType = historyType;
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
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
        view = inflater.inflate(R.layout.fragment_history_list, container, false);

        // Set the adapter

        recyclerView = view.findViewById(R.id.list);
        tvNoBookings = view.findViewById(R.id.tv_no_bookings);
        pbHistory = view.findViewById(R.id.pb_history);
        tvNoBookings.setVisibility(View.GONE);
        getUserBookingHistory();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void getUserBookingHistory() {
        String url;
        pbHistory.setVisibility(View.VISIBLE);
        if (historyType.equalsIgnoreCase("meeting"))
            url = UrlConstant.BOOKED_MEETINGS_HISTORY;
        else
            url = UrlConstant.BOOKED_EVENTS_HISTORY;
        SimpleHttpAgent<BookingHistoryResponse> bookingHistoryResponseAgent =
                new SimpleHttpAgent<BookingHistoryResponse>(getActivity(), url,
                        new ResponseListener<BookingHistoryResponse>() {
                            @Override
                            public void response(BookingHistoryResponse responseObject) {
                                pbHistory.setVisibility(View.GONE);
                                if (historyAdapter == null) {
                                    bookingHistories = responseObject.getBookingHistories();
                                    historyAdapter = new HistoryRecyclerViewAdapter(getActivity(),
                                            bookingHistories, mListener, historyType);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerView.setAdapter(historyAdapter);
                                } else {
                                    bookingHistories.clear();
                                    bookingHistories.addAll(responseObject.getBookingHistories());
                                    historyAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(historyAdapter);
                                }
                                if (responseObject.getBookingHistories().size() == 0)
                                    tvNoBookings.setVisibility(View.VISIBLE);
                                else
                                    tvNoBookings.setVisibility(View.GONE);


                            }
                        }, new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                }, BookingHistoryResponse.class);

        bookingHistoryResponseAgent.get();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
        getUserBookingHistory();

    }

    @Override
    public void updateData() {
        update();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(BookingHistory item, String eventType, Order order);
    }
}
