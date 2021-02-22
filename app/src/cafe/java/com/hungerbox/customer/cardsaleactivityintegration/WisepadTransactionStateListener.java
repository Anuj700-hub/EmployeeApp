package com.hungerbox.customer.cardsaleactivityintegration;

import com.mswipetech.wisepad.sdk.device.WisePadConnection;
import com.mswipetech.wisepad.sdk.device.WisePadTransactionState;

import java.util.Hashtable;


public interface WisepadTransactionStateListener {

	public void onReturnWisepadConnectionState(WisePadConnection aWisePadConnection);

	public void onReturnWisepadTransactionState(WisePadTransactionState aWisePadTransactionState, String aMessage, CreditSaleMessageEnum.WisePosStateInfoType aWisePosStateInfoType);

	public void onReturnWisepadDeviceInfo(Hashtable<String, String> deviceInfoData);

}
