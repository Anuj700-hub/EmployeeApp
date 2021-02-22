package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ChangePassword;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Guest;
import com.hungerbox.customer.model.GuestListResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.AvtiveGuestListAdapter;
import com.hungerbox.customer.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuestListDialog.OnPasswordChangeListener} interface
 * to handle interaction events.
 * Use the {@link GuestListDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuestListDialog extends DialogFragment {

    Button btPositiveButton;
    RecyclerView rlGuestList;
    ProgressBar pbGuest;
    TextView tvNoGuestFound;
    AvtiveGuestListAdapter avtiveGuestListAdapter;
    private OnPasswordChangeListener mListener;

    public GuestListDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartCancelDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static GuestListDialog newInstance() {
        GuestListDialog fragment = new GuestListDialog();
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
        View view = inflater.inflate(R.layout.fragment_guest_list, container, false);
        btPositiveButton = view.findViewById(R.id.bt_positive_button);
        rlGuestList = view.findViewById(R.id.rl_guest_list);
        pbGuest = view.findViewById(R.id.pb_guest_list);
        tvNoGuestFound = view.findViewById(R.id.tv_no_guest_list);
        btPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle yes click
                dismiss();
            }
        });
        rlGuestList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getUserList();
        return view;
    }

    private void getUserList() {
        pbGuest.setVisibility(View.VISIBLE);
        String url = UrlConstant.GUEST_LIST + "?status=approved";
        SimpleHttpAgent<GuestListResponse> guestListResponseSimpleHttpAgent = new SimpleHttpAgent<GuestListResponse>(
                getActivity(),
                url,
                new ResponseListener<GuestListResponse>() {
                    @Override
                    public void response(GuestListResponse responseObject) {
                        pbGuest.setVisibility(View.INVISIBLE);
                        if (responseObject != null) {
                            setUpGuestList(responseObject.getGuests());
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        //TODO show no geust count abb
                        pbGuest.setVisibility(View.INVISIBLE);
                        setUpGuestList(new ArrayList<Guest>());
                    }
                },
                GuestListResponse.class
        );

        guestListResponseSimpleHttpAgent.get();
    }

    private void setUpGuestList(ArrayList<Guest> guests) {
        if (guests.size() <= 0) {
            rlGuestList.setVisibility(View.GONE);
            tvNoGuestFound.setVisibility(View.VISIBLE);
        } else {
            rlGuestList.setVisibility(View.VISIBLE);
            tvNoGuestFound.setVisibility(View.GONE);
        }
        avtiveGuestListAdapter = new AvtiveGuestListAdapter(getActivity(), guests);
        rlGuestList.setAdapter(avtiveGuestListAdapter);
    }


    private void performPasswordChange(long userId, String oldPassword, String newPassword) {
        String url = UrlConstant.CHANGE_PASSWORD;
        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        if (mListener != null)
                            mListener.onPasswordChanged();
                        else
                            doLogout();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                Object.class
        );

        objectSimpleHttpAgent.post(new ChangePassword().setUserId(userId).setOldPassword(oldPassword).setPassword(newPassword).setPasswordConfirmation(newPassword), new HashMap<String, JsonSerializer>());

    }

    private void doLogout() {
        if (getActivity() != null)
            AppUtils.doLogout(getActivity());
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
    public interface OnPasswordChangeListener {
        // TODO: Update argument type and name
        void onPasswordChanged();

        void onCancel();
    }
}
