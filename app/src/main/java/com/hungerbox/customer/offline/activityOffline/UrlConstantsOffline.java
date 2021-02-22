package com.hungerbox.customer.offline.activityOffline;

public class UrlConstantsOffline {

    public static String ABOUT_US,LOCATION,OCCASION,VENDOR,VENDOR_MENU;

    public static void initializeUrl(String baseUrl){
        ABOUT_US = baseUrl + "api/wallet-list";
        LOCATION = baseUrl + "api/company/";
        OCCASION = baseUrl + "api/listOccasions";
        VENDOR = baseUrl + "api/listVendors";
        VENDOR_MENU = baseUrl + "api/listMenu";
    }
}
