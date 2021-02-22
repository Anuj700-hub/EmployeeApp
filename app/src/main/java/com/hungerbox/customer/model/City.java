package com.hungerbox.customer.model;

import androidx.annotation.NonNull;

/**
 * Created by sandipanmitra on 12/12/17.
 */

public class City implements Comparable<City> {

    String cityName;
    long cityId;

    public String getCityName() {
        if (cityName == null)
            cityName = "";
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    @Override
    public int compareTo(@NonNull City city) {
        return getCityName().compareTo(city.getCityName());
//        return (int) (cityId - city.cityId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof City) {
            City otherCity = (City) obj;
            return otherCity.cityId == cityId;
        }
        return false;
    }

    @Override
    public String toString() {
        return cityName;
    }
}
