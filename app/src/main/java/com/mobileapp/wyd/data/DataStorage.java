package com.mobileapp.wyd.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class DataStorage
{
    private static DataStorage single_instance = null;
    private SharedPreferences pref;
    private static final String PREF_NAME = "wydapp";
    private static final String LOCATION="userLocation";
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;


    public static DataStorage getInstance(Context context)
    {
        if (single_instance == null) single_instance = new DataStorage(context);
        return single_instance;
    }

    public void setUserLocation(Location location)
    {
        String loc=location.getLatitude()+","+location.getLongitude();
        editor.putString(LOCATION,loc);
        editor.apply();
    }

    public String getLocation()
    {
        return pref.getString(LOCATION,"");
    }

    private DataStorage(Context context)
    {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
}
