package com.hungerbox.customer.offline.modelOffline;

import androidx.annotation.NonNull;

public class CityOffline implements Comparable<CityOffline> {

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
    public int compareTo(@NonNull CityOffline city) {
        return getCityName().compareTo(city.getCityName());
//        return (int) (cityId - city.cityId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CityOffline) {
            CityOffline otherCity = (CityOffline) obj;
            return otherCity.cityId == cityId;
        }
        return false;
    }

    @Override
    public String toString() {
        return cityName;
    }
}
