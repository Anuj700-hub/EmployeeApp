package com.hungerbox.customer.navmenu.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.UserSettings;
import com.hungerbox.customer.model.UserSettingsResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class SettingsActivity extends ParentActivity {

    CheckedTextView ctvSms, ctvAppNotification, ctvChatHead;
    private boolean smsSetting, appNotificationSetting;
    private boolean chatHeadSetting;
    private ImageView ivBack;
    private RelativeLayout pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        pb = findViewById(R.id.pb);
        ivBack = findViewById(R.id.iv_back);
        ctvSms = findViewById(R.id.ctv_sms_setting);
        ctvAppNotification = findViewById(R.id.ctv_notification_setting);
        ctvChatHead = findViewById(R.id.ctv_chat_head_setting);
        setClickListeners();
        getUserSetting();
        LogoutTask.updateTime();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void setClickListeners() {

        ctvSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvSms.isChecked()) {
                    ctvSms.setChecked(false);
                } else {
                    ctvSms.setChecked(true);
                }
                try {
                    JSONStringer jObject = new JSONStringer()
                            .object()
                            .key(ApplicationConstants.SMS_SETTING).value(ctvSms.isChecked())
                            .endObject();
                    JSONObject smsNotificationJson = new JSONObject(jObject.toString());
//                    chatHeadJson.put(ApplicationConstants.CHAT_HEAD_SETTING, ctvChatHead.isChecked());
                    updateUserSetting(smsNotificationJson, ApplicationConstants.SMS_SETTING);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ctvAppNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvAppNotification.isChecked()) {
                    ctvAppNotification.setChecked(false);
                } else {
                    ctvAppNotification.setChecked(true);
                }

                try {
                    JSONStringer jObject = new JSONStringer()
                            .object()
                            .key(ApplicationConstants.APP_NOTIFICATION_SETTING).value(ctvAppNotification.isChecked())
                            .endObject();
                    JSONObject appNotificationJson = new JSONObject(jObject.toString());
//                    chatHeadJson.put(ApplicationConstants.CHAT_HEAD_SETTING, ctvChatHead.isChecked());
                    updateUserSetting(appNotificationJson, ApplicationConstants.APP_NOTIFICATION_SETTING);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ctvChatHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvChatHead.isChecked()) {
                    ctvChatHead.setChecked(false);
                } else {
                    ctvChatHead.setChecked(true);
                }
                try {
                    JSONStringer jObject = new JSONStringer()
                            .object()
                            .key(ApplicationConstants.CHAT_HEAD_SETTING).value(ctvChatHead.isChecked())
                            .endObject();
                    JSONObject chatHeadJson = new JSONObject(jObject.toString());
//                    chatHeadJson.put(ApplicationConstants.CHAT_HEAD_SETTING, ctvChatHead.isChecked());
                    updateUserSetting(chatHeadJson, ApplicationConstants.CHAT_HEAD_SETTING);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUserSetting(final JSONObject userSettings, final String key) {
        SimpleHttpAgent<UserSettings> settingsHttpAgent = new SimpleHttpAgent<UserSettings>(SettingsActivity.this,
                UrlConstant.SET_USER_SETTINGS, new ResponseListener<UserSettings>() {
            @Override
            public void response(UserSettings responseObject) {
                try {
                    SharedPrefUtil.putBoolean(key, userSettings.getBoolean(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateUserSettingUI();
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                updateUserSettingUI();
            }
        }, UserSettings.class);
        settingsHttpAgent.post(userSettings.toString());
    }

    private void updateUserSettingUI() {

        smsSetting = SharedPrefUtil.getBoolean(ApplicationConstants.SMS_SETTING, true);
        appNotificationSetting = SharedPrefUtil.getBoolean(ApplicationConstants.APP_NOTIFICATION_SETTING, true);
        chatHeadSetting = SharedPrefUtil.getBoolean(ApplicationConstants.CHAT_HEAD_SETTING, true);

        ctvAppNotification.setChecked(appNotificationSetting);
        ctvSms.setChecked(smsSetting);
        ctvChatHead.setChecked(chatHeadSetting);

    }

    private void getUserSetting() {

        pb.setVisibility(View.VISIBLE);
        SimpleHttpAgent<UserSettingsResponse> settingsHttpAgent = new SimpleHttpAgent<UserSettingsResponse>(SettingsActivity.this,
                UrlConstant.USER_SETTINGS, new ResponseListener<UserSettingsResponse>() {
            @Override
            public void response(UserSettingsResponse responseObject) {
                SharedPrefUtil.putBoolean(ApplicationConstants.EMAIL_SETTING, responseObject.userSettings.isEmail());
                SharedPrefUtil.putBoolean(ApplicationConstants.SMS_SETTING, responseObject.userSettings.isSms());
                SharedPrefUtil.putBoolean(ApplicationConstants.GENERAL_NOTIFICATION, responseObject.userSettings.isGeneralNotification());
                SharedPrefUtil.putBoolean(ApplicationConstants.APP_NOTIFICATION_SETTING, responseObject.userSettings.isAppNotification());
                SharedPrefUtil.putBoolean(ApplicationConstants.CHAT_HEAD_SETTING, responseObject.userSettings.isChatHeadEnabled());

                updateUserSettingUI();

                pb.setVisibility(View.GONE);
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                updateUserSettingUI();
                pb.setVisibility(View.GONE);
            }
        }, UserSettingsResponse.class);
        settingsHttpAgent.get();
    }
}
