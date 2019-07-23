package com.rahul.newsdroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.himangi.imagepreview.ImagePreviewActivity;
import com.himangi.imagepreview.PreviewFile;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityHotNews extends YouTubeBaseActivity {

    int max = 4;
    int likeCount = 0;
    ImageView postImg;
    int incr_count = 0;
    Button postMoreCat;
    AdRequest adRequest;

    Toolbar toolbar;

    LinearLayout postShare, postLinearTop, postLinearBottom;
    ArrayList<String> list;
    Boolean isLiked = false;
    float titleSize, descSize;
    LikeButton postLikeButton;
    RelativeLayout postRelativeImage, postFontSize;
    TextView postTitle, postDesc, postLikeCount, postDate, postDetails;
    String stringYouTube, stringTitle, stringDesc, stringImg, stringPostUserName, stringCatg, stringPostID, stringUserID, stringPostDate, stringYouTubeAvailable;

    YouTubePlayerView postYoutubePlayerView;

    YouTubePlayer.OnInitializedListener youTubeListener;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase fireDatabase;
    DatabaseReference notificationReference;

    AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_news);
        showLoadingDialog();

        //Initializing all the views
        postImg = findViewById(R.id.hotImg);
        postTitle = findViewById(R.id.hotTitle);
        postDesc = findViewById(R.id.hotDesc);
        //postCatg = findViewById(R.id.postCatg);
        //postDate = findViewById(R.id.postDate);
        postDetails = findViewById(R.id.hotDetails);
        postLikeButton = findViewById(R.id.hotLike);
        postFontSize = findViewById(R.id.hotFontSize);
        postLikeCount = findViewById(R.id.hotLikeCount);
        postShare = findViewById(R.id.hotShare);
        postMoreCat = findViewById(R.id.hotMoreCat);
        postRelativeImage = findViewById(R.id.hotRelativeImage);
        postYoutubePlayerView = findViewById(R.id.hotYoutubePlayerView);

        postLinearTop = findViewById(R.id.hotLinearTop);
        postLinearBottom = findViewById(R.id.hotLinearBottom);

        firebaseAuth = FirebaseAuth.getInstance();
        fireDatabase = FirebaseDatabase.getInstance();
        stringUserID = firebaseAuth.getCurrentUser().getUid();
        notificationReference = fireDatabase.getReference().child("NotificationPost");

        //final DataNotification dataNotification = new DataNotification(postTitle,postDesc,postCategory,postTime,postImage);
        //notificationReference.setValue(dataNotification);

        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                stringImg = snapshot.child("post_image").getValue(String.class);
                stringTitle = snapshot.child("post_title").getValue(String.class);
                stringDesc = snapshot.child("post_description").getValue(String.class);
                stringCatg = snapshot.child("post_category").getValue(String.class);
                stringPostDate = snapshot.child("post_date").getValue(String.class);
                stringYouTube = snapshot.child("youtube_video").getValue(String.class);
                stringPostID = snapshot.child("post_id").getValue(String.class);
                stringYouTubeAvailable = snapshot.child("post_youtube").getValue(String.class);

                if (stringYouTubeAvailable != null && stringYouTubeAvailable.equals("yes")){
                    loadYouTubePlayer();
                }else {
                    final RequestOptions requestOptions = new RequestOptions();
                    Glide.with(ActivityHotNews.this).applyDefaultRequestOptions(requestOptions).load(stringImg)
                            .thumbnail(Glide.with(ActivityHotNews.this).load(stringImg)).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loadingDialog.dismiss();
                            return false;
                        }
                    }).into(postImg);
                }

                postTitle.setText(stringTitle);
                postDesc.setText(stringDesc);

                stringPostUserName = "Admin";
                String stringPostDetails = "Posted by " + stringPostUserName + " on " + stringPostDate;
                postDetails.setText(stringPostDetails);

                String sourceString = "Find more posts from " + "<b>" + stringCatg + "</b>";
                postMoreCat.setText(Html.fromHtml(sourceString));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityHotNews.this, "Server error!", Toast.LENGTH_SHORT).show();
            }
        });


        //postMoreCat.setText(stringCatg);

        //Add AdUnit ID for AdViews
        //Initializing some misc things too
        adRequest = new AdRequest.Builder().build();

        //Check Font Size
        titleSize = postTitle.getTextSize();
        descSize = postDesc.getTextSize();
        checkFontSize();

        //This is for Post Image Click View
        list = new ArrayList<>();
        final ArrayList<PreviewFile> imageList = new ArrayList<>();
        imageList.add(new PreviewFile(stringImg, stringTitle));


        //Check Likes
        checkForLiked();
        checkForLikeCount();

        //Show off share
        //doSuperShare(view);

        //Dont touch upto here

        postFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFontSize();
            }
        });

        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHotNews.this, ImagePreviewActivity.class);
                intent.putExtra(ImagePreviewActivity.IMAGE_LIST, imageList);
                intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, 0);
                startActivity(intent);
                startActivity(intent);
            }
        });

        postLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeThePost();
                likeCount++;
                postLikeCount.setText(String.valueOf(likeCount));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeThePost();
                likeCount--;
                postLikeCount.setText(String.valueOf(likeCount));
            }
        });

        postShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //path_url = stringImg;
                //askPermission();
                postImg.invalidate();
                BitmapDrawable drawable = (BitmapDrawable) postImg.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                prepareShareIntent(bitmap);

            }
        });

        postMoreCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passCat = new Intent(ActivityHotNews.this, ActivityHomeCategory.class);
                passCat.putExtra("CategoryPassed", stringCatg);
                startActivity(passCat);
            }
        });


        //Loading Banner Ad
        if (ActivityConfig.SHOW_BANNER_AD) {
            loadBannnerAdTop();
            loadBannnerAdBottom();
        }

    }

    private void loadYouTubePlayer() {
        postRelativeImage.setVisibility(View.GONE);
        postYoutubePlayerView.setVisibility(View.VISIBLE);
        youTubeListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(stringYouTube);
                loadingDialog.dismiss();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        postYoutubePlayerView.initialize("AIzaSyCGAjBPcn7CX38jMj5C2j8LjnnnG9HOK_s", youTubeListener);
    }


    //To change font size
    private void checkFontSize() {
        SharedPreferences sp = getSharedPreferences("SHARE_SETTINGS", MODE_PRIVATE);
        String fontSizeString = sp.getString("fontSize", "Normal");

        if (fontSizeString.equals("Small")) {
            postTitle.setTextSize(0, postTitle.getTextSize());
            postDesc.setTextSize(0, postDesc.getTextSize());

        } else if (fontSizeString.equals("Normal")) {
            postTitle.setTextSize(0, postTitle.getTextSize() + 3.0f);
            postDesc.setTextSize(0, postDesc.getTextSize() + 3.0f);

        } else if (fontSizeString.equals("Large")) {
            postTitle.setTextSize(0, postTitle.getTextSize() + 6.0f);
            postDesc.setTextSize(0, postDesc.getTextSize() + 6.0f);
        } else {
            postTitle.setTextSize(titleSize);
            postDesc.setTextSize(descSize);
        }
    }

    //Sharing Image and Post
    private void prepareShareIntent(Bitmap bmp) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri bmpUri = getLocalBitmapUri(bmp);
        Intent shareIntent = new Intent();
        //shareIntent.setPackage("com.whatsapp");
        shareIntent.setAction(Intent.ACTION_SEND);

        String postData = stringTitle + "\n" + stringDesc + "\n\n\n" + "Get latest sports news on SportsHunt News App: https://play.google.com/store/apps/details?id=com.sportshunt.app";

        shareIntent.putExtra(Intent.EXTRA_TEXT, postData);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Post Via"));
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        String xAppName = getString(R.string.app_name);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "" +xAppName+ ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void checkForLiked() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts/" + stringPostID + "/Likes").document(stringUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    postLikeButton.setLiked(false);
                } else {
                    postLikeButton.setLiked(true);
                }
            }
        });
    }

    private void checkForLikeCount() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts/" + stringPostID + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    int count = documentSnapshots.size();
                    postLikeCount.setText(String.valueOf(count));

                } else {
                    postLikeCount.setText("0");
                }
            }
        });
    }

    private void likeThePost() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts/" + stringPostID + "/Likes").document(stringUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    //Log.d("Like_State","Exists");
                    Map<String, Object> likesMap = new HashMap<>();
                    likesMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts/" + stringPostID + "/Likes").document(stringUserID).set(likesMap);
                } else {
                    //Log.d("Like_State","Not Exists");
                    firebaseFirestore.collection("Posts/" + stringPostID + "/Likes").document(stringUserID).delete();
                }
            }
        });
    }

    private void loadBannnerAdTop() {
        AdRequest adRequest = new AdRequest.Builder().build();
        final LinearLayout adContainer = postLinearTop;
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ActivityConfig.AD_BANNER_TOP);
        adView.loadAd(adRequest);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        adContainer.addView(adView, params);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                postLinearTop.setVisibility(View.VISIBLE);
                //loadBannnerAdBottom();
            }
        });
    }

    private void loadBannnerAdBottom() {
        AdRequest adRequest = new AdRequest.Builder().build();
        final LinearLayout adContainer = postLinearBottom;
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.LARGE_BANNER);
        adView.setAdUnitId(ActivityConfig.AD_BANNER_BOTTOM);
        adView.loadAd(adRequest);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        adContainer.addView(adView, params);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                postLinearBottom.setVisibility(View.VISIBLE);
            }
        });
    }

    private void changeFontSize() {
        if (incr_count < max) {
            incr_count++;
            postTitle.setTextSize(0, postTitle.getTextSize() + 1.0f);
            postDesc.setTextSize(0, postDesc.getTextSize() + 1.0f);

        } else {
            incr_count = incr_count - 5;
            postTitle.setTextSize(0, postTitle.getTextSize() - 4.0f);
            postDesc.setTextSize(0, postTitle.getTextSize() - 4.0f);
        }
    }

    private void showLoadingDialog(){
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.dialog_loading, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(alertLayout);
        alert.setCancelable(false);
/*        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsDialog.dismiss();
            }
        });*/
        loadingDialog = alert.create();
        loadingDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //checkFontSize();
    }
}