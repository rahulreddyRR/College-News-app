package com.rahul.newsdroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private TextView loginSkip;
    private Button loginRegBtn;

    private String deviceID;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;

    private ProgressBar loginProgress;
    private String guest_user_image = "https://firebasestorage.googleapis.com/v0/b/sports-hunt.appspot.com/o/profile_images%2Fguest_user.png?alt=media&token=1ba9ce27-892d-480a-862f-14afe2d76c3f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor("#B3ADAF"));
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loginEmailText = findViewById(R.id.reg_email);
        loginPassText = findViewById(R.id.reg_confirm_pass);
        loginSkip = findViewById(R.id.login_skip);
        loginBtn = findViewById(R.id.login_btn);
        loginRegBtn = findViewById(R.id.login_reg_btn);
        loginProgress = findViewById(R.id.login_progress);

        loginSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
                deviceID = Settings.Secure.getString(ActivityLogin.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                signInAnonymously();
            }
        });

        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(ActivityLogin.this, ActivitySignUp.class);
                startActivity(regIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hides the keyboard
                InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String loginEmail = loginEmailText.getText().toString();
                String loginPass = loginPassText.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseMessaging.getInstance().subscribeToTopic("post_push");
                                sendToMain();
                            } else {
                                String errorMessage = task.getException().getMessage();
                                showSnackBar(errorMessage);
                            }
                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void storeGuestUser(String deviceID) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", "Guest User");
        userMap.put("image", guest_user_image);
        firebaseFirestore.collection("Users").document(deviceID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Snackbar.make(findViewById(R.id.content), "Welcome Sports Lover!", Snackbar.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(ActivityLogin.this, ActivityHome.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ActivityLogin.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
                loginProgress.setVisibility(View.GONE);
            }
        });
    }

    private void signInAnonymously(){
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    storeGuestUser(user.getUid());
                } else {
                    Toast.makeText(ActivityLogin.this, "Sign error: "+task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content),message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }


    private void sendToMain() {
        Intent mainIntent = new Intent(ActivityLogin.this, ActivityHome.class);
        startActivity(mainIntent);
        finish();
    }
}
