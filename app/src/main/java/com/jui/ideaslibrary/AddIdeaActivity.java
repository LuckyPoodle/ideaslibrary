package com.jui.ideaslibrary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jui.ideaslibrary.model.IdeaDatabase;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.view.IdeaListActivity;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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


    private String state;
    private LocationClass locationClass;
    public static LocationListener locationListener;
    public String[] myaddress = new String[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_idea);
        ButterKnife.bind(this);
        locationClass=new LocationClass(this);



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

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String add = locationClass.getAddress(location);
                locationAns.setText(add);
                locationClass.removelistener();

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
        };


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
        String formatteddate = formatter.format(timestamp);
        newidea.timestamp = formatteddate;

        //newidea.timestamp = timestamp.toString();
        newidea.problemStatement = problem;
        newidea.thoughts = thought;
        Log.d("LOCATION", "in createIdea =====================location is " + location);
        newidea.location=locationAns.getText().toString();

        if (imageUri != null) {
            newidea.imageUrl = imageUri.toString();
        } else {
            newidea.imageUrl=null;

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

            checkGalleryPermission();

    }

    private void getImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_CODE);
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
        //
        //
        checkLocationPermission();



    }
    public void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Access Location for user to record their location ")
                        .setMessage("This app requires access to your location so that you can easily record your exact location. Location access is only used for this purpose and is disabled right away after it is used")
                        .setPositiveButton("Ask me", (dialog, which) -> {
                            requestLocationPermission();
                        })
                        .setNegativeButton("No", ((dialog, which) -> {
                            Toast.makeText(AddIdeaActivity.this,"No external storage access",Toast.LENGTH_SHORT).show();
                        }))
                        .show();
            } else {
                requestLocationPermission();
            }
        }getLocation();
    }

    private void getLocation() {
        locationClass.getLocation();

    }


    public void checkGalleryPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Access local image file ")
                        .setMessage("This app requires access to your phone gallery so that you can upload image with your Idea post")
                        .setPositiveButton("Ask me", (dialog, which) -> {
                            requestgalleryPermission();
                        })
                        .setNegativeButton("No", ((dialog, which) -> {
                           Toast.makeText(AddIdeaActivity.this,"No external storage access",Toast.LENGTH_SHORT).show();
                        }))
                        .show();
            } else {
                requestgalleryPermission();
            }
        }getImage();
    }

    private void requestgalleryPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERYREQUESTCODE);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUESTCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUESTCODE:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSION","LOCATION ACCESS PERMISSION GRANTED");
                }else{
                    Log.d("PERMISSION","LOCATION ACCESS PERMISSION NOT GRANTED");
                }


            case GALLERYREQUESTCODE:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSION","GALLERY ACCESS PERMISSION GRANTED");
                }else{
                    Log.d("PERMISSION","GALLERY ACCESS PERMISSION NOT GRANTED");
                }
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