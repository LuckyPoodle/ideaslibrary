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
        if (ContextCompat.checkSelfPermission(AddIdeaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

            if (ContextCompat.checkSelfPermission(AddIdeaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getImage();
            }


        } else {
            getImage();
        }


    }

    private void getImage() {
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
        //
        //
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //REQUEST PERMISSION

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        }


        locationClass.getLocation();




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