package com.rahul.newsdroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityPostScore extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference scoreReference;

    //Top - Views
    ImageView isl_img_top_left, isl_img_top_right;
    EditText isl_date;
    EditText isl_time, isl_score_left, isl_score_right;
    //Top - String
    String stringScoreLeft, stringScoreRight;
    String stringImgTopLeft, stringImgTopRight, stringDate, stringTime;

    //Middle - Views
    ImageView isl_shortIcon_left, isl_shortIcon_right;
    EditText isl_shots_left, isl_shots_right, isl_shots_target_left, isl_shots_target_right, isl_possession_left, isl_possession_right,
            isl_passes_left, isl_passes_right, isl_pass_accuracy_left,isl_pass_accuracy_right, isl_fouls_left, isl_fouls_right,
            isl_yellow_card_left, isl_yellow_card_right, isl_red_card_left, isl_red_card_right, isl_offside_left, isl_offside_right,
            isl_corners_left, isl_corners_right;

    String stat_isl_shots_left, stat_isl_shots_right, stat_isl_shots_target_left, stat_isl_shots_target_right, stat_isl_possession_left, stat_isl_possession_right,
            stat_isl_passes_left, stat_isl_passes_right, stat_isl_pass_accuracy_left,stat_isl_pass_accuracy_right, stat_isl_fouls_left, stat_isl_fouls_right,
            stat_isl_yellow_card_left, stat_isl_yellow_card_right, stat_isl_red_card_left, stat_isl_red_card_right, stat_isl_offside_left, stat_isl_offside_right,
            stat_isl_corners_left, stat_isl_corners_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_score);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        scoreReference = firebaseDatabase.getReference().child("ScoreData/ISL");


        //Top
        isl_img_top_left = findViewById(R.id.isl_team_icon_left);
        isl_img_top_right = findViewById(R.id.isl_team_icon_right);
        isl_date = findViewById(R.id.isl_date);
        isl_time = findViewById(R.id.isl_fulltime);
        isl_score_left = findViewById(R.id.isl_team_score_left);
        isl_score_right = findViewById(R.id.isl_team_score_right);

        //Middle
        isl_shots_left = findViewById(R.id.isl_shots_left);
        isl_shots_right = findViewById(R.id.isl_shots_right);
        isl_shots_target_left = findViewById(R.id.isl_target_shots_left);
        isl_shots_target_right = findViewById(R.id.isl_target_shots_right);
        isl_possession_left = findViewById(R.id.isl_possession_left);
        isl_possession_right = findViewById(R.id.isl_possession_right);
        isl_passes_left = findViewById(R.id.isl_passes_left);
        isl_passes_right = findViewById(R.id.isl_passes_right);
        isl_pass_accuracy_left = findViewById(R.id.isl_pass_accuracy_left);
        isl_pass_accuracy_right = findViewById(R.id.isl_pass_accuracy_right);
        isl_fouls_left = findViewById(R.id.isl_fouls_left);
        isl_fouls_right = findViewById(R.id.isl_fouls_right);
        isl_yellow_card_left= findViewById(R.id.isl_yellow_cards_left);
        isl_yellow_card_right = findViewById(R.id.isl_yellow_cards_right);
        isl_red_card_left = findViewById(R.id.isl_red_cards_left);
        isl_red_card_right = findViewById(R.id.isl_red_cards_right);
        isl_offside_left = findViewById(R.id.isl_offsides_left);
        isl_offside_right = findViewById(R.id.isl_offsides_right);
        isl_corners_left = findViewById(R.id.isl_corners_left);
        isl_corners_right = findViewById(R.id.isl_corners_right);

        loadFullScore();
    }


    private void loadFullScore(){
        scoreReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Top
                stringImgTopLeft = snapshot.child("score_imgLeft").getValue(String.class);
                stringImgTopRight = snapshot.child("score_imgRight").getValue(String.class);
                stringDate = snapshot.child("score_date").getValue(String.class);
                stringTime = snapshot.child("score_time").getValue(String.class);
                stringScoreLeft = snapshot.child("score_left").getValue(String.class);
                stringScoreRight = snapshot.child("score_right").getValue(String.class);

                //isl_score_left.setText("asd"+stringScoreLeft);
                //isl_score_right.setText("asd"+stringScoreRight);

                isl_time.setText(stringTime);
                isl_date.setText(stringDate);

                isl_score_right.setText(String.valueOf(stringScoreRight));
                isl_score_left.setText(String.valueOf(stringScoreLeft));

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placeholder_post);
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(stringImgTopLeft).thumbnail(Glide.with(getApplicationContext()).load(stringImgTopLeft)
                ).into(isl_img_top_left);

                Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(stringImgTopRight).thumbnail(Glide.with(getApplicationContext()).load(stringImgTopRight)
                ).into(isl_img_top_right);


                //Middle
                stat_isl_shots_left = snapshot.child("score_shots_left").getValue(String.class);
                stat_isl_shots_right = snapshot.child("score_shots_right").getValue(String.class);
                stat_isl_shots_target_left = snapshot.child("score_target_shot_left").getValue(String.class);
                stat_isl_shots_target_right = snapshot.child("score_target_shot_right").getValue(String.class);
                stat_isl_possession_left = snapshot.child("score_possession_left").getValue(String.class);
                stat_isl_possession_right = snapshot.child("score_possession_right").getValue(String.class);
                stat_isl_passes_left = snapshot.child("score_passes_left").getValue(String.class);
                stat_isl_passes_right = snapshot.child("score_passes_right").getValue(String.class);
                stat_isl_pass_accuracy_left = snapshot.child("score_pass_accuracy_left").getValue(String.class);
                stat_isl_pass_accuracy_right = snapshot.child("score_pass_accuracy_right").getValue(String.class);
                stat_isl_fouls_left = snapshot.child("score_fouls_left").getValue(String.class);
                stat_isl_fouls_right = snapshot.child("score_fouls_right").getValue(String.class);
                stat_isl_yellow_card_left = snapshot.child("score_yellow_card_left").getValue(String.class);
                stat_isl_yellow_card_right = snapshot.child("score_yellow_card_right").getValue(String.class);
                stat_isl_red_card_left = snapshot.child("score_red_card_left").getValue(String.class);
                stat_isl_red_card_right = snapshot.child("score_red_card_right").getValue(String.class);
                stat_isl_offside_left = snapshot.child("score_offsides_left").getValue(String.class);
                stat_isl_offside_right = snapshot.child("score_offsides_right").getValue(String.class);
                stat_isl_corners_left = snapshot.child("score_corners_left").getValue(String.class);
                stat_isl_corners_right = snapshot.child("score_corners_right").getValue(String.class);

                isl_shots_left.setText(String.valueOf(stat_isl_shots_left));
                isl_shots_right.setText(String.valueOf(stat_isl_shots_right));
                isl_shots_target_left.setText(String.valueOf(stat_isl_shots_target_left));
                isl_shots_target_right.setText(String.valueOf(stat_isl_shots_target_right));
                isl_possession_left.setText(String.valueOf(stat_isl_possession_left));
                isl_possession_right.setText(String.valueOf(stat_isl_possession_right));
                isl_passes_left.setText(String.valueOf(stat_isl_passes_left));
                isl_passes_right.setText(String.valueOf(stat_isl_passes_right));
                isl_pass_accuracy_left.setText(String.valueOf(stat_isl_pass_accuracy_left));
                isl_pass_accuracy_right.setText(String.valueOf(stat_isl_pass_accuracy_right));
                isl_fouls_left.setText(String.valueOf(stat_isl_fouls_left));
                isl_fouls_right.setText(String.valueOf(stat_isl_fouls_right));
                isl_yellow_card_left.setText(String.valueOf(stat_isl_yellow_card_left));
                isl_yellow_card_right.setText(String.valueOf(stat_isl_yellow_card_right));
                isl_red_card_left.setText(String.valueOf(stat_isl_red_card_left));
                isl_red_card_right.setText(String.valueOf(stat_isl_red_card_right));
                isl_offside_left.setText(String.valueOf(stat_isl_offside_left));
                isl_offside_right.setText(String.valueOf(stat_isl_offside_right));
                isl_corners_left.setText(String.valueOf(stat_isl_corners_left));
                isl_corners_right.setText(String.valueOf(stat_isl_corners_right));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void updateScore(){
        scoreReference.child("score_imgLeft").setValue("https://ssl.gstatic.com/onebox/media/sports/logos/_5VS8XluJxBhUh29V2yB_Q_96x96.png");
        scoreReference.child("score_imgRight").setValue("https://ssl.gstatic.com/onebox/media/sports/logos/Lz1eBn3t9Xmrhm5qm7Q3RQ_96x96.png");
        scoreReference.child("score_date").setValue(""+isl_date.getText().toString());
        scoreReference.child("score_time").setValue(""+isl_time.getText().toString());
        scoreReference.child("score_left").setValue(""+isl_score_left.getText().toString());
        scoreReference.child("score_right").setValue(""+isl_score_right.getText().toString());

        scoreReference.child("score_shots_left").setValue(""+isl_shots_left.getText().toString());
        scoreReference.child("score_shots_right").setValue(""+isl_shots_right.getText().toString());
        scoreReference.child("score_target_shot_left").setValue(""+isl_shots_target_left.getText().toString());
        scoreReference.child("score_target_shot_right").setValue(""+isl_shots_target_right.getText().toString());
        scoreReference.child("score_possession_left").setValue(""+isl_possession_left.getText().toString());
        scoreReference.child("score_possession_right").setValue(""+isl_possession_right.getText().toString());
        scoreReference.child("score_passes_left").setValue(""+isl_passes_left.getText().toString());
        scoreReference.child("score_passes_right").setValue(""+isl_passes_right.getText().toString());
        scoreReference.child("score_pass_accuracy_left").setValue(""+isl_pass_accuracy_left.getText().toString());
        scoreReference.child("score_pass_accuracy_right").setValue(""+isl_pass_accuracy_right.getText().toString());
        scoreReference.child("score_fouls_left").setValue(""+isl_fouls_left.getText().toString());
        scoreReference.child("score_fouls_right").setValue(""+isl_fouls_right.getText().toString());
        scoreReference.child("score_yellow_card_left").setValue(""+isl_yellow_card_left.getText().toString());
        scoreReference.child("score_yellow_card_right").setValue(""+isl_yellow_card_right.getText().toString());
        scoreReference.child("score_red_card_left").setValue(""+isl_red_card_left.getText().toString());
        scoreReference.child("score_red_card_right").setValue(""+isl_red_card_right.getText().toString());
        scoreReference.child("score_offsides_left").setValue(""+isl_offside_left.getText().toString());
        scoreReference.child("score_offsides_right").setValue(""+isl_offside_right.getText().toString());
        scoreReference.child("score_corners_left").setValue(""+isl_corners_left.getText().toString());
        scoreReference.child("score_corners_right").setValue(""+isl_corners_right.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_score, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }else if (item.getItemId() == R.id.action_upload){
            updateScore();
        }
        return super.onOptionsItemSelected(item);
    }
}