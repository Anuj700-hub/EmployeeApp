package com.hungerbox.customer.model;

import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

public class EnterExitConfig {

    int enforcedCapacity;

    public EnterExitConfig(int enforcedCapacity){
        this.enforcedCapacity = enforcedCapacity;
    }

    public boolean isEntryCafeActive(){

        if(enforcedCapacity > 0)
            return true;
        else
            return false;
    }

    public boolean isExitCafeActive(){

        if(enforcedCapacity > 1)
            return true;
        else
            return false;
    }

    public boolean isEntryCafeShowingQr(){

        if(enforcedCapacity == 1 || enforcedCapacity == 3 || enforcedCapacity == 4)
            return true;
        else
            return false;
    }

    public boolean isExitCafeShowingQr(){

        if(enforcedCapacity == 4)
            return true;
        else
            return false;
    }
}
