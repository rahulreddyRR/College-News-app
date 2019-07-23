package com.rahul.newsdroid.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rahul.newsdroid.ActivityConfig;
import com.rahul.newsdroid.ActivityHome;
import com.rahul.newsdroid.ActivityPost;
import com.rahul.newsdroid.Data.DataNewsPost;
import com.rahul.newsdroid.R;

import java.util.Date;
import java.util.List;

public class AdapterNews extends RecyclerView.Adapter<AdapterNews.ViewHolder> {

    private List<DataNewsPost> blog_list;
    public Context context;

    private int likeCount;
    private boolean isLiked;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String dateString;

    public AdapterNews(List<DataNewsPost> blog_list) {
        this.blog_list = blog_list;
        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        String postListStyle = ActivityConfig.POST_LIST_STYLE;
        View postView = null;

        if (postListStyle.equals("STYLE_ONE")) {
            postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_style_one, parent, false);
        } else if (postListStyle.equals("STYLE_TWO")) {
            postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_style_two, parent, false);
        } else if (postListStyle.equals("STYLE_THREE")) {
            postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_style_three, parent, false);
        } else if (postListStyle.equals("STYLE_FOUR")) {
            if (viewType == 1) {
                postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_style_one, parent, false);
            } else {
                postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_style_two, parent, false);
            }
        }

        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = blog_list.get(position).DataNewsPostID;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String category_name = blog_list.get(position).getCategory();
        final String title_data = blog_list.get(position).getTitle();
        final String desc_data = blog_list.get(position).getDesc();
        final String youtube_data = blog_list.get(position).getImage_url();
        holder.setTitleDescText(title_data, desc_data, category_name);


        final String youtube_available = blog_list.get(position).getYoutube_video();
        if (youtube_available != null && youtube_available.equals("yes")){
            holder.blogYouTubeImage.setVisibility(View.VISIBLE);
        }

        final String thumbUri = blog_list.get(position).getImage_thumb();

        //final String postedUserName = user_list.get(position).getName();

        holder.setBlogImage(thumbUri);

        String blog_user_id = blog_list.get(position).getUser_id();

        if (ActivityConfig.ADMIN_APP) {
            holder.blogPostDelete.setVisibility(View.VISIBLE);
        }

        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            dateString = DateFormat.format("h:mm a - dd/MM/yyyy", new Date(millisecond)).toString();
            String dateStringEdited = dateString.replace("am", "AM").replace("pm", "PM");
            holder.setTime(dateStringEdited);
        } catch (Exception e) {
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeGot = "" + holder.blogDate.getText();
                saveSelectedPost(youtube_data, title_data, desc_data, thumbUri, "Admin", category_name, blogPostId, currentUserId, timeGot, likeCount, isLiked, youtube_available);
                pushToActivity();

                //pushFragment(new FragmentPost(), context);
            }
        });

/*
        String userName = user_list.get(position).getName();
        String userImage = user_list.get(position).getImage();
        */
/*
        holder.setUserData(userName, userImage);
*/


        /* Like Starts Here*/
/*
        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();
                    likeCount = count;
                    holder.updateLikesCount(count);

                } else {

                    likeCount = 0;
                    holder.updateLikesCount(0);

                }

            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    holder.blogLikeBtn.setImageResource(R.drawable.action_like_accent);
                    isLiked = true;

                } else {
                    holder.blogLikeBtn.setImageResource(R.drawable.action_like_gray);
                    isLiked = false;
                }

            }
        });
        //Likes Feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        } else {
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });*/

        /*
         *//* Comment Starts Here *//*

        //Get Comment Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){
                    int count = documentSnapshots.size();
                    holder.updateCommentCount(count);

                } else {
                    holder.updateCommentCount(0);
                }
            }
        });

        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);
            }
        });

        */


        //Post Deleting
        holder.blogPostDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Sure you want to delete?", Snackbar.LENGTH_SHORT)
                        .setActionTextColor(Color.WHITE)
                        .setAction("DELETE", new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(v, "News deleted, refresh to see changes...", Snackbar.LENGTH_SHORT).show();
                                        blog_list.remove(position);
                                        notifyItemRemoved(position);
                                        ((ActivityHome)context).reCreateTheActivity();
                                        //notifyDataSetChanged();
                                    }
                                });
                            }
                        })
                        .show();
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView titleView;
        private TextView descView;
        private TextView categoryView;
        private ImageView blogImageView;
        private ImageButton blogPostDelete;
        private ImageView blogYouTubeImage;
        private TextView blogDate;

        //private ImageView blogLikeBtn;
        //private TextView blogLikeCount;

        //private ImageView blogCommentBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            //blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogYouTubeImage = mView.findViewById(R.id.blog_video_play);
            blogPostDelete = mView.findViewById(R.id.blog_delete_post);
            //blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
        }

        @SuppressLint("SetTextI18n")
        public void setTitleDescText(String titleText, String descText, String categoryText) {
            titleView = mView.findViewById(R.id.blog_title);
            descView = mView.findViewById(R.id.blog_desc);
            categoryView = mView.findViewById(R.id.blog_category);

/*
            //Random Bg Color for Category TextView
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            categoryView.setBackgroundColor(color);
*/

            categoryView.setText(categoryText);
            titleView.setText(titleText);
            descView.setText(descText);
        }

        public void setBlogImage(String thumbUri) {
            blogImageView = mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder_post);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(thumbUri).into(blogImageView);

        }

        public void setTime(String date) {
            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        public void setUserData(String name, String image) {
            //blogUserImage = mView.findViewById(R.id.blog_user_image);
            //blogUserName = mView.findViewById(R.id.blog_user_name);
            //blogUserName.setText(name);
            RequestOptions placeholderOption = new RequestOptions();
            //placeholderOption.placeholder(R.drawable.profile_placeholder);
            //Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);
        }

/*        public void updateLikesCount(int count){
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");
        }*/

/*        public void updateCommentCount(int count){
            blogLikeCount = mView.findViewById(R.id.blog_comment_count);
            blogLikeCount.setText(count + " Comments");
        }*/

    }

    private void saveSelectedPost(String YouTubeData, String postTitle, String postDesc, String postImage, String postUserName,
                                  String postCategory, String postID, String userID, String postDate, int postLikeCount, boolean postLiked, String isYouTubeVideo) {

        SharedPreferences sp = context.getSharedPreferences("POST_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("POST_YOUTUBE", YouTubeData);
        editor.putString("POST_TITLE", postTitle);
        editor.putString("POST_DESC", postDesc);
        editor.putString("POST_IMAGE", postImage);
        editor.putString("POST_USERNAME", postUserName);
        editor.putString("POST_CATG", postCategory);
        editor.putString("POST_ID", postID);
        editor.putString("POST_DATE", postDate);
        editor.putString("POST_USER_ID", userID);
        editor.putString("YOUTUBE_VIDEO",isYouTubeVideo);
        editor.putInt("POST_LIKE_COUNT", postLikeCount);
        editor.putBoolean("POST_LIKE", postLiked);

        editor.apply();
    }

    public void pushToActivity() {
        Intent postIntent = new Intent(context, ActivityPost.class);
        context.startActivity(postIntent);
    }
}
