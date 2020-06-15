package com.jui.ideaslibrary.view;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jui.ideaslibrary.R;
import com.jui.ideaslibrary.adapter.IdeasAdapter;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.viewmodel.IdeaViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IdeaListActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    IdeaViewModel ideaViewModel;
    @BindView(R.id.ideasList)
    RecyclerView ideasList;
    @BindView(R.id.listError)
    TextView listError;
    @BindView(R.id.loadingView)
    ProgressBar loadingView;

    private IdeasAdapter ideasAdapter = new IdeasAdapter(new ArrayList<>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_list);
        ButterKnife.bind(this);


        ideaViewModel = ViewModelProviders.of(this).get(IdeaViewModel.class);
        ideaViewModel.refresh();

        ideasList.setLayoutManager(new LinearLayoutManager(this));
        ideasList.setAdapter(ideasAdapter);
        ideasList.setOnClickListener(this);
        ideasList.setOnLongClickListener(this);


        observeViewModel();


    }

    private void observeViewModel() {

        ideaViewModel.ideas.observe(this, new Observer<List<IdeaEntry>>() {
            @Override
            public void onChanged(List<IdeaEntry> ideas) {
                if (ideas!=null && ideas instanceof List){
                    ideasList.setVisibility(View.VISIBLE);
                    //loadingView.setVisibility(View.GONE);
                    ideasAdapter.updateDogsList(ideas);
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
    public void onClick(View v) {
        Toast.makeText(this,"hi",Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            builder=new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert); //material designs
        }else{
            builder=new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete Entry")
                .setMessage("Sure???")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {






                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();



        return false;


    }
}