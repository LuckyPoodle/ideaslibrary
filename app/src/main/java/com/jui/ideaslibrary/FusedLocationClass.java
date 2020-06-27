/*
 * Copyright 2020 Quek Rui. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jui.ideaslibrary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.view.View;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jui.ideaslibrary.util.Constants;
import com.jui.ideaslibrary.util.FetchAddressIntentService;

import androidx.core.app.ActivityCompat;

import static com.jui.ideaslibrary.AddIdeaActivity.resultReceiver;

public class FusedLocationClass {

    Context context;


    public FusedLocationClass(Context context) {
        this.context = context;
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);




        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(context)
                                .removeLocationUpdates(this);

                        if (locationResult!=null && locationResult.getLocations().size()>0){
                            int latestLocationIndex=locationResult.getLocations().size()-1;
                            double latitude=locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude=locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Location location=new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);

                            fetchAddressFromLocation(location);
                        }




                    }
                }, Looper.getMainLooper());

    }



    private void fetchAddressFromLocation(Location location){
        Intent intent=new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        context.startService(intent);
    }

}
