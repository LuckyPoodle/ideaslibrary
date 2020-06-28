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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.jui.ideaslibrary.model.IdeaDatabase;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.util.Constants;
import com.jui.ideaslibrary.view.IdeaListActivity;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddIdeaActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int ALL_PERMISSIONS_RESULT = 1111;
    private static final int LOCATION_REQUESTCODE = 500;
    private static final int GALLERYREQUESTCODE = 1000;
    @BindView(R.id.deleteEntryButton)
    Button deleteEntryButton;
    @BindView(R.id.locationAns)
    EditText locationAns;
    @BindView(R.id.imageplaceholder)
    TextView imageplaceholder;
    @BindView(R.id.imageicon)
    ImageView imageicon;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    private IdeaEntry newIdeaEntry;
    private Boolean isEdit = false;

    @BindView(R.id.sparkAns)
    EditText sparkAns;
    @BindView(R.id.ideaInput)
    EditText ideaInput;
    @BindView(R.id.postCameraButton)
    ImageView postCameraButton;
    @BindView(R.id.locationButton)
    ImageView locationButton;
    @BindView(R.id.saveEntryButton)
    Button saveEntryButton;

    private String timestamp;
    private String location;
    private int isFavourite;
    int ideaid;

    private Uri imageUri;

    private IdeaDatabase db;


    private String state;
//    private LocationClass locationClass;
//    public static LocationListener locationListener;
//
    public static ResultReceiver resultReceiver;
    private FusedLocationClass fusedLocationClass;

    public String[] myaddress = new String[1];

    private ConstraintLayout constraintlayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_idea);
        ButterKnife.bind(this);
        //locationClass = new LocationClass(this);
        if (!checkPlayServices()){
            Toast.makeText(AddIdeaActivity.this,"Please install Google Play",Toast.LENGTH_SHORT).show();
        }
        fusedLocationClass=new FusedLocationClass(this);

        constraintlayout = findViewById(R.id.addIdealayout);


        db = IdeaDatabase.getInstance(getApplicationContext());

        //for location
        resultReceiver=new AddressResultReceiver(new Handler());

        Intent intent = getIntent();
        if (intent.hasExtra("IDEA_ID")) {
            //means is editing
            Log.d("IDEAS", "++++++++++++++++++++++++++++++++++++++++edit+++++++++++++++++++++++++++");


            isEdit = true;
            deleteEntryButton.setVisibility(View.VISIBLE);
            setTitle("Edit Idea");
            isFavourite = intent.getIntExtra("isFavourite", 0);
            sparkAns.setText(intent.getStringExtra("problem"));
            ideaInput.setText(intent.getStringExtra("idea"));
            if (intent.getStringExtra("image") != null) {
                imageUri = Uri.parse(intent.getStringExtra("image"));
            }

            location = intent.getStringExtra("location");
            ideaid = intent.getIntExtra("IDEA_ID", 0);


        } else {
            Log.d("IDEAS", "++++++++++++++++++++++++++++++++++++++++add+++++++++++++++++++++++++++");
            setTitle("Add New Idea");

        }

//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                String add = locationClass.getAddress(location);
//                if (add == null || add.trim().length() == 0) {
//                    Snackbar.make(constraintlayout, "Error detecting location, please enter manually", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                } else {
//                    locationAns.setTextSize(10);
//                    locationClass.removelistener();
//                    Snackbar.make(constraintlayout, "Location fetched. Location detection switched off", Snackbar.LENGTH_SHORT)
//                            .setAction("Action", null).show();
//                }
//                progressBar.setVisibility(View.INVISIBLE);
//                locationAns.setText(add);
//
//
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addideaactivitymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(AddIdeaActivity.this, MainActivity.class));
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    private void createIdea(String problem, String thought) {

        IdeaEntry newidea = new IdeaEntry();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        String formatteddate = formatter.format(timestamp);
        newidea.timestamp = formatteddate;

        newidea.isFavourite = 0;

        //newidea.timestamp = timestamp.toString();
        newidea.problemStatement = problem;
        newidea.thoughts = thought;

        newidea.location = locationAns.getText().toString();

        if (imageUri != null) {
            newidea.imageUrl = imageUri.toString();

        } else {
            newidea.imageUrl = null;

        }


        if (isEdit == false) {


            new CreateIdeaAsyncTask().execute(newidea);
        } else {

            newidea.IdeaUid = ideaid;
            newidea.isFavourite = isFavourite;
            new UpdateIdeaAsyncTask().execute(newidea);
        }



    }

    @OnClick(R.id.postCameraButton)
    public void onPostCameraButtonClicked() {

        checkGalleryPermission();

    }

    private void getImage() {
/*       ACTION_GET_CONTENT temporary permission grant to be able to read and/or write the content. That grant will eventually lapse
Part of the Storage Access Framework includes the concept that a provider of content can offer permission grants that can last for an extended period ("long-term, persistent"). While there's nothing stopping an app from offering such persistent permissions with ACTION_GET_CONTENT on API Level 19+, they will be more common with ACTION_OPEN_DOCUMENT.*/

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                imageplaceholder.setVisibility(View.VISIBLE);
                imageplaceholder.setText(imageUri.toString().substring(0, 50) + R.string.dots);
                imageicon.setVisibility(View.GONE);


            }
        }


    }




    @OnClick(R.id.locationButton)
    public void onLocationButtonClicked() {
        checkLocationPermission();
        progressBar.setVisibility(View.VISIBLE);

    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Access Location for user to record their location ")
                        .setMessage("This app requires access to your location so that you can easily record your exact location. Location access is only used for this purpose and is disabled right away after it is used")
                        .setPositiveButton("Ask me", (dialog, which) -> {
                            requestLocationPermission();
                        })
                        .setNegativeButton("No", ((dialog, which) -> {
                            Toast.makeText(AddIdeaActivity.this, "No location access", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }))
                        .show();
            } else {
                requestLocationPermission();
            }
        }
        progressBar.setVisibility(View.GONE);
        getLocation();
    }

    private void getLocation() {
        //locationClass.getLocation();
        fusedLocationClass.getCurrentLocation();

    }


    public void checkGalleryPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Access local image file ")
                        .setMessage("This app requires access to your phone gallery so that you can upload image with your Idea post")
                        .setPositiveButton("Ask me", (dialog, which) -> {
                            requestgalleryPermission();
                        })
                        .setNegativeButton("No", ((dialog, which) -> {
                            Toast.makeText(AddIdeaActivity.this, "No external storage access", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }))
                        .show();
            } else {
                requestgalleryPermission();
            }
        }getImage();


    }

    private void requestgalleryPermission() {
        String[] gallerypermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, gallerypermission, GALLERYREQUESTCODE);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUESTCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUESTCODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "LOCATION ACCESS PERMISSION GRANTED");
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(constraintlayout, "Successful. Please try again", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(constraintlayout, "Location access permission not granted", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();                }
                break;

            case GALLERYREQUESTCODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(constraintlayout, "Successful. Please try again", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else {
                    Snackbar.make(constraintlayout, "Gallery access permission not granted", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    @OnClick(R.id.saveEntryButton)
    public void onSaveEntryButtonClicked() {
        createIdea(sparkAns.getText().toString(), ideaInput.getText().toString());
    }

    @OnClick(R.id.deleteEntryButton)
    public void onViewClicked() {
        deleteIdea(ideaid);
        startActivity(new Intent(AddIdeaActivity.this, IdeaListActivity.class));

    }

    private class AddressResultReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode,Bundle resultData){
            super.onReceiveResult(resultCode,resultData);
            progressBar.setVisibility(View.GONE);
            if (resultCode== Constants.SUCCESS_RESULT){
                locationAns.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            }else{
                Toast.makeText(AddIdeaActivity.this,"NO ADDRESS",Toast.LENGTH_LONG).show();
            }
        }


    }




    ////////////BACKGROUND TASKS FOR creating,updating, deleting

    private class CreateIdeaAsyncTask extends AsyncTask<IdeaEntry, Void, Void> {


        @Override
        protected Void doInBackground(IdeaEntry... ideas) {

            long id = db.ideaDAO().insertIdea(ideas[0]);


            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            startActivity(new Intent(AddIdeaActivity.this, IdeaListActivity.class));


        }
    }


    private class UpdateIdeaAsyncTask extends AsyncTask<IdeaEntry, Void, Void> {


        @Override
        protected Void doInBackground(IdeaEntry... ideas) {

            db.ideaDAO().updateIdea(ideas[0]);


            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            startActivity(new Intent(AddIdeaActivity.this, IdeaListActivity.class));


        }
    }

    public void deleteIdea(final int id) {
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.ideaDAO().deleteIdea(id);
            }
        });
    }
}