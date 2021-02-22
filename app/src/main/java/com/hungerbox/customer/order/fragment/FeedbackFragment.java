package com.hungerbox.customer.order.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FeedbackOptionResposne;
import com.hungerbox.customer.model.FeedbackRatingReasonResponse;
import com.hungerbox.customer.model.FeedbackReason;
import com.hungerbox.customer.model.FeedbackResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.adapter.FeedbackEditTextView;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;


public class FeedbackFragment extends DialogFragment {


    Button btSubmit;
    TextView tvTitle,feedbackReasonTv;
    HashMap<Integer, ArrayList<FeedbackReason>> feedbackRatingReasons = new HashMap<>();
    LinearLayout llFeedbackReasonContainer,llMoodRatingContainer;
    int selectedRating;
    private long id, vendorId;
    private String type = "", vendorName = "";
    private String description = "";
    private RatingBar rbFeedback;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    ImageView ivSad, ivNeutral, ivHappy,ivClose, ivFeedbackLogo;
    ImageView emoji;
    ScrollView descriptionSv;
    private TextView feedbackQueryTv,vendorNameTv, feedbackText, tvHeaderText;
    private long referenceId;
    private boolean forWashroomFeedback = false;
    private Order order;

    public FeedbackFragment() {
    }

    public static FeedbackFragment newInstance() {
        return newInstance();
    }

    public static FeedbackFragment newInstance(long id, String type, long vendorId, String vendorName, String description, String reference, Order order, OnFragmentInteractionListener onFragmentInteractionListener) {
        FeedbackFragment fragment = new FeedbackFragment();
        fragment.id = id;
        fragment.type = type;
        fragment.vendorId = vendorId;
        fragment.vendorName = vendorName;
        fragment.description = description;
        fragment.order = order;
        fragment.onFragmentInteractionListener = onFragmentInteractionListener;
        return fragment;
    }

