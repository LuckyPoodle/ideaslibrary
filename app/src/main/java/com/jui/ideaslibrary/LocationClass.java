package com.jui.ideaslibrary;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationClass {
    Context context;

    LocationManager locationManager;



    public String[] myaddress = new String[1];
    public LocationClass(Context context){
        this.context=context;
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, AddIdeaActivity.locationListener);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public String getAddress(Location location){

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            myaddress[0] =address;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myaddress[0];



    }

    public void removelistener() {
        locationManager.removeUpdates(AddIdeaActivity.locationListener);
        locationManager=null;

    }










}
