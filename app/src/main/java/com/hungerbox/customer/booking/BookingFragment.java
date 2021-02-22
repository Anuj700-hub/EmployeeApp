package com.hungerbox.customer.booking;

import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment {


    String url;
    ProgressBar pbLoader;
    String finalUrl;
    WebView wvEvents;
    private OnFragmentInteractionListener mListener;


    public BookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param urlPart
     * @return A new instance of fragment BookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingFragment newInstance(String urlPart) {
        BookingFragment fragment = new BookingFragment();
        fragment.url = urlPart;
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
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        wvEvents = view.findViewById(R.id.wv_events);
        pbLoader = view.findViewById(R.id.pb_loader);

        String token = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        final WebSettings settings = wvEvents.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getActivity().getCacheDir().getPath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wvEvents.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            wvEvents.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        wvEvents.clearCache(true);
        wvEvents.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }
        });
        wvEvents.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String currentUrl) {
                super.onPageFinished(view, url);
                pbLoader.setVisibility(View.GONE);
                if (currentUrl.contains("login") || currentUrl.contains("get-start")) {
                    loadAccessToken();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.cancel();
                if (error != null && error.toString().length() > 0) {
                    AppUtils.showToast(error.toString(), true, 0);
                }
            }
        });


        wvEvents.addJavascriptInterface(new WebViewInterFace((EventsBaseActivity) getActivity()), "Android");

        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        String cookieString = "webAccessToken=" + token;
        cookieManager.setCookie(UrlConstant.BASE_URL, cookieString);
        CookieSyncManager.getInstance().sync();

        Map<String, String> abc = new HashMap<String, String>();
        abc.put("Cookie", cookieString);
        String loadUrl;
        pbLoader.setVisibility(View.VISIBLE);

        String subdomain = SharedPrefUtil.getString(ApplicationConstants.COMPANY_SUBDOMAIN, "");
        if (subdomain.isEmpty() || subdomain.equalsIgnoreCase("abb")) {
            subdomain = "paladion";
        }

        if (url != null && !url.isEmpty()) {
            loadUrl = "https://" + subdomain + ".hungerbox.com/#/" + url;
            EventsBaseActivity eventsBaseActivity = (EventsBaseActivity) getActivity();
            eventsBaseActivity.toggleToolbar(false);
        } else {
            loadUrl = UrlConstant.EVENTS_WEB_VIEW;
            loadUrl = loadUrl.replace("{1}", subdomain);
        }
        if (subdomain.equalsIgnoreCase("qbase")) {
            loadUrl = loadUrl.replace("#/", "ios/#/");
            loadUrl = loadUrl.replace("https", "http");
        }
        finalUrl = loadUrl;
        wvEvents.loadUrl(loadUrl,
                abc);

        return view;
    }

    private void loadAccessToken() {
        String token = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
        String function = String.format("javascript:updateWebAccessToken('%s', '%s')", token, finalUrl);
        wvEvents.loadUrl(function);
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

    public void goBackInWebView() {
        if (wvEvents != null) {
            wvEvents.goBack();
        }
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


    class WebViewInterFace {
        Context mContext;
        EventsBaseActivity eventsBaseActivity;

        public WebViewInterFace(EventsBaseActivity eventsBaseActivity) {
            this.mContext = eventsBaseActivity;
            this.eventsBaseActivity = eventsBaseActivity;
        }

        @JavascriptInterface
        public void displayHeader(boolean state) {
            if (eventsBaseActivity != null) {
                eventsBaseActivity.toggleToolbar(state);
            }
        }

        @JavascriptInterface
        public void onHideToolbar() {
            if (eventsBaseActivity != null) {
                eventsBaseActivity.toggleToolbar(false);
            }
        }

        @JavascriptInterface
        public void thanks(String message) {
            if (eventsBaseActivity != null) {

            }
        }

        @JavascriptInterface
        public void processPayment(long vendorId, long bookingId, String type, long locationId, long productId, int quantity, double price, String message) {
            if (eventsBaseActivity != null) {
                eventsBaseActivity.navigateToCheckoutView(vendorId, bookingId, type, locationId, productId, quantity, price, message);
            }
        }


    }
}