    public static FeedbackFragment newInstance(long id, String type, long referenceId, OnFragmentInteractionListener onFragmentInteractionListener) {
        FeedbackFragment fragment = new FeedbackFragment();
        fragment.id = id;
        fragment.type = type;
        fragment.referenceId = referenceId;
        fragment.forWashroomFeedback = true;
        fragment.onFragmentInteractionListener = onFragmentInteractionListener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        descriptionSv = view.findViewById(R.id.sv_description);
        btSubmit = view.findViewById(R.id.btn_submit_feeedback);
        rbFeedback = view.findViewById(R.id.rb_feedback_rating);
        tvTitle = view.findViewById(R.id.tv_feedback);
        feedbackReasonTv = view.findViewById(R.id.tv_what_went_wrong);
        llFeedbackReasonContainer = view.findViewById(R.id.ll_form_container);
        llMoodRatingContainer = view.findViewById(R.id.ll_mood_rating_container);
        ivSad = view.findViewById(R.id.iv_bad);
        ivNeutral = view.findViewById(R.id.iv_neutral);
        ivHappy = view.findViewById(R.id.iv_happy);
        ivClose = view.findViewById(R.id.iv_close);
        emoji = view.findViewById(R.id.iv_emoji);
        feedbackQueryTv = view.findViewById(R.id.tv_feedback_query);
        vendorNameTv = view.findViewById(R.id.tv_vendor_name);
        feedbackText = view.findViewById(R.id.tv_feedback_text);
        ivFeedbackLogo = view.findViewById(R.id.iv_order_status);
        tvHeaderText = view.findViewById(R.id.tv_order_status);

        tvTitle.setText(description);
        ViewGroup.LayoutParams params = descriptionSv.getLayoutParams();

        if(getActivity()!=null) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            if (description.length() < 40) {
                params.height = (int) (displayMetrics.heightPixels * 0.14);
            } else if (description.length() < 70) {
                params.height = (int) (displayMetrics.heightPixels * 0.20);
            } else {
                params.height = (int) (displayMetrics.heightPixels * 0.26);
            }
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        descriptionSv.setLayoutParams(params);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
                dismiss();
            }
        });

        if (AppUtils.getConfig(getActivity()).is_feedback_skip_allowed()){
            ivClose.setVisibility(View.VISIBLE);
        }else{
            ivClose.setVisibility(View.GONE);
        }

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedRating > 0){
                    btSubmit.setEnabled(false);
                    submitFeedbackToServer();
                } else
                    AppUtils.showToast("Please give a rating to submit", true, 2);
            }
        });
        setFeedbackText();

        initializeRatingBar();

        setupRatingReason();

        return view;
    }

    private void setFeedbackText(){
        feedbackReasonTv.setVisibility(View.GONE);
        if (vendorName!=null && !vendorName.equalsIgnoreCase("")){
            vendorNameTv.setVisibility(View.VISIBLE);
            vendorNameTv.setText(vendorName+"?");
            feedbackQueryTv.setText("How was your food from");
        } else{
            vendorNameTv.setVisibility(View.GONE);
        }
        if(forWashroomFeedback){
            feedbackQueryTv.setText("How was your experience of using the restroom?");
            ivFeedbackLogo.setImageResource(0);
            ivFeedbackLogo.getLayoutParams().height = (int) getResources().getDimension(R.dimen.dimen_30);
            ivFeedbackLogo.getLayoutParams().width = (int) getResources().getDimension(R.dimen.dimen_36);
            tvHeaderText.setText("Washroom Feedback");
            ivFeedbackLogo.setBackgroundResource(R.drawable.ic_washroom);
        }
        if(order != null && order.isSpaceBookingOrder()){
            feedbackQueryTv.setText("How was your experience from");
            tvHeaderText.setText("Your booking has been fulfilled");
            vendorNameTv.setVisibility(View.VISIBLE);
            if(order.getProducts().size() > 0){
                vendorNameTv.setText(order.getProducts().get(0).getName() + "?");
            }
        }
    }

    private void setupRatingReason() {
        getFeedbackReasonFromServer();
        rbFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                selectedRating = (int) rating;
                if (selectedRating > 0) {
                    setRatingEmoji(selectedRating);
                    populateFeebackReason();
                }
            }
        });
    }

    private void setRatingEmoji(int rating){
        if (!AppUtils.getConfig(getActivity()).isSentiment_feedback()) {
            emoji.setVisibility(View.VISIBLE);
            switch (rating) {
                case 1: {
                    emoji.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_emoji_feedback_angry));
                    feedbackText.setText("We are sorry you had a bad experience");
                }
                break;
                case 2: {
                    emoji.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_emoji_crying));
                    feedbackText.setText("Thank you for your time");
                }
                break;
                case 3: {
                    emoji.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_emoji_meh));
                    feedbackText.setText("Thank you for your time");
                }
                break;
                case 4: {
                    emoji.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_emoji_feedback_happy));
                    feedbackText.setText("Thank you for your time");
                }
                break;
                case 5: {
                    emoji.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_emoji_love));
                    feedbackText.setText("Thank you for your time");
                }
                break;
                default: {
                    emoji.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initializeRatingBar(){
        if (AppUtils.getConfig(getActivity()).isSentiment_feedback()) {
            emoji.setVisibility(View.GONE);
            rbFeedback.setVisibility(View.GONE);
            llMoodRatingContainer.setVisibility(View.VISIBLE);

            if(AppUtils.getConfig(getActivity()).getSentiment_feedback_count()==2){
                ivNeutral.setVisibility(View.GONE);
            }else{
                ivNeutral.setVisibility(View.VISIBLE);
            }

            ivSad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedRating = 1;
                    //ivSad.setColorFilter(ContextCompat.getColor(getActivity(), R.color.rating_sad));
//                    ivNeutral.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_grey));
//                    ivHappy.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_grey));

                    ivNeutral.setAlpha(0.5f);
                    ivHappy.setAlpha(0.5f);
                    ivSad.setAlpha(1f);
                    animationIcon(ivSad);
                    populateFeebackReason();
                }
            });
            ivNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedRating = 3;
                    //ivSad.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_grey));
                    //ivNeutral.setColorFilter(ContextCompat.getColor(getActivity(), R.color.rating_neutral));
                    //ivHappy.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_grey));
                    ivSad.setAlpha(0.5f);
                    ivHappy.setAlpha(0.5f);
                    ivNeutral.setAlpha(1f);

                    animationIcon(ivNeutral);
                    populateFeebackReason();
                }
            });
            ivHappy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedRating = 5;
                    //ivSad.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_grey));
                    //ivNeutral.setColorFilter(ContextCompat.getColor(getActivity(), R.color.light_grey));
                    //ivHappy.setColorFilter(ContextCompat.getColor(getActivity(), R.color.rating_happy));
                    ivSad.setAlpha(0.5f);
                    ivNeutral.setAlpha(0.5f);
                    ivHappy.setAlpha(1f);

                    animationIcon(ivHappy);
                    populateFeebackReason();
                }
            });
        } else {
            rbFeedback.setVisibility(View.VISIBLE);
            llMoodRatingContainer.setVisibility(View.GONE);
        }
    }

    private void animationIcon(ImageView imageView){
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) ivHappy.getLayoutParams();
        int dimen = Math.round(AppUtils.convertDpToPixel(36,getActivity()));
        params.width = dimen;
        params.height = dimen;
