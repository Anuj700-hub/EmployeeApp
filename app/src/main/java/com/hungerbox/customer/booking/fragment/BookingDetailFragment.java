package com.hungerbox.customer.booking.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.adapter.BookingDetailAdapter;
import com.hungerbox.customer.model.BookingDetail;
import com.hungerbox.customer.model.BookingDetailSlot;
import com.hungerbox.customer.model.BookingsResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.view.CustomNonScrollingLayoutManager;
import com.hungerbox.customer.util.view.FatLiDialog;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookingDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookingDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingDetailFragment extends Fragment {

    TextView tvLocationName;
    RecyclerView rvBookginList;
    ArrayList<BookingDetailSlot> bookingDetailSlots = new ArrayList<>();
    private long bookingId;
    private OnFragmentInteractionListener mListener;
    private String historyType;
    private LinearLayout llDate;
    private TextView tvDate;

    public BookingDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookingDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingDetailFragment newInstance(long bookingId, String historyType) {
        BookingDetailFragment fragment = new BookingDetailFragment();
        fragment.bookingId = bookingId;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        tvLocationName = view.findViewById(R.id.tv_location_name);
        rvBookginList = view.findViewById(R.id.tv_location_time_container);
        llDate = view.findViewById(R.id.ll_date);
        tvDate = view.findViewById(R.id.tv_date);
        rvBookginList.setLayoutManager(new CustomNonScrollingLayoutManager(getActivity()));

        getBookingListFromServer();

        return view;
    }

    private void getBookingListFromServer() {
        String url = UrlConstant.BOOKING_DETAIL + bookingId;

        SimpleHttpAgent<BookingsResponse> bookingsResponseSimpleHttpAgent = new SimpleHttpAgent<BookingsResponse>(
                getActivity(),
                url,
                new ResponseListener<BookingsResponse>() {
                    @Override
                    public void response(BookingsResponse responseObject) {
                        setupBookngDetail(responseObject.getBookings().get(0));
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                BookingsResponse.class
        );

        bookingsResponseSimpleHttpAgent.get();
    }

    private void setupBookngDetail(BookingDetail booking) {
        if (!historyType.equalsIgnoreCase("meeting")) {
            llDate.setVisibility(View.VISIBLE);
            tvDate.setText(booking.getEventStartDate() + " - "
                    + booking.getEventEndDate());
            tvLocationName.setText(booking.getLocation());

        } else {
            llDate.setVisibility(View.GONE);
            tvLocationName.setText(booking.getBookingLocation());

        }
        for (BookingDetailSlot bookingDetailSlot : booking.getMeetingSlots()) {
            if (bookingDetailSlot.getAvailable().equalsIgnoreCase("booked")) {
                bookingDetailSlots.add(bookingDetailSlot);
            }

        }

        setupListViews();
    }

    private void setupListViews() {
        if (rvBookginList.getAdapter() == null) {
            BookingDetailAdapter bookingDetailAdapter = new BookingDetailAdapter(getActivity(), bookingDetailSlots, new BookingDetailAdapter.BookingSlotListener() {
                @Override
                public void onCancelSlot(final int position, final long slotId) {
                    //TODO cancel slot
                    String msg = "Are you sure you want to cancel this booking? ";
                    FatLiDialog fatLiDialog = FatLiDialog.newInstance(0, null, new FatLiDialog.OnFragmentInteractionListener() {
                                @Override
                                public void onFragmentInteraction(int status, Object payload) {
                                    cancelSlot(position, slotId);
                                    rvBookginList.getAdapter().notifyDataSetChanged();
                                }
                            },
                            msg, "Slot Cancellation");
                    FragmentManager fragmentManager = getChildFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(fatLiDialog, "error")
                            .commitAllowingStateLoss();

                }
            });
            rvBookginList.setAdapter(bookingDetailAdapter);
        } else {
            if (rvBookginList.getAdapter() instanceof BookingDetailAdapter) {
                BookingDetailAdapter bookingDetailAdapter = (BookingDetailAdapter) rvBookginList.getAdapter();
                bookingDetailAdapter.notifyDataSetChanged();
            }
        }
    }

    private void cancelSlot(int position, long slotId) {
        BookingDetailSlot bookingDetailSlot = bookingDetailSlots.get(position);

        String url = UrlConstant.BOOKING_DETAIL_SLOT_CANCEL + bookingDetailSlot.getId();
        SimpleHttpAgent<Object> slotCancelEvent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        AppUtils.showToast("Your Slot has been cancelled", true, 0);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                Object.class
        );

        slotCancelEvent.put("cancel", new HashMap<String, JsonSerializer>());

        bookingDetailSlots.remove(position);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
