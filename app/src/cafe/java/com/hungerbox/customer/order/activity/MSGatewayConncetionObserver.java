package com.hungerbox.customer.order.activity;

import com.hungerbox.customer.util.AppUtils;
import com.mswipetech.sdk.network.MSGatewayConnectionListener;

/**
 * Created by manas on 20/11/18.
 */

public class MSGatewayConncetionObserver implements MSGatewayConnectionListener {

    @Override
    public void Connected(String msg) {

        AppUtils.HbLog("mswipe_connected", " msg " + msg);

//            imgHostConnectionStatus.setAnimation(null);
//            imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_active);
    }

    @Override
    public void Connecting(String msg) {


			/*if(ApplicationData.IS_PERFORMENCE_TEST_ON){
				Logs.p(ApplicationData.packName, "Wedrocket connection started", true, true);
			}*/

        AppUtils.HbLog("mswipe_connecting"," msg " + msg);

//            imgHostConnectionStatus.startAnimation(alphaAnim);
//            imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_inactive);
    }

    @Override
    public void disConnect(String msg) {


			/*if(ApplicationData.IS_PERFORMENCE_TEST_ON){
				Logs.p(ApplicationData.packName, "Wedrocket disconnected", true, true);
			}*/

        AppUtils.HbLog("mswipe_disconnect", " msg " + msg);

//            imgHostConnectionStatus.setAnimation(null);
//            imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_inactive);
    }
}