//        ivHappy.setLayoutParams(params);
//        ivNeutral.setLayoutParams(params);
//        ivSad.setLayoutParams(params);
        ivSad.clearAnimation();
        ivNeutral.clearAnimation();
        ivHappy.clearAnimation();


//        ViewGroup.LayoutParams largeParams = (ViewGroup.LayoutParams) ivHappy.getLayoutParams();
//        int dimenLarge = Math.round(AppUtils.convertDpToPixel(48,getActivity()));
//        largeParams.width = dimenLarge;
//        largeParams.height = dimenLarge;

        Animation logoMoveAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.image_large);
        logoMoveAnimation.setFillAfter(true);
        imageView.startAnimation(logoMoveAnimation);


    }

    private void populateFeebackReason() {

        llFeedbackReasonContainer.removeAllViews();

        if (feedbackRatingReasons.get(selectedRating) != null)
            for (FeedbackReason feedbackReason : feedbackRatingReasons.get(selectedRating)) {
                createFeedback(llFeedbackReasonContainer, feedbackReason);
            }

//        getFeedbackReasonFromServer();
    }


    private void createFeedback(LinearLayout llFeedbackReasonContainer, FeedbackReason feedbackReason) {
        switch (feedbackReason.getType()) {
            case ApplicationConstants.FEEDBACK_TYPE_CHECK_BOX:
                createCheckBox(llFeedbackReasonContainer, feedbackReason);
                break;
            case ApplicationConstants.FEEDBACK_TYPE_INPUT_TEXT:
                createEditText(llFeedbackReasonContainer, feedbackReason);
                break;
            case ApplicationConstants.FEEDBACK_TYPE_RATING_BAR:
                createRatingBar(llFeedbackReasonContainer, feedbackReason);

            default:
                break;
        }

    }

    private void createRatingBar(LinearLayout llFeedbackReasonContainer, final FeedbackReason feedbackReason) {
        feedbackReasonTv.setVisibility(View.GONE);

        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setText(feedbackReason.getReason());
        llFeedbackReasonContainer.addView(textView);
        textView.setTextSize(15);

        RatingBar ratingBar = new RatingBar(getActivity());
        ratingBar.setScaleX(0.75f);
        ratingBar.setScaleY(0.75f);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ratingBar.setLayoutParams(layoutParams);
        ratingBar.setTag(feedbackReason);

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);
        ratingBar.setMax(5);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(getActivity(),R.color.gray),PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellow), PorterDuff.Mode.SRC_ATOP);
//        Drawable drawable = ratingBar.getProgressDrawable();
//        if (getActivity()!=null)
//        drawable.setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                feedbackReason.setAnswer(String.valueOf(rating));
            }
        });
        llFeedbackReasonContainer.addView(ratingBar);
    }

    private void createEditText(LinearLayout llFeedbackReasonContainer, final FeedbackReason feedbackReason) {
        FeedbackEditTextView editText = new FeedbackEditTextView(getActivity(), null);
//        editText.setHint(feedbackReason.getReason());
        editText.setEnabled(true);
        editText.setTag(feedbackReason);
        editText.setText(feedbackReason.getAnswer());
        editText.setMinLines(6);

        llFeedbackReasonContainer.addView(editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null)
                    feedbackReason.setAnswer(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        feedbackReasonTv.setText(feedbackReason.getReason());
        feedbackReasonTv.setVisibility(View.VISIBLE);

    }

    private void createCheckBox(LinearLayout llFeedbackReasonContainer, final FeedbackReason feedbackReason) {
        feedbackReasonTv.setVisibility(View.GONE);
        final CheckBox cb = new CheckBox(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cb.setTextAppearance(R.style.CheckBoxAccent);
        }
        cb.setChecked(false);
        cb.setText(feedbackReason.getReason());
        cb.setEnabled(true);
        cb.setTag(feedbackReason);
        if (feedbackReason.getAnswer().equalsIgnoreCase("1"))
            cb.setChecked(true);
        else
            cb.setChecked(false);
        llFeedbackReasonContainer.addView(cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    feedbackReason.setAnswer("1");
                else
                    feedbackReason.setAnswer("0");
            }
        });

        //addLine(llFeedbackReasonContainer);
    }

    private void addLine(LinearLayout llFeedbackReasonContainer) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setBackgroundResource(R.color.bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        params.setMargins(0, 5, 0, 5);
        imageView.setLayoutParams(params);
        llFeedbackReasonContainer.addView(imageView);
    }

    private void getFeedbackReasonFromServer() {
        String url = UrlConstant.FEEDBACK_REASON_API;
        if (id == 0 && !forWashroomFeedback)
            url = url + 0 + "/" + vendorId;
        else
            url += id;

        url += "?type=" + type;

        if(forWashroomFeedback){
            url += "&reference_id=" + referenceId;
        }

        SimpleHttpAgent<FeedbackRatingReasonResponse> feedbackRatingReasonResponseSimpleHttpAgent = new SimpleHttpAgent<FeedbackRatingReasonResponse>(
                getActivity(),
                url,
                new ResponseListener<FeedbackRatingReasonResponse>() {
                    @Override
                    public void response(FeedbackRatingReasonResponse responseObject) {
                        if (responseObject != null) {
                            feedbackRatingReasons = responseObject.getFeedbackRatingResponse();
                            llFeedbackReasonContainer.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                    }
                },
                FeedbackRatingReasonResponse.class
        );

        feedbackRatingReasonResponseSimpleHttpAgent.get();
    }

    private void submitFeedbackToServer() {

        if (selectedRating <= AppUtils.getConfig(getActivity()).isVendor_feedback_comment_mandatory() && AppUtils.getConfig(getActivity()).isVendor_feedback_comment_mandatory() != -1) {

            boolean success = true;
            for (int i = 0; i < llFeedbackReasonContainer.getChildCount(); i++) {
                View view = llFeedbackReasonContainer.getChildAt(i);
                if (view instanceof FeedbackEditTextView) {
                    EditText et = ((FeedbackEditTextView)view).getEditText();
                    if (et.getText() == null || et.getText().toString().trim().isEmpty()) {
                        success = false;
                    }
                }
            }

            if (!success) {
                btSubmit.setEnabled(true);
                AppUtils.showToast(AppUtils.getConfig(getActivity()).getVendor_feedback_comment_mandatory_msg(), true, 2);
                return;
            }
        }

        FeedbackResponse feedbackResponse = new FeedbackResponse();
        if(forWashroomFeedback){
            feedbackResponse.setOrderId(referenceId);
        }
        else{
            feedbackResponse.setOrderId(id);
        }
        feedbackResponse.setType(type);
        feedbackResponse.setRating(selectedRating);
        feedbackResponse.setFeedbackOptionResposnes(new ArrayList<FeedbackOptionResposne>());
        cleverTapEvent(selectedRating);

        if(forWashroomFeedback){
            feedbackResponse.setReference(type);
        }
        else if (type.equalsIgnoreCase("booking")) {
            feedbackResponse.setReference("event");
        }
        for (int i = 0; i < llFeedbackReasonContainer.getChildCount(); i++) {
            View view = llFeedbackReasonContainer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                if (cb.isChecked()) {
                    FeedbackOptionResposne feedbackOptionResposne = new FeedbackOptionResposne();
                    FeedbackReason feedbackReason = (FeedbackReason) cb.getTag();
                    feedbackOptionResposne.setId(feedbackReason.getId());
                    feedbackOptionResposne.setValue("1");
                    feedbackResponse.getFeedbackOptionResposnes().add(feedbackOptionResposne);
                }
            } else if (view instanceof FeedbackEditTextView) {
                EditText et = ((FeedbackEditTextView)view).getEditText();
                if (et.getText() != null || !et.getText().toString().isEmpty()) {
                    FeedbackOptionResposne feedbackOptionResposne = new FeedbackOptionResposne();
                    FeedbackReason feedbackReason = (FeedbackReason) et.getTag();
                    feedbackOptionResposne.setId(feedbackReason.getId());
                    feedbackOptionResposne.setValue(et.getText().toString());
                    feedbackResponse.getFeedbackOptionResposnes().add(feedbackOptionResposne);
                }
            } else if (view instanceof RatingBar) {
                RatingBar ratingBar = (RatingBar) view;
                FeedbackOptionResposne feedbackOptionResposne = new FeedbackOptionResposne();
                FeedbackReason feedbackReason = (FeedbackReason) ratingBar.getTag();
                feedbackOptionResposne.setId(feedbackReason.getId());
                feedbackOptionResposne.setValue(String.valueOf(ratingBar.getRating()));
                feedbackResponse.getFeedbackOptionResposnes().add(feedbackOptionResposne);
            }
        }


        String url = UrlConstant.FEEDBACK_API;
        SimpleHttpAgent<Object> feedbackHttpAgent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        btSubmit.setEnabled(true);
                        if(onFragmentInteractionListener!=null) {
                            onFragmentInteractionListener.onFragmentInteraction();
                        }
                        dismissAllowingStateLoss();

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        btSubmit.setEnabled(true);
                        if(onFragmentInteractionListener!=null) {
                            onFragmentInteractionListener.onFragmentInteraction();
                        }
                        dismissAllowingStateLoss();

                    }
                },
                Object.class
        );

        feedbackHttpAgent.post(feedbackResponse, new HashMap<String, JsonSerializer>());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        cancelDialog();
//        cleverTapEvent(0);
//
//        if(AppUtils.getConfig(getContext()).isIgnore_feedback_on_skip()){
//            editor.remove(ApplicationConstants.IGNORE_FEEDBACK_ON_SKIP).apply();
//            editor.putLong(ApplicationConstants.IGNORE_FEEDBACK_ON_SKIP, id).apply();
//        }
    }

    private void cancelDialog(){
        cleverTapEvent(0);

        if(AppUtils.getConfig(getContext()).isIgnore_feedback_on_skip()){
            addToIgnoreList(id);
        }
    }

    private void addToIgnoreList(long orderId){
        ArrayList<Long> ignoreList = SharedPrefUtil.getLongArrayList(ApplicationConstants.IGNORE_FEEDBACK_ON_SKIP_LIST);
        ignoreList.add(orderId);
        SharedPrefUtil.putLongArrayList(ApplicationConstants.IGNORE_FEEDBACK_ON_SKIP_LIST, ignoreList);
    }

    private void cleverTapEvent(int selectedRating){
//        try{
//            HashMap<String,Object> map = new HashMap<>();
//            map.put(CleverTapEvent.PropertiesNames.getRating(),selectedRating);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getFeedback_submit(),map,getActivity());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();

        void dismissPopup();


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onFragmentInteractionListener!=null) {
            onFragmentInteractionListener.dismissPopup();
        }
    }
}