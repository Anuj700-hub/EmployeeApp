package com.hungerbox.customer.offline.activityOffline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.adapterOffline.OrderFeedAdapterOffline;
import com.hungerbox.customer.offline.fragmentOffline.FreeOrderErrorHandleDialogOffline;
import com.hungerbox.customer.offline.listenersOffline.VendorValidationListener;
import com.hungerbox.customer.offline.modelOffline.OcassionReposneOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.order.activity.OrderReviewActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderFeedFragmentOffline extends Fragment implements VendorValidationListener {

    public String location = "India T";
    public String city = "Bangalore";
    public long locationId;
    public TextView tvCart, tvTitle, tvCartAmount;
    public RelativeLayout flFloatingContainer;
    protected Button btCheckout;
    public RecyclerView rvOrderFeed;
    public OrderFeedAdapterOffline orderFeedAdpater;
    TextView tvNoVendorFound;
    ArrayList<VendorOffline> vendorRealmResults;
    long ocassionId;
    private ArrayList<Object> baseBanners = new ArrayList<>();
    private Activity activity;
    private RelativeLayout baseView;
    public ShimmerFrameLayout shimmerView;
    private ArrayList<VendorOffline> vendorList = new ArrayList<>();

    private boolean openQR = false;


    public OrderFeedFragmentOffline() {
    }

    public static OrderFeedFragmentOffline newInstance(Activity activity, String campusLocation, String city, long ocassionId) {
        OrderFeedFragmentOffline fragment = new OrderFeedFragmentOffline();
        fragment.activity = activity;
        fragment.location = campusLocation;
        fragment.city = city;
        fragment.ocassionId = ocassionId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_feed_container_offline, container, false);


        baseView = view.findViewById(R.id.rl);
        shimmerView = view.findViewById(R.id.shimmer_view_container);
        shimmerView.startShimmer();

        ((ShimmerFrameLayout) (view.findViewById(R.id.shimmer_view_container))).startShimmer();
        rvOrderFeed = view.findViewById(R.id.rv_order_feed);
        tvNoVendorFound = view.findViewById(R.id.tv_no_vendors_found);

        flFloatingContainer = view.findViewById(R.id.fl_cart_container);
        tvCart = view.findViewById(R.id.tv_cart);
        tvCartAmount = view.findViewById(R.id.tv_order_amount);
        btCheckout = view.findViewById(R.id.bt_checkout);

        EventUtil.FbEventLog(activity, EventUtil.SCREEN_OPEN_HOME, EventUtil.SCREEN_HOME);


        flFloatingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.logBaseEvent(activity, EventUtil.CHECKOUT_CLICK);
                navigateToOrderReview(view);

                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_CHECKOUT, jo);

                    Bundle bundle = new Bundle();
                    bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                    EventUtil.FbEventLog(activity, EventUtil.MixpanelEvent.MENU_CHECKOUT, bundle);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        });
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtil.FbEventLog(activity, EventUtil.HOME_CHECKOUT_RECOMMENDED, EventUtil.SCREEN_HOME);
                EventUtil.logBaseEvent(activity, EventUtil.CHECKOUT_CLICK);
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_CHECKOUT, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                navigateToOrderReview(v);
            }
        });
        location = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);

        SharedPrefUtil.putBoolean(ApplicationConstants.SHOULD_REFRESH_FROM_CHAT, false);



        tvNoVendorFound.setVisibility(View.GONE);


        return view;
    }


    private void navigateToOrderReview(View view) {
        LogoutTask.updateTime();
        Intent intent = new Intent(activity, OrderReviewActivity.class);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
        intent.putExtra("anim", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }


    public void refreshLogic(boolean exitPip) {
        try {

            if (getActivity() != null) {
                SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);
            }

            Calendar calendar = Calendar.getInstance();
            long currentOccasionId = 0;
            for (int i = 0; i < ((GlobalActivityOffline) getActivity()).occasionChooserAdapter.getOccassionResponse().size(); i++) {
                if (calendar.getTimeInMillis() >
                        DateTimeUtil.getTodaysTimeFromString(((GlobalActivityOffline) getActivity()).occasionChooserAdapter.getOccassionResponse().get(i).startTime)
                        &&
                        calendar.getTimeInMillis() <
                                DateTimeUtil.getTodaysTimeFromString(((GlobalActivityOffline) getActivity()).occasionChooserAdapter.getOccassionResponse().get(i).endTime)) {
                    currentOccasionId = ((GlobalActivityOffline) getActivity()).occasionChooserAdapter.getOccassionResponse().get(i).id;
                    break;
                }
            }
            if (ocassionId != currentOccasionId && MainApplicationOffline.isCartCreated()) {
                showCartClearPopUp();
            } else {
                ((GlobalActivityOffline) getActivity()).getOccassionList();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private void showCartClearPopUp() {
        FreeOrderErrorHandleDialogOffline orderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(new OrderOffline(),
                "Your cart will get cleared",
                new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        MainApplicationOffline.clearOrder(1);
                        MainApplicationOffline.bus.post(new OrderClear());
                        ((GlobalActivityOffline) getActivity()).getOccassionList();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                }, "OK", "CANCEL");

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(orderErrorHandleDialog, "cart_clear")
                .commitAllowingStateLoss();
    }


    public void updateViewWithSelectedOcassion(long occasionId) {
        try {
            if (activity == null) {
                activity = getActivity();
            }
        } catch (Exception exp) {
        }

        if (activity == null) {
            return;
        }

        if (occasionId == 0) {
            setVendorsAndUpdateUi(new ArrayList<VendorOffline>());
        }
        ocassionId = occasionId;
        getVendorFromServerAndUpdateView();
    }


    @Override
    public void onResume() {
        super.onResume();
        MainApplicationOffline.bus.register(this);
        setUpCart();
        verifyLocationChange();
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplicationOffline.bus.unregister(this);
    }

    private void verifyLocationChange() {
        long newLocationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        String newLocationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "");
        if (locationId != newLocationId) {
            locationId = newLocationId;
            location = newLocationName;
            Activity activity = getActivity();
            if (activity != null) {
                GlobalActivityOffline globalActivity = (GlobalActivityOffline) activity;
                globalActivity.getOccassionList();
            }
        }
    }

    public void setUpCart() {
        int cartQty = MainApplicationOffline.getTotalOrderCount(1);
        double cardAmount = MainApplicationOffline.getTotalOrderAmount(1);
        if (cartQty <= 0) {
            flFloatingContainer.setVisibility(View.GONE);
        } else {
            flFloatingContainer.setVisibility(View.VISIBLE);
            if (cartQty >= 10)
                tvCart.setText(String.format("%d Items  ", cartQty));
            else
                tvCart.setText(String.format("  %d Items ", cartQty));
            tvCartAmount.setText(String.format("₹ %.2f", cardAmount));
        }
    }

    private void setVendorsAndUpdateUi(ArrayList<VendorOffline> vendorOfflines) {
        if (vendorOfflines != null && vendorOfflines.size() > 0) {
            if (getActivity() == null || activity == null)
                return;

            ArrayList<VendorOffline> vendorOfflineArrayList = new ArrayList<>();
            final long currentTime = Calendar.getInstance().getTimeInMillis();

            for (VendorOffline vendorOffline : vendorOfflines) {
                if(vendorOffline.getActive()==1) {
                    long vendorStartTime = DateTimeUtil.getTodaysTimeFromString(vendorOffline.getStartTime());
                    final long vendorEndTime = DateTimeUtil.getTodaysTimeFromString(vendorOffline.getEndTime());
                    if (currentTime < vendorStartTime || currentTime > vendorEndTime) {
                        vendorOffline.setActive(0);
                    }
                }
                if(vendorOffline.getActive()==1) {
                    vendorOfflineArrayList.add(vendorOffline);
                }
            }
            rvOrderFeed.setVisibility(View.VISIBLE);

            tvNoVendorFound.setVisibility(View.GONE);
            try {
                baseView.setVisibility(View.VISIBLE);
                shimmerView.stopShimmer();
                shimmerView.setVisibility(View.GONE);
            } catch (Exception exp) {
                exp.printStackTrace();
            }

            if (orderFeedAdpater == null) {
                orderFeedAdpater = new OrderFeedAdapterOffline((GlobalActivityOffline) activity,
                        vendorOfflineArrayList, this, ocassionId,
                        false, baseBanners, true, location, getChildFragmentManager());

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2) {
                    @Override
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0) {
                            return 2;
                        } else if (position > vendorList.size() + 2) {
                            return 1;
                        } else if (position > 1) {
                            return 1;
                        } else {
                            return 2;
                        }
                    }
                });
                rvOrderFeed.setLayoutManager(gridLayoutManager);
                rvOrderFeed.setAdapter(orderFeedAdpater);

            } else {
                orderFeedAdpater.replaceVendors(vendorOfflineArrayList);
                orderFeedAdpater.setOcassionId(ocassionId);
                orderFeedAdpater.notifyDataSetChanged();
            }
        } else {
            rvOrderFeed.setVisibility(View.GONE);
            tvNoVendorFound.setVisibility(View.VISIBLE);
            shimmerView.stopShimmer();
            shimmerView.setVisibility(View.GONE);
        }

    }

    private void getVendorFromServerAndUpdateView() {

        if (getActivity() == null || activity == null)
            return;

        String url = UrlConstantsOffline.VENDOR + "?locationId=" + locationId + "&occasionId=" + ocassionId;
        if (ocassionId == 0) {
            setVendorsAndUpdateUi(new ArrayList<VendorOffline>());
            return;
        }


        SimpleHttpAgent<OcassionReposneOffline> venSimpleHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
                url,
                new ResponseListener<OcassionReposneOffline>() {
                    @Override
                    public void response(OcassionReposneOffline responseObject) {
                        GlobalActivityOffline globalActivity = (GlobalActivityOffline) getActivity();

                        if (responseObject != null && responseObject.getOcassions() != null && responseObject.getOcassions().size() > 0 && responseObject.getOcassions().get(0).vendors.vendors.size() > 0) {

                            if (globalActivity != null) {
                                SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, true);
                            }

                            setVendorsAndUpdateUi(responseObject.getOcassions().get(0).vendors.vendors);
                        } else {
                            if (globalActivity != null) {

                                SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);

                                setVendorsAndUpdateUi(new ArrayList<VendorOffline>());
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        Activity activity = getActivity();
                        if (activity != null) {

                            SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);

                            if (activity instanceof GlobalActivityOffline) {

                            }
                        }
                    }

                },
                OcassionReposneOffline.class
        );
        venSimpleHttpAgent.get();

    }

    @Subscribe
    public void onOrderClearEvent(OrderClear orderClear) {
        flFloatingContainer.setVisibility(View.GONE);
    }

    @Override
    public void validateAndAddProduct(VendorOffline vendor, ProductOffline product, boolean isBuffet) {

    }
}
