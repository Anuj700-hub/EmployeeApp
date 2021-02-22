package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.Map;

public class SimplWebViewFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "data";
    private static final String IS_CANCELABLE = "isCancelable";

    WebView paymentWebView;
    ProgressBar pbLoader;
    LinearLayout llPaymentCancelContainer;
    Button btNegative, btPositive;
    String method;
    Map<String, String> postData;
    boolean transactionComplete = false;

    private OnRedirectListener mListener;
    private String url;

    public SimplWebViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SimplWebViewFragment newInstance(String url, OnRedirectListener listener) {
        SimplWebViewFragment fragment = new SimplWebViewFragment();
        fragment.url = url;
        fragment.mListener = listener;
        fragment.method = ApplicationConstants.HTTP_GET;
        fragment.postData = null;
        return fragment;
    }


    public static SimplWebViewFragment newInstance(String url, String method, Map<String, String> postData, OnRedirectListener listener) {
        SimplWebViewFragment fragment = new SimplWebViewFragment();
        fragment.url = url;
        fragment.mListener = listener;
        if (method == null)
            method = ApplicationConstants.HTTP_POST;
        fragment.method = method;
        fragment.postData = postData;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setRetainInstance(true);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        paymentWebView = view.findViewById(R.id.webview_layout);
        pbLoader = view.findViewById(R.id.pb_loader);
        llPaymentCancelContainer = view.findViewById(R.id.ll_back_container);
        btNegative = view.findViewById(R.id.btn_negative);
        btPositive = view.findViewById(R.id.btn_positive);

        paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    if(request.getUrl().toString().contains("#/status/simpl/success"))
//                    {
//                        mListener.onRedirect(request.getUrl().toString(),true);
//                        dismissAllowingStateLoss();
//                    }else if(url.contains("#/status/simpl/error")){
//                        mListener.onRedirect(request.getUrl().toString(),false);
//                        dismissAllowingStateLoss();
//                    }
//                }
                return super.shouldOverrideUrlLoading(view, request);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.matches(".+#/status/[a-zA-Z]+/success.*")) {
                    transactionComplete = true;
                    if (mListener != null)
                        mListener.onRedirect(url, true, "");
                    dismissAllowingStateLoss();
                    return true;

                } else if (url.matches(".+#/status/[a-zA-Z]+/error.*")) {
                    if (mListener != null) {
                        url = url.replace("#", "");
                        Uri errorUri = Uri.parse(url);
                        String reason = errorUri.getQueryParameter("reason");
                        if (reason != null)
                            mListener.onRedirect(url, false, reason);
                        else
                            mListener.onRedirect(url, false, "");
                    }
                    transactionComplete = true;
                    dismissAllowingStateLoss();
                    return true;
                } else if (url.contains("/user_cancelled")) {
                    transactionComplete = true;
                    if (mListener != null)
                        mListener.onRedirect(url, false, "");
                    dismissAllowingStateLoss();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoader.setVisibility(View.GONE);
                paymentWebView.clearHistory();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                request.getUrl()
                if (view.getUrl().contains(UrlConstant.ZETA_TRANSACTION_VALIDATION)) {
                    loadWebView();
                } else if (view.getUrl().contains("hungerbox")) {
                    if (mListener != null)
                        mListener.onRedirect(url, false, "");
                }
            }
        });

        WebSettings settings = paymentWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            paymentWebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setJavaScriptEnabled(true);
        paymentWebView.getSettings().setLoadWithOverviewMode(true);
        paymentWebView.getSettings().setBuiltInZoomControls(true);
        paymentWebView.getSettings().setSupportZoom(true);
        loadWebView();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        return true;
                    else {
//                        AppUtils.HbLog("Peeyush", "back key detected");
//                        if(mListener!=null){
//                            mListener.onBackClicked(postData);
//                        }
                        llPaymentCancelContainer.setVisibility(View.VISIBLE);
                        return true; // pretend we've processed it
                    }
                } else
                    return false;
            }
        });

        llPaymentCancelContainer.setVisibility(View.GONE);
        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentCancelNegativeAction();
            }
        });

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentCancelPositiveAction();
            }
        });

        return view;
    }

    private void paymentCancelPositiveAction() {
        if (mListener != null)
            mListener.onBackClicked(postData);
        dismissAllowingStateLoss();
    }

    private void paymentCancelNegativeAction() {
        llPaymentCancelContainer.setVisibility(View.GONE);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        if (transactionComplete)
            AppUtils.HbLog("Peeyush", "simpl cancelled");
        else {
            if (mListener != null)
                mListener.onBackClicked(postData);
            AppUtils.HbLog("Peeyush", "back detected");
        }
    }

    private void loadWebView() {
        if (method.equalsIgnoreCase(ApplicationConstants.REDIRECTION)) {
            paymentWebView.loadUrl(url);
        } else if (method.equals(ApplicationConstants.HTTP_GET)) {
            paymentWebView.loadUrl(url);
        } else {
            StringBuilder sb = new StringBuilder();

            sb.append("<html><head></head>");
            sb.append("<body>");
            sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
            for (String key : postData.keySet()) {
                sb.append(String.format("<input name='%s' type='hidden' value='%s' />", key, postData.get(key)));
            }
            sb.append("</form>");
//            sb.append("<script>document.getElementById('form1').submit();</script>");
            sb.append("<script>setTimeout(function(){ document.getElementById('form1').submit(); }, 1000);</script>");
            sb.append("</body></html>");

            String url = UrlConstant.ZETA_TRANSACTION_VALIDATION + "/" + postData.get("order_id");

            paymentWebView.loadUrl(url);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            //This is not implemented on all the phones
            paymentWebView.destroy();
        } catch (Exception e) {
            AppUtils.logException(e);
        }
    }

    public void updateOtp(String otp) {
        if (paymentWebView != null) {
            paymentWebView.loadUrl(String.format("javascript:try{document.getElementById('otp-box').value=%s;}catch(e){}", otp));
        }
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
    public interface OnRedirectListener {
        // TODO: Update argument type and name
        void onRedirect(String url, boolean isSuccess, String reason);

        void onBackClicked(Map<String, String> postData);
    }
}
