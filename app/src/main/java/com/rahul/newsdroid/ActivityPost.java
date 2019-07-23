package com.rahul.newsdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class ActivityPost extends YouTubeBaseActivity {

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
    NestedScrollView postNestedScroll;
    TextView postTitle, postDesc, postLikeCount, postDate, postDetails;
    String stringYouTube, stringTitle, stringDesc, stringImg, stringPostUserName, stringCatg, stringPostID, stringUserID, stringPostDate;

    YouTubePlayerView postYoutubePlayerView;

    YouTubePlayer.OnInitializedListener youTubeListener;

    public ActivityPost() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        retrieveSelectedPost();

        //Initializing all the views
        postImg = findViewById(R.id.postImg);
        postTitle = findViewById(R.id.postTitle);
        postDesc = findViewById(R.id.postDesc);
        //postCatg = findViewById(R.id.postCatg);
        //postDate = findViewById(R.id.postDate);
        postDetails = findViewById(R.id.postDetails);
        postLikeButton = findViewById(R.id.postLike);
        postFontSize = findViewById(R.id.postFontSize);
        postLikeCount = findViewById(R.id.postLikeCount);
        postShare = findViewById(R.id.postShare);
        postMoreCat = findViewById(R.id.postMoreCat);
        postRelativeImage = findViewById(R.id.postRelativeImage);
        postNestedScroll = findViewById(R.id.postNestedScroll);
        postYoutubePlayerView = findViewById(R.id.postYoutubePlayerView);

        postLinearTop = findViewById(R.id.postLinearTop);
        postLinearBottom = findViewById(R.id.postLinearBottom);

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

        //Set Category as Toolbar Title
        setTitle(stringCatg);

        //Setting value to texts
        postTitle.setText(stringTitle);
        postDesc.setText(stringDesc);
        //postDate.setText(stringPostDate);
        //postCatg.setText(stringCatg);
        /*postLikeButton.setLiked(isLiked);
        postLikeCount.setText(String.valueOf(likeCount));*/

        String stringPostDetails = "Posted by " + stringPostUserName + " on " + stringPostDate;
        postDetails.setText(stringPostDetails);

        //Check if post image or YouTube video is available

        if (stringYouTube != null && stringYouTube.length()<11){
            //Showing the Post Image
            final RequestOptions requestOptions = new RequestOptions();
            Glide.with(ActivityPost.this).applyDefaultRequestOptions(requestOptions).load(stringImg)
                    .thumbnail(Glide.with(ActivityPost.this).load(stringImg)).into(postImg);
        } else {
            loadYouTubePlayer();
        }

        String sourceString = "Find more posts from " + "<b>" + stringCatg + "</b>";
        postMoreCat.setText(Html.fromHtml(sourceString));

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
                Intent intent = new Intent(ActivityPost.this, ImagePreviewActivity.class);
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
                Intent passCat = new Intent(ActivityPost.this, ActivityHomeCategory.class);
                passCat.putExtra("CategoryPassed", stringCatg);
                startActivity(passCat);
            }
        });

        if (postNestedScroll != null) {
            postNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                    }
                    if (scrollY < oldScrollY) {
                    }
                    if (scrollY == 0) {
                    }
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        //postShareScroll.setVisibility(View.VISIBLE);
                        //postMoreCat.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

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
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        postYoutubePlayerView.initialize(ActivityConfig.YOUTUBE_API, youTubeListener);
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

    private void retrieveSelectedPost() {
        SharedPreferences sp = getSharedPreferences("POST_DATA", MODE_PRIVATE);
        stringImg = sp.getString("POST_IMAGE", "error");
        stringYouTube = sp.getString("POST_YOUTUBE", "error");
        stringTitle = sp.getString("POST_TITLE", "Loading...");
        stringDesc = sp.getString("POST_DESC", "Loading...");
        stringCatg = sp.getString("POST_CATG", "Loading...");
        stringPostUserName = sp.getString("POST_USERNAME", "Loading...");
        stringPostID = sp.getString("POST_ID", "error");
        stringUserID = sp.getString("POST_USER_ID", "error");
        stringPostDate = sp.getString("POST_DATE", "error");
        likeCount = sp.getInt("POST_LIKE_COUNT", 0);
        isLiked = sp.getBoolean("POST_LIKE", false);
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

    @Override
    public void onResume() {
        super.onResume();
        //checkFontSize();
    }
}