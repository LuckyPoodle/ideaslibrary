package com.jui.ideaslibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.view.IdeaListActivity;
import com.jui.ideaslibrary.viewmodel.IdeaViewModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.bulb)
    ImageView bulb;
    @BindView(R.id.listimage)
    ImageView listimage;

    @BindView(R.id.numberofideas)
    TextView numberOfIdeas;

    IdeaViewModel ideaViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);




        ideaViewModel = ViewModelProviders.of(this).get(IdeaViewModel.class);

        ideaViewModel.getCount();

        ideaViewModel.count.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer ideacount) {
                numberOfIdeas.setText(String.valueOf(ideacount));


            }
        });






    }

    @OnClick({R.id.bulb, R.id.listimage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bulb:
                startActivity(new Intent(MainActivity.this,AddIdeaActivity.class));
                break;
            case R.id.listimage:
                startActivity(new Intent(MainActivity.this, IdeaListActivity.class));
                break;
        }
    }
}