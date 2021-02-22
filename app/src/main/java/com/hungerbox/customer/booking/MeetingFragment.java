package com.hungerbox.customer.booking;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeetingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeetingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeetingFragment extends Fragment {

    WebView wvEvents;
    String url;
    ProgressBar pbLoader;
    private OnFragmentInteractionListener mListener;
    private boolean isHistory;

    public MeetingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MeetingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeetingFragment newInstance(boolean isHistory, String urlPart) {
        MeetingFragment fragment = new MeetingFragment();
        fragment.isHistory = isHistory;
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

        final WebSettings settings = wvEvents.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getActivity().getCacheDir().getPath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wvEvents.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            wvEvents.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        wvEvents.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 95) {
                    pbLoader.setVisibility(View.GONE);
                }
            }
        });
        wvEvents.addJavascriptInterface(new WebViewInterFace((MeetingBaseActivty) getActivity()), "Android");

        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        String cookieString = "webAccessToken=" + token;
        cookieManager.setCookie(UrlConstant.BASE_URL, cookieString);
        CookieSyncManager.getInstance().sync();

        Map<String, String> abc = new HashMap<String, String>();
        abc.put("Cookie", cookieString);
        pbLoader.setVisibility(View.VISIBLE);

        String subdomain = SharedPrefUtil.getString(ApplicationConstants.COMPANY_SUBDOMAIN, "");
        if (subdomain.isEmpty()) {
            subdomain = "paladion";
        }

        if (isHistory) {
            wvEvents.loadUrl(UrlConstant.MEETINGS_HISTORY_WEB_VIEW.replace("{1}", subdomain),
                    abc);
        } else {
            if (url == null || url.isEmpty()) {
                wvEvents.loadUrl(UrlConstant.MEETINGS_WEB_VIEW.replace("{1}", subdomain),
                        abc);
            } else {
                String loadUrl = "https://" + subdomain + ".hungerbox.com/#" + url;
                wvEvents.loadUrl(loadUrl,
                        abc);
                MeetingBaseActivty meetingBaseActivty = (MeetingBaseActivty) getActivity();
                meetingBaseActivty.toggleToolbar(false);
            }
        }
        return view;
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
        MeetingBaseActivty meetingBaseActivty;

        public WebViewInterFace(MeetingBaseActivty meetingBaseActivty) {
            this.mContext = meetingBaseActivty;
            this.meetingBaseActivty = meetingBaseActivty;
        }

        @JavascriptInterface
        public void displayHeader(boolean state) {
            if (meetingBaseActivty != null) {
                meetingBaseActivty.toggleToolbar(state);
            }
        }


        @JavascriptInterface
        public void thanks(String message) {
            if (meetingBaseActivty != null) {
                meetingBaseActivty.navigateToCheckoutView(message);
            }
        }

        @JavascriptInterface
        public void thanks(long id) {
            if (meetingBaseActivty != null) {
                meetingBaseActivty.getBookingFeedback(id);
            }
        }

    }
}
