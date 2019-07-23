package com.rahul.newsdroid;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.rahul.newsdroid.Adapter.AdapterCategory;

public class ActivityCategory extends AppCompatActivity {

    LinearLayout catLinear;
    private AdapterCategory adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        catLinear = findViewById(R.id.catLinear);

        RecyclerView recycler = findViewById(R.id.categoriesRecyclerView);
        adapter = new AdapterCategory(this);
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        //For loading AdMob ads
        if (ActivityConfig.SHOW_BANNER_AD){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadBannnerAd();
                }
            },3000);
        }
    }

    private void loadBannnerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        final LinearLayout adContainer = catLinear;
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(ActivityConfig.AD_BANNER_TOP);
        adView.loadAd(adRequest);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        adContainer.addView(adView, params);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                catLinear.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    boolean isExpanded = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.onBackPressed();
                break;

            case R.id.action_expand:
                if (isExpanded) {
                    adapter.collapseAll();
                    isExpanded = false;
                } else {
                    adapter.expandAll();
                    isExpanded = true;
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }
}