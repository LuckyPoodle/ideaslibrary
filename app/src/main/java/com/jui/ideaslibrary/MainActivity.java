package com.jui.ideaslibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jui.ideaslibrary.view.IdeaListActivity;
import com.jui.ideaslibrary.viewmodel.IdeaViewModel;

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
    @BindView(R.id.emoticon)
    ImageView emoticon;

    AdView mAdView;

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
                if (ideacount==0){
                    emoticon.setImageResource(R.drawable.emoji_sad_square_round);
                }


            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @OnClick({R.id.bulb, R.id.listimage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bulb:
                startActivity(new Intent(MainActivity.this, AddIdeaActivity.class));
                break;
            case R.id.listimage:
                startActivity(new Intent(MainActivity.this, IdeaListActivity.class));
                break;
        }
    }
}