package com.hungerbox.customer.cardsaleactivityintegration;

public  class CreditSaleEnumClass {

	public enum CARDTYPE
	{
		VISA,
		MASTER,
		EXPRESS,
		DINERS,
		DISCOVER,
		JCB,
		MAESTRO,
		RUPAY,
		UNKNOWN
	};

	public enum CardSaleScreens
	{
		CardSaleScreens_NONE,
		CardSaleScreens_AMOUNT,
		CardSaleScreens_EMI_AMOUNT,
		CardSaleScreens_EMI_OPTION_SELECTION,
		CardSaleScreens_EMI_TERM_CONDITION,
		CardSaleScreens_SALECASH_AMOUNT,
		CardSaleScreens_CARD_SWIPE_OR_INSERT,
		CardSaleScreens_AMEX_PINENTRY,
		CardSaleScreens_CARDDETAILS
	}
}
