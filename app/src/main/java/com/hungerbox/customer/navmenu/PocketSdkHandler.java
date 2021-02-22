package com.hungerbox.customer.navmenu;

/**
 * Created by sandipanmitra on 12/6/17.
 */

public interface PocketSdkHandler {
    int POCKET_SDK_REQUEST_INIT = 90370;
    int POCKET_SDK_REQUEST_ONBOARD = 90371;
    int POCKET_SDK_REQUEST_DASHBOARD = 90372;

    void onPocketSdkClicked();
}
