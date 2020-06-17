package com.jui.ideaslibrary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationClass implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //FUSEDLOCATIONRELATED
    public static GoogleApiClient client;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public ArrayList<String> permissionsToRequest;
    public ArrayList<String> permissions=new ArrayList<>();
    public ArrayList<String> permissionsRejected=new ArrayList<>();



    public static final long UPDATE_INTERVAL=5000;

    // variable to hold context
    private Context context;

    private LocationRequest locationRequest;

//save the context recievied via constructor in a local variable

    public LocationClass(Context context){
        this.context=context;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //invoke fusedlocationproviderclient and listener
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener( new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //get last known location
                        if (location!=null){
                            Log.d("LOCATION","--------------------"+location.getLatitude()+"----------------");
                        }


                    }
                });

        startLocationUpdates();

    }

    public String startLocationUpdates() {
        String[] myaddress = new String[1];

        //create a location request
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION","NO PERMISSION+++++++++++++++++++++++++++++++++");
        }

        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Log.d("LOCATION","CALLED FUSED LCCATIONNNNNN");
                        if (locationResult != null) {
                            Location location = locationResult.getLastLocation();
                            Log.d("LOCATION","CALLED FUSED LCCATIONNNNNN"+ location.getLatitude()+ location.getLongitude()+location.getTime());
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



                        }

                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                    }
                }, null);
            Log.d("LOCATION","------^^^^^^^^^^^ In startLocationUpdates ---"+myaddress[0]);
            return myaddress[0];

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
