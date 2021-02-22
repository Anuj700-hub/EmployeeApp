package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class OcassionReposneOffline {
    @SerializedName("data")
    public ArrayList<OcassionOffline> ocassions;

    public ArrayList<OcassionOffline> getOcassions() {
        if (ocassions == null) {
            return new ArrayList<OcassionOffline>();
        } else {
            return ocassions;
        }
    }
}
