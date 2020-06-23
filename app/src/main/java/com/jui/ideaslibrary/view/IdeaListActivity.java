package com.jui.ideaslibrary.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jui.ideaslibrary.AddIdeaActivity;
import com.jui.ideaslibrary.MainActivity;
import com.jui.ideaslibrary.R;
import com.jui.ideaslibrary.adapter.IdeasAdapter;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.viewmodel.IdeaViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IdeaListActivity extends AppCompatActivity {
    // implements IdeasAdapter.ListItemClickListener
    IdeaViewModel ideaViewModel;
    @BindView(R.id.ideasList)
    RecyclerView ideasList;
    @BindView(R.id.listError)
    TextView listError;
    @BindView(R.id.loadingView)
    ProgressBar loadingView;

    List<IdeaEntry> userIdealist;

    private IdeasAdapter ideasAdapter;

    private Menu menu;

    ActionMode actionMode;

    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_list);
        ButterKnife.bind(this);
        final Toolbar mToolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        CollapsingToolbarLayout ctl=findViewById(R.id.collapsing_toolbar_layout);
        ctl.setTitle("My Brilliant Ideas");

        //ctl.setCollapsedTitleTextColor(getResources().getColor(R.color.colorAccent));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(IdeaListActivity.this, AddIdeaActivity.class);
                startActivity(intent);

            }
        });



        AppBarLayout mAppBarLayout=(AppBarLayout)findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.action_add);

                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.action_add);
                }
            }
        });





        ideasAdapter =new IdeasAdapter(new ArrayList<>());
        //ideasAdapter.setItemClickListener(this);


        ideaViewModel = ViewModelProviders.of(this).get(IdeaViewModel.class);
        ideaViewModel.refresh();

        ideasList.setLayoutManager(new LinearLayoutManager(this));
        ideasList.setAdapter(ideasAdapter);

        observeViewModel();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        getMenuInflater().inflate(R.menu.listactivitymenu,menu);
        hideOption(R.id.action_add);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(IdeaListActivity.this, AddIdeaActivity.class));

                break;
            case R.id.action_home:
                startActivity(new Intent(IdeaListActivity.this, MainActivity.class));
                break;


        }





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
                    ideasAdapter.updateIdeasList(userIdealist);
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






//    @Override
//    public void onListItemClick(IdeaEntry ideaEntry) {
//        Log.d("IDEAS","CLICKED ON "+ideaEntry);
//        Intent intent=new Intent(IdeaListActivity.this, AddIdeaActivity.class);
//        intent.putExtra("IDEA_ID",ideaEntry.IdeaUid);
//
//        intent.putExtra("problem",ideaEntry.problemStatement);
//        intent.putExtra("idea",ideaEntry.thoughts);
//        intent.putExtra("location",ideaEntry.location);
//        intent.putExtra("image",ideaEntry.imageUrl);
//        startActivity(intent);
//    }
//
//    @Override
//    public void onLongItemClick(int clickedItemIndex) {
//        Log.d("IDEAS","LONGGGGGGGG CLICKED ON "+clickedItemIndex);
//    }
}