package com.rahul.newsdroid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahul.newsdroid.Data.DataNotification;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

public class ActivityPostNews extends AppCompatActivity {

    boolean isYouTubeVideo = false;
    private String isYouTubeString = "no";
    private String youtubeVideoURL, finalYoutubeVideoURL;

    private ImageView newPostImage, pasteTitle, pasteDesc;
    private EditText newPostDesc, newPostTitle, newPostYouTubeLink;
    private Button newPostBtn;
    private ImageButton newPostGenerateButton;

    private Uri postImageUri = null;

    private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    String current_user_id, dateStringEdited;

    private Bitmap compressedImageFile;

    ClipboardManager myClipboard;

    //Sending data and notification
    String postMapID, postTitle, postDesc, postCategory, postImage, postTime, postLike, postYouTube;
    Switch newPostPushSwitch;
    Boolean sendNotify = true;
    FirebaseDatabase fireDatabase;
    DatabaseReference notificationReference;

    //Spinner here
    String selectedCategory = "Football";
    Spinner categoryChooser;
    List<String> categories_array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        FirebaseCrash.log("ActivityPostNews");

        // Spinner Drop down elements
        categories_array.add("BCA Staff Admin");
        categories_array.add("BCA Student");

        categories_array.add("MCA Staff Admin");
        categories_array.add("MCA Student");

        categories_array.add("B.Sc Staff Admin");
        categories_array.add("B.Sc Student");

        categories_array.add("M.Sc Staff Admin");
        categories_array.add("M.Sc Student");





        /* Spinner Initialization starts */
        categoryChooser = findViewById(R.id.categoryChooser);
        categoryChooser.setSelection(0);
        categoryChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "Football";
            }
        });

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories_array);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryChooser.setAdapter(categoriesAdapter);
        /* Spinner Initialization ends */

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostBtn = findViewById(R.id.post_btn);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostImage = findViewById(R.id.new_post_image);
        newPostTitle = findViewById(R.id.new_post_title);
        newPostProgress = findViewById(R.id.new_post_progress);
        newPostPushSwitch = findViewById(R.id.new_post_notification);
        newPostYouTubeLink = findViewById(R.id.new_post_youtube_link);
        newPostGenerateButton = findViewById(R.id.new_post_generate_button);

        pasteTitle = findViewById(R.id.pasteTitle);
        pasteDesc = findViewById(R.id.pasteDesc);

        checkClipboardContent();

        newPostGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeVideoURL = newPostYouTubeLink.getText().toString();
                if (youtubeVideoURL.equals("")) {
                    newPostYouTubeLink.setError("Field is blank!");
                } else if (youtubeVideoURL.length() < 13) {
                    newPostYouTubeLink.setError("Paste proper YouTube video link!");
                } else if (!youtubeVideoURL.startsWith("http")) {
                    newPostYouTubeLink.setError("URL have to be starting with HTTP!");
                } else {
                    finalYoutubeVideoURL = getExtactYTId(youtubeVideoURL, v);
                }
            }
        });


        pasteTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteCopyContent(1);
            }
        });
        pasteDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteCopyContent(2);
            }
        });

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(16, 9)
                        .start(ActivityPostNews.this);
            }
        });

        newPostPushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sendNotify = b;
            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final String title = newPostTitle.getText().toString();
                final String desc = newPostDesc.getText().toString();

                final String stringDescription = desc.replace("bbb", "\n");

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && postImageUri != null) {

                    newPostProgress.setVisibility(View.VISIBLE);
                    newPostBtn.setEnabled(false);

                    final String randomName = UUID.randomUUID().toString();

                    // PHOTO UPLOAD
                    File newImageFile = new File(postImageUri.getPath());
                    try {
                        compressedImageFile = new Compressor(ActivityPostNews.this)
                                .setMaxHeight(512)
                                .setMaxWidth(512)
                                .setQuality(65)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD
                    UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);
                    filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if (task.isSuccessful()) {

                                File newThumbFile = new File(postImageUri.getPath());
                                try {
                                    compressedImageFile = new Compressor(ActivityPostNews.this)
                                            .setMaxHeight(300)
                                            .setMaxWidth(300)
                                            .setQuality(65)
                                            .compressToBitmap(newThumbFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                        .child(randomName + ".jpg").putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap = new HashMap<>();
                                        //postMap.put("image_url", downloadUri);
                                        postMap.put("image_thumb", downloadthumbUri);
                                        postMap.put("title", title);
                                        postMap.put("desc", stringDescription);
                                        postMap.put("category", selectedCategory);
                                        postMap.put("user_id", current_user_id);
                                        if (isYouTubeVideo){
                                            postMap.put("image_url", finalYoutubeVideoURL);
                                            postMap.put("youtube_video","yes");
                                            postYouTube = finalYoutubeVideoURL;
                                        }else {
                                            postMap.put("youtube_video","no");
                                            postMap.put("image_url", "no_link");
                                            postYouTube = "no_link";
                                        }
                                        postMap.put("timestamp", FieldValue.serverTimestamp());

                                        postTitle = title;
                                        postDesc = stringDescription;
                                        postCategory = selectedCategory;
                                        postImage = downloadthumbUri;
                                        postTime = String.valueOf(FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                postMapID = task.getResult().getId();
                                                Log.d("posted_data_content", postMapID);

                                                if (task.isSuccessful()) {

                                                    sendNotificationData(sendNotify);

                                                    Snackbar.make(v, "Posts added successfully!", Snackbar.LENGTH_SHORT);
                                                    Intent mainIntent = new Intent(ActivityPostNews.this, ActivityHome.class);
                                                    startActivity(mainIntent);
                                                    finish();

                                                } else {
                                                    Snackbar.make(v, "Error while saving to database!", Snackbar.LENGTH_SHORT);
                                                }

                                                newPostProgress.setVisibility(View.INVISIBLE);
                                                newPostBtn.setEnabled(true);

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        FirebaseCrash.logcat(Log.ERROR, "ActivityPostNews_", e.getLocalizedMessage());
                                        FirebaseCrash.report(e);

                                        //Error handling
                                    }
                                });



                            } else {
                                newPostProgress.setVisibility(View.INVISIBLE);
                                newPostBtn.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });
    }


    private void checkClipboardContent() {
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (!(myClipboard.hasPrimaryClip())) {

            pasteTitle.setEnabled(false);
            pasteDesc.setEnabled(false);

        } else {

            // This enables the paste menu item, since the clipboard contains plain text.
            pasteTitle.setEnabled(true);
            pasteDesc.setEnabled(true);
        }
    }

    private void pasteCopyContent(int case_code) {
        String text;
        ClipData abc = myClipboard.getPrimaryClip();
        ClipData.Item item = abc.getItemAt(0);
        text = item.getText().toString();
        if (case_code == 1) {
            newPostTitle.setText(text);
        } else {
            newPostDesc.setText(text);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void getCurrentTimeDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a - dd/MM/yyyy");
        String xxxTimeDate = sdf.format(c.getTime());
        dateStringEdited = xxxTimeDate.replace("am", "AM").replace("pm", "PM");
    }

    private void sendNotificationData(boolean canSend) {
        if (canSend) {
            getCurrentTimeDate();
            fireDatabase = FirebaseDatabase.getInstance();
            DataNotification dataNotification = new DataNotification(postMapID, postTitle, postDesc, postCategory, dateStringEdited, postImage, postLike, postYouTube, isYouTubeString);
            notificationReference = fireDatabase.getReference().child("NotificationPost"); //Do not change this
            notificationReference.setValue(dataNotification);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        final String title = newPostTitle.getText().toString();
        final String desc = newPostDesc.getText().toString();

        if (!title.isEmpty() || !desc.isEmpty()) {
            showExitDialog();
        }else {
            finish();
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Discard Writing?")
                .setMessage("You have selected to close writing new post, are you sure?")
                .setPositiveButton("KEEP WRITING", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setIcon(R.drawable.ic_note_add_black_24dp)
                .show();
    }

    public String getExtactYTId(String ytUrl, View myView) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        isYouTubeString = "yes";
        isYouTubeVideo = true;
        Snackbar.make(myView,"Adding video success!",Snackbar.LENGTH_LONG).show();
        return vId;
    }
}