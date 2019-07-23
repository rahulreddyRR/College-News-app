package com.rahul.newsdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityIntro extends OnboarderActivity {

    SharedPreferences sharedIntro = null;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    boolean isSuccessful;

    String guest_user_image = "https://livefreycom-dehki12yv.stackpathdns.com/wp-content/uploads/2017/09/round-account-button-with-user-inside.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedIntro = getSharedPreferences("INTRO_STATE", MODE_PRIVATE);

        //Checking Account Preference
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signInAnonymously();

        OnboarderPage intro1 = new OnboarderPage(getString(R.string.app_name), "Latest College News & Update App!", R.drawable.intro_app_icon);
        OnboarderPage intro2 = new OnboarderPage("Get Instant College Updates", "See new posts instantly as we bring it, get instantly notified too!", R.drawable.intro_two);
        OnboarderPage intro3 = new OnboarderPage("Choose from wide range", "Filled with various categories and sections!", R.drawable.intro_three);
        OnboarderPage intro5 = new OnboarderPage("Night Mode For Readers!", "Automatic Night Mode Switching by Time. Read news on bed with zero eye pain, ", R.drawable.intro_five);
        OnboarderPage intro6 = new OnboarderPage("Settings Deep Users", "Change Font Size and customize Night Mode and other features as per you!", R.drawable.intro_six);
        OnboarderPage intro8 = new OnboarderPage("We Welcome You..", "Give your try to our brand new College App. Stay tuned for more updates!", R.drawable.intro_eight);

        intro1.setBackgroundColor(R.color.colorTeal900);
        intro2.setBackgroundColor(R.color.colorPink600);
        intro3.setBackgroundColor(R.color.colorRed600);
        intro5.setBackgroundColor(R.color.colorPurple700);
        intro6.setBackgroundColor(R.color.colorOrange600);
        intro8.setBackgroundColor(R.color.colorGrey700);

        List<OnboarderPage> pages = new ArrayList<>();

        pages.add(intro1);
        pages.add(intro2);
        pages.add(intro3);
        pages.add(intro5);
        pages.add(intro6);
        pages.add(intro8);

        for (OnboarderPage page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.white);
            //page.setMultilineDescriptionCentered(true);
        }
        setSkipButtonTitle("Skip");
        setFinishButtonTitle("Finish");
        setOnboardPagesReady(pages);
    }

    @Override
    public void onSkipButtonPressed() {
        super.onSkipButtonPressed();

        if (isSuccessful){
            gotoHomePage();
        } else {
            Toast.makeText(this, "Saving Settings...", Toast.LENGTH_LONG).show();
            signInAnonymously();
        }
        //sharedIntro.edit().putBoolean("First_Run", false).apply();
        //gotoLoginPage();
    }

    @Override
    public void onFinishButtonPressed() {

        if (isSuccessful){
            gotoHomePage();
        } else {
            Toast.makeText(this, "Saving Settings...", Toast.LENGTH_LONG).show();
            signInAnonymously();
        }
    }

    private void gotoHomePage() {
        Intent homeIntent = new Intent(ActivityIntro.this, ActivityHome.class);
        startActivity(homeIntent);
        finish();
    }


    private void storeGuestUser(String deviceID) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", "Guest User");
        userMap.put("image", guest_user_image);
        firebaseFirestore.collection("Users").document(deviceID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    isSuccessful = true;
                    sharedIntro.edit().putBoolean("First_Run", false).apply();

                    //gotoLoginPage();
                    //Snackbar.make(findViewById(R.id.content), "Welcome Sports Lover!", Snackbar.LENGTH_LONG).show();
//                    Intent mainIntent = new Intent(ActivityIntro.this, ActivityHome.class);
//                    startActivity(mainIntent);
//                    finish();
                } else {
                    sharedIntro.edit().putBoolean("First_Run", true).apply();
                    isSuccessful = false;

                    String error = task.getException().getMessage();
                    //Toast.makeText(ActivityIntro.this, "Error, try again!", Toast.LENGTH_LONG).show();
                }
                //loginProgress.setVisibility(View.GONE);
            }
        });
    }

    private void signInAnonymously(){
        firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    storeGuestUser(user.getUid());
                } else {
                    //Toast.makeText(ActivityIntro.this, "Error, try again!", Toast.LENGTH_LONG).show();
                    //Toast.makeText(ActivityIntro.this, "Sign error: "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}