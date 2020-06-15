package com.jui.ideaslibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jui.ideaslibrary.adapter.IdeasAdapter;
import com.jui.ideaslibrary.model.IdeaDatabase;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.view.IdeaListActivity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddIdeaActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;

    private IdeaEntry newIdeaEntry;

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

    String timestamp;

    private Uri imageUri;

    private IdeaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_idea);
        ButterKnife.bind(this);
        db = IdeaDatabase.getInstance(getApplicationContext());




    }

    private void createIdea(String problem, String thought) {

        IdeaEntry newidea=new IdeaEntry();
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        newidea.timestamp=timestamp.toString();
        newidea.problemStatement=problem;
        newidea.thoughts=thought;
        newidea.location="TEMPORARY";
        if (imageUri!=null){
            newidea.imageUrl=imageUri.toString();
        }else{

        }





        new CreateIdeaAsyncTask().execute(newidea);

    }

    @OnClick(R.id.postCameraButton)
    public void onPostCameraButtonClicked() {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==GALLERY_CODE && resultCode==RESULT_OK){
            if (data!=null){
                imageUri =data.getData();


            }
        }




    }

    @OnClick(R.id.locationButton)
    public void onLocationButtonClicked() {
        //Todo: save location to string
    }

    @OnClick(R.id.saveEntryButton)
    public void onSaveEntryButtonClicked() {
        createIdea(sparkAns.getText().toString(),ideaInput.getText().toString());
    }


    private class CreateIdeaAsyncTask extends AsyncTask<IdeaEntry,Void,Void> {



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
}