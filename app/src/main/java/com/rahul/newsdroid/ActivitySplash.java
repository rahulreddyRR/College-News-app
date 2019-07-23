package com.rahul.newsdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ActivitySplash extends AppCompatActivity {

    //Important
    private SharedPreferences sharedpreferences;
    private SharedPreferences sharedIntro;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    private String myTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedIntro = getSharedPreferences("INTRO_STATE", MODE_PRIVATE);
        boolean firstRun = sharedIntro.getBoolean("First_Run", true);

        if (firstRun) {
            Intent splashIntent = new Intent(ActivitySplash.this, ActivityIntro.class);
            startActivity(splashIntent);
            finish();
        } else {
            if (!isConnected(10000)) {
                Intent noInternet = new Intent(ActivitySplash.this, ActivityInternet.class);
                startActivity(noInternet);
                finish();
            } else {
                if (getIntent().getExtras() != null) {
                    myTitle = getIntent().getExtras().getString("Notification_Title");
                }
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                //CheckAutoTheming
                sharedpreferences = getSharedPreferences("SHARE_SETTINGS", MODE_PRIVATE);
                boolean state = sharedpreferences.getBoolean("stateTheme", false);
                if (state) {
                    CheckTheme();
                }

                //Checking Account Preference
                mAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();

                if (mAuth.getCurrentUser() != null) {

                    if (myTitle != null && myTitle.equals("yes")) {
                        Intent hotNewsIntent = new Intent(ActivitySplash.this, ActivityHotNews.class);
                        startActivity(hotNewsIntent);
                        finish();
                    } else {
                        Intent homeIntent = new Intent(ActivitySplash.this, ActivityHome.class);
                        startActivity(homeIntent);
                        finish();
                    }

                } else {
                    checkUserCondition();
                }
            }
        }
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }


    //Load random images in Splash screen
/*    private void loadBackgroundImages() {
        int[] yourListOfImages = {R.drawable.splash_bg, R.drawable.splash_bg, R.drawable.splash_bg, R.drawable.splash_bg, R.drawable.splash_bg};
        Random random = new Random(System.currentTimeMillis());
        int posOfImage = random.nextInt(yourListOfImages.length - 1);
        ImageView imageView = findViewById(R.id.splashIcon);
        final int myImageDrawable = yourListOfImages[posOfImage];
        imageView.setBackgroundResource(myImageDrawable);
        Bitmap bimapOfThatImage = BitmapFactory.decodeResource(getResources(), myImageDrawable);

        Palette.from(bimapOfThatImage).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {

                int defaultValue = 0x000000;
                int vibrant = palette.getDominantColor(defaultValue);
                //getWindow().setStatusBarColor(vibrant);

            }
        });
    }*/

    private void checkUserCondition() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(ActivitySplash.this, ActivityLogin.class);
            startActivity(loginIntent);
            finish();
        } else {
            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            Intent setupIntent = new Intent(ActivitySplash.this, ActivityAccount.class);
                            startActivity(setupIntent);
                            finish();
                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                    }
                }
            });
        }
    }

    private void CheckTheme() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 6) {
            SetNightTheme();
        } else if (timeOfDay >= 6 && timeOfDay < 19) {
            SetDayTheme();
        } else if (timeOfDay >= 19 && timeOfDay < 24) {
            SetNightTheme();
        }
    }

    private void SetDayTheme() {
        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            //navView.setItemIconTintList(getResources().getColorStateList(R.color.primaryTextColor));
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            //navView.setItemIconTintList(null);
        }
    }

    private void SetNightTheme() {
        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            //navView.setItemIconTintList(getResources().getColorStateList(R.color.primaryTextColor));
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            //navView.setItemIconTintList(null);
        }
    }

    private boolean isConnected(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedIntro.getBoolean("First_Run", true)) {
            showAppIntroPages();
            sharedIntro.edit().putBoolean("First_Run", false).apply();
        }
    }

    private void showAppIntroPages() {
        Intent introIntent = new Intent(ActivitySplash.this, ActivityIntro.class);
        startActivity(introIntent);
        finish();
    }

}