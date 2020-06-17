package com.jui.ideaslibrary;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.jui.ideaslibrary.model.IdeaDatabase;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.view.IdeaListActivity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddIdeaActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int GALLERY_CODE = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000 ;
    private static final int ALL_PERMISSIONS_RESULT = 1111;
    @BindView(R.id.deleteEntryButton)
    Button deleteEntryButton;


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
    int ideaid;

    private Uri imageUri;

    private IdeaDatabase db;

    private LocationClass locationClass;
    private String state;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_idea);
        ButterKnife.bind(this);

        locationClass=new LocationClass(this);
        location=" ";

        locationClass.client=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(AddIdeaActivity.this)
                .build();

        locationClass.fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(AddIdeaActivity.this);
        locationClass.permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        locationClass.permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        locationClass.permissionsToRequest=permissionsToRequestMtd(locationClass.permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (locationClass.permissionsToRequest.size() > 0) {
                requestPermissions(locationClass.permissionsToRequest.toArray(
                        new String[locationClass.permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT
                );
            }
        }



        db = IdeaDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent.hasExtra("IDEA_ID")) {
            //means is editing
            Log.d("IDEAS", "++++++++++++++++++++++++++++++++++++++++edit+++++++++++++++++++++++++++");


            isEdit = true;
            deleteEntryButton.setVisibility(View.VISIBLE);
            setTitle("Edit Idea");
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
                startActivity(new Intent(AddIdeaActivity.this, IdeaListActivity.class));

                break;


        }


        return super.onOptionsItemSelected(item);
    }

    private void createIdea(String problem, String thought) {

        IdeaEntry newidea = new IdeaEntry();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-YYYY HH:mm");
        String formatteddate=formatter.format(timestamp);
        newidea.timestamp=formatteddate;

        //newidea.timestamp = timestamp.toString();
        newidea.problemStatement = problem;
        newidea.thoughts = thought;
        Log.d("LOCATION","in createIdea =====================location is "+location);
        newidea.location = state;
        if (imageUri != null) {
            newidea.imageUrl = imageUri.toString();
        } else {

        }


        if (isEdit == false) {
            Log.d("IDEAS", "+++++++++++++++++++++++++++isEdit == false+++++++++++++++++++++++++");

            new CreateIdeaAsyncTask().execute(newidea);
        } else {
            Log.d("IDEAS", "+++++++++++++++++++++++++++isEdit == true+++++++++++++++++++++++++");
            newidea.IdeaUid = ideaid;
            new UpdateIdeaAsyncTask().execute(newidea);
        }


    }

    @OnClick(R.id.postCameraButton)
    public void onPostCameraButtonClicked() {
        if (ContextCompat.checkSelfPermission(AddIdeaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

            if (ContextCompat.checkSelfPermission(AddIdeaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED){
                getImage();
            }


        }else{
            getImage();
        }



    }

    private void getImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();


            }
        }


    }

    @OnClick(R.id.locationButton)
    public void onLocationButtonClicked() {
        //Todo: save location to string

       state=locationClass.startLocationUpdates();


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




    ////////////Location related

    private ArrayList<String> permissionsToRequestMtd(ArrayList<String> wantedpermissions) {
        ArrayList<String> results=new ArrayList<>();
        for (String permission:wantedpermissions){
            //check if already permitted
            if (!hasPermission(permission)){
                results.add(permission);
            }
        }
        return results;

    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            return checkSelfPermission(permission)==PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : locationClass.permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        locationClass.permissionsRejected.add(perm);


                    }
                }
                if (locationClass.permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(locationClass.permissionsRejected.get(0))) {
                            new AlertDialog.Builder(AddIdeaActivity.this)
                                    .setMessage("These permissions are mandatory to get location")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(locationClass.permissionsRejected.toArray(
                                                        new String[locationClass.permissionsRejected.size()]),
                                                        ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null)
                                    .create()
                                    .show();


                        }
                    }
                }else {
                    if (locationClass.client != null) {
                        locationClass.client.connect();
                    }
                }
                break;

        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        if (locationClass.client!=null){
            locationClass.client.connect();

        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        locationClass.client.disconnect();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (locationClass.client != null && locationClass.client.isConnected()) {
            LocationServices.getFusedLocationProviderClient(this)
                    .removeLocationUpdates(new LocationCallback() {

                    });
            locationClass.client.disconnect();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();



        if (!checkPlayServices()){
            Toast.makeText(AddIdeaActivity.this,"Please install Google Play",Toast.LENGTH_SHORT).show();
        }

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    public void deleteIdea(final int id){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.ideaDAO().deleteIdea(id);
            }
        });
    }
}