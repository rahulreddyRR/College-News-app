package com.rahul.newsdroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rahul.newsdroid.Adapter.AdapterNews;
import com.rahul.newsdroid.Data.DataNewsPost;

import java.util.ArrayList;
import java.util.List;

public class ActivityHome extends AppCompatActivity {

    //For Activity
    Toolbar toolbar;
    NavigationView navigationView;
    FirebaseAuth mAuth;

    //Settings
    AlertDialog settingsDialog;
    SharedPreferences sharedpreferences;

    //For Loading ads
    AdRequest adRequest;
    InterstitialAd interAd;

    //For HomePage
    List<DataNewsPost> blog_list;
    FirebaseFirestore firebaseFirestore;

    AdapterNews blogRecyclerAdapter;

    RecyclerView recyclerView, thumbRecyclerView;
    LinearLayoutManager lnrMgr, firstLnrMgr;
    DocumentSnapshot lastVisible;
    SwipeRefreshLayout homeSwipeRefresh;
    Boolean isFirstPageFirstLoad = false;
/*
    FloatingActionButton newPost, newScore;*/

    LottieAnimationView loadingView;
    DrawerLayout drawer;
    Parcelable state;


    FloatingActionButton fabAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fabAdmin = findViewById(R.id.new_post_fab);
        recyclerView = findViewById(R.id.rv_home);
        thumbRecyclerView = findViewById(R.id.rv_thumb);
        loadingView = findViewById(R.id.loading_home);
        homeSwipeRefresh = findViewById(R.id.homeSwipeRefresh);

        if (toolbar.getVisibility() == View.VISIBLE) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
        //setupSearch();

        //Initialize AdRequest
        adRequest = new AdRequest.Builder().build();

        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES) {
            navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));

            Menu menu = navigationView.getMenu();
            MenuItem menuItem = menu.findItem(R.id.nav_night_mode);
            menuItem.setTitle("Day Mode");
        }

        if (ActivityConfig.ADMIN_APP) {
            fabAdmin.setVisibility(View.VISIBLE);
        }

/*        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        newScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityHome.this, "This clicked!", Toast.LENGTH_SHORT).show();
            }
        });*/

        navigationView.setItemIconTintList(null);
        changeAppScreens(R.id.nav_home);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //navigationView.setCheckedItem(item.getItemId());
                changeAppScreens(item.getItemId());
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.setFirestoreSettings(settings);

        if (mAuth.getCurrentUser() == null) {
            Intent accountIntent = new Intent(ActivityHome.this, ActivityAccount.class);
            startActivity(accountIntent);
        }

        blog_list = new ArrayList<>();

        blogRecyclerAdapter = new AdapterNews(blog_list);

        lnrMgr = new LinearLayoutManager(this);
        firstLnrMgr = new LinearLayoutManager(this);

        //Restoring RecyclerView (News list) position
        if (state != null) {
            lnrMgr.onRestoreInstanceState(state);
            firstLnrMgr.onRestoreInstanceState(state);
        }

        //Showing divider between each news post
        if (ActivityConfig.SHOW_POST_DIVIDER) {
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this,
                    lnrMgr.getOrientation());
            recyclerView.addItemDecoration(mDividerItemDecoration);
        }

        recyclerView.setLayoutManager(lnrMgr);
        recyclerView.setAdapter(blogRecyclerAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.getItemAnimator().setChangeDuration(0);

        //Loading home page news posts.
        loadHomePage();

        //Show first Interstitial ad in 20seconds
        if (ActivityConfig.SHOW_INTERSTITIAL_AD) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadInterstitialAd();
                }
            }, 20000);
        }

        homeSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHomePage();
            }
        });


        fabAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Intent postIntent = new Intent(ActivityHome.this, ActivityPostNews.class);
                startActivity(postIntent);
            }
        });
    }

    //Load news list on home page at start up.
    private void loadHomePage() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.ASCENDING);
        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    if (isFirstPageFirstLoad) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        blog_list.clear();
                    }
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();
                            DataNewsPost blogPost = doc.getDocument().toObject(DataNewsPost.class).withId(blogPostId);
                            if (isFirstPageFirstLoad) {
                                blog_list.add(blogPost);
                            } else {
                                blog_list.add(0, blogPost);
                            }

                            loadingView.setVisibility(View.GONE);
                            homeSwipeRefresh.setRefreshing(false);
                            homeSwipeRefresh.setEnabled(false);

                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });
    }

    public void loadInterstitialAd() {
        interAd = new InterstitialAd(this);
        interAd.setAdUnitId(ActivityConfig.AD_INTERSTITIAL);
        interAd.loadAd(adRequest);
        interAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //interAd.loadAd(adRequest);
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                interAd.loadAd(adRequest);
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                if (ActivityConfig.SHOW_CONTINUES_ADS) {
                    interAd.loadAd(adRequest);
                }
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                if (ActivityConfig.SHOW_CONTINUES_ADS) {
                    interAd.loadAd(adRequest);
                }
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                if (interAd.isLoaded()) {
                    interAd.show();
                } else {
                    interAd.loadAd(adRequest);
                }
                super.onAdLoaded();
            }
        });
    }


    //This manage back button work.
    boolean doubleBack;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            if (doubleBack) {
                super.onBackPressed();
                return;
            }
            this.doubleBack = true;
            Snackbar.make(homeSwipeRefresh,"Please click BACK again to exit",Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).setAction("EXIT ME", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBack = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;

        } else if (id == R.id.action_account) {
            sendToAccount();
        }

        return super.onOptionsItemSelected(item);
    }

    //Moving to new action or activity on navigation item click
    private void changeAppScreens(int id) {
        switch (id) {
            case R.id.nav_home:
                toolbar.setTitle("Sports Hunt!");
                break;

            case R.id.nav_categories:
                Intent catIntent = new Intent(ActivityHome.this, ActivityCategory.class);
                startActivity(catIntent);
                break;

            case R.id.nav_settings:
                openAppSettings();
                break;

            case R.id.nav_facebook:
                openFbUrl(ActivityConfig.SOCIAL_FACEBOOK);
                break;

            case R.id.nav_night_mode:
                if (AppCompatDelegate.getDefaultNightMode()
                        == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    //navigationView.setItemIconTintList(getResources().getColorStateList(R.color.colorAccent));
                } else {
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    navigationView.setItemIconTintList(null);
                }
                recreate();

                break;

            case R.id.nav_rate:
                rateTheApp();
                break;

            case R.id.nav_share:
                shareTheApp();
                break;

            case R.id.nav_about:
                Intent aboutIntent = new Intent(ActivityHome.this, ActivityAbout.class);
                startActivity(aboutIntent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
    }


    //Sharing the app
    private void shareTheApp() {

        final String appPackageName = getPackageName();
        String appName = getString(R.string.app_name);
        String appCategory = ActivityConfig.APP_CATEGORY;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String postData = "Get " + appName + "for latest " + appCategory + "+News: https://play.google.com/store/apps/details?id=" + appPackageName;

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Now!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, postData);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share Post Via"));
    }

    private void rateTheApp() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void openAppSettings() {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.dialog_settings, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        Switch switchTheme = alertLayout.findViewById(R.id.SwitchTheme);
        Switch switchNotification = alertLayout.findViewById(R.id.SwitchPush);
        final TextView textFontSize = alertLayout.findViewById(R.id.text_font_size);
        final LottieAnimationView settings_close = alertLayout.findViewById(R.id.settings_close);

        sharedpreferences = getSharedPreferences("SHARE_SETTINGS", MODE_PRIVATE);
        boolean theme = sharedpreferences.getBoolean("stateTheme", false);
        String currentFontSize = sharedpreferences.getString("fontSize", "Normal");
        textFontSize.setText(currentFontSize);
        switchTheme.setChecked(theme);

        SharedPreferences pushShare = getSharedPreferences("SHARE_SETTINGS", MODE_PRIVATE);
        boolean push = pushShare.getBoolean("statePush", true);
/*        if (push){
            FirebaseMessaging.getInstance().subscribeToTopic("post_push");
        }else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("post_push");
        }*/

        switchNotification.setChecked(push);

        //alert.setTitle("Info");
        alert.setView(alertLayout);

        //Elements
        textFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ActivityHome.this, view);
                popupMenu.inflate(R.menu.activity_font);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int item_id = menuItem.getItemId();
                        String fontSizeString = "Normal";
                        if (item_id == R.id.dialog_font_small) {
                            fontSizeString = "Small";
                        } else if (item_id == R.id.dialog_font_large) {
                            fontSizeString = "Large";
                        }
                        sharedpreferences = getSharedPreferences("SHARE_SETTINGS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("fontSize", fontSizeString);
                        editor.apply();
                        textFontSize.setText(fontSizeString);
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //SaveSwitchPush(isChecked);
            }
        });
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveSwitchTheme(isChecked);
            }
        });

        settings_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });

        alert.setCancelable(true);
/*        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsDialog.dismiss();
            }
        });*/
        settingsDialog = alert.create();
        settingsDialog.show();
    }

    public void reCreateTheActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    private void SaveSwitchPush(boolean state) {
        sharedpreferences = getSharedPreferences("SHARE_SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("statePush", state);
        editor.apply();
        if (state) {
            FirebaseMessaging.getInstance().subscribeToTopic("post_push");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("post_push");
        }
    }

    private void SaveSwitchTheme(boolean state) {
        sharedpreferences = getSharedPreferences("SHARE_SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("stateTheme", state);
        editor.apply();
    }

    private void sendToAccount() {
        Intent loginIntent = new Intent(ActivityHome.this, ActivityAccount.class);
        startActivity(loginIntent);
        finish();
    }

    protected void openFbUrl(String username) {
       /* Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = openFacebookUrl(username);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    public String openFacebookUrl(String username) {
        String FACEBOOK_URL = "https://www.facebook.com/" + username;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + username;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }*/
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/profile.php?id=100009588109234")));

    }
}