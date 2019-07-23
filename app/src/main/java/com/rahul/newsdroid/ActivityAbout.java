package com.rahul.newsdroid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ActivityAbout extends AppCompatActivity {

    ImageView social_fb, social_tw, social_inst, social_yt, social_brw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        social_fb = findViewById(R.id.social_fb);
        social_tw = findViewById(R.id.social_tw);
        social_inst = findViewById(R.id.social_inst);
        social_yt = findViewById(R.id.social_yt);
        social_brw = findViewById(R.id.social_brw);

        social_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFbUrl(ActivityConfig.SOCIAL_FACEBOOK);
            }
        });

        social_tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTwitterUrl(ActivityConfig.SOCIAL_TWITTER);
            }
        });

        social_inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInstagram(ActivityConfig.SOCIAL_INSTAGRAM);
            }
        });

        social_yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openYoutubeUrl(ActivityConfig.SOCIAL_YOUTUBE);
            }
        });

        social_brw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebUrlInChrome(ActivityConfig.SOCIAL_BROWSER);
            }
        });
    }

    protected void openFbUrl(String username){
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
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
    }

    protected void openTwitterUrl(String username) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=" + username));
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/" + username)));
        }
    }

    protected void openYoutubeUrl(String youtubeUrl) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(youtubeUrl));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(youtubeUrl));
            startActivity(intent);
        }
    }

    protected void openInstagram(String username) {
        Uri uri = Uri.parse("http://instagram.com/_u/" + username);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.instagram.android");
        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/" + username)));
        }
    }

    protected void openWebUrlInChrome(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
