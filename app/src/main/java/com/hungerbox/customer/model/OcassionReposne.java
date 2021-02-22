package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by peeyush on 3/8/16.
 */
public class OcassionReposne {


    /*
        "occasion_id": 1,
                "name": "lunch",
                "label": "Lunch",
                "active": "1",
                "display_start_time": "13:00:00",
                "start_time": "13:00:00",
                "end_time": "23:00:00",
                "type": "repeat",
    */

    public static String api_key = "list_occasion";

    @SerializedName("data")
    public ArrayList<Ocassion> ocassions;

    public ArrayList<Ocassion> getOcassions() {
        if (ocassions == null) {
            return new ArrayList<Ocassion>();
        } else {
            return ocassions;
        }
    }
}
