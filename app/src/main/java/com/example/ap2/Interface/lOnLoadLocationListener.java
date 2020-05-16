package com.example.ap2.Interface;

import com.example.ap2.MyLatLng;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface lOnLoadLocationListener {
    void onLoadLocationSuccess(List<MyLatLng> latLngs);
    void onLoadLocationFail(String message);

}
