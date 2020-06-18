package com.jui.ideaslibrary.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jui.ideaslibrary.AddIdeaActivity;
import com.jui.ideaslibrary.MainActivity;
import com.jui.ideaslibrary.R;
import com.jui.ideaslibrary.adapter.IdeasAdapter;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.viewmodel.IdeaViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IdeaListActivity extends AppCompatActivity implements IdeasAdapter.ListItemClickListener {
    IdeaViewModel ideaViewModel;
    @BindView(R.id.ideasList)
    RecyclerView ideasList;
    @BindView(R.id.listError)
    TextView listError;
    @BindView(R.id.loadingView)
    ProgressBar loadingView;

    List<IdeaEntry> userIdealist;

    private IdeasAdapter ideasAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_list);
        ButterKnife.bind(this);


        ideasAdapter =new IdeasAdapter(new ArrayList<>());
        ideasAdapter.setItemClickListener(this);


        ideaViewModel = ViewModelProviders.of(this).get(IdeaViewModel.class);
        ideaViewModel.refresh();

        ideasList.setLayoutManager(new LinearLayoutManager(this));
        ideasList.setAdapter(ideasAdapter);

        observeViewModel();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listactivitymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {





        return super.onOptionsItemSelected(item);
    }

    private void observeViewModel() {

        ideaViewModel.ideasListVM.observe(this, new Observer<List<IdeaEntry>>() {
            @Override
            public void onChanged(List<IdeaEntry> ideas) {
                if (ideas!=null && ideas instanceof List){
                    ideasList.setVisibility(View.VISIBLE);
                    //loadingView.setVisibility(View.GONE);
                    userIdealist=ideas;
                    ideasAdapter.updateDogsList(userIdealist);
                }
            }
        });

        ideaViewModel.loadingerror.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isError) {
                if (isError!=null && isError instanceof Boolean){
                    listError.setVisibility(isError? View.VISIBLE : View.GONE);
                }
            }
        });

        ideaViewModel.loading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading!=null && isLoading instanceof Boolean){
                    loadingView.setVisibility(isLoading? View.VISIBLE : View.GONE);
                    if (isLoading){
                        listError.setVisibility(View.GONE);
                        ideasList.setVisibility(View.GONE);
                    }

                }
            }
        });


    }


    @Override
    public void onListItemClick(IdeaEntry ideaEntry) {
        Log.d("IDEAS","CLICKED ON "+ideaEntry);
        Intent intent=new Intent(IdeaListActivity.this, AddIdeaActivity.class);
        intent.putExtra("IDEA_ID",ideaEntry.IdeaUid);

        intent.putExtra("problem",ideaEntry.problemStatement);
        intent.putExtra("idea",ideaEntry.thoughts);
        intent.putExtra("location",ideaEntry.location);
        intent.putExtra("image",ideaEntry.imageUrl);




        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int clickedItemIndex) {
        Log.d("IDEAS","LONGGGGGGGG CLICKED ON "+clickedItemIndex);
    }
}