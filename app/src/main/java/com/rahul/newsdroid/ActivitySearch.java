package com.rahul.newsdroid;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahul.newsdroid.Adapter.AdapterNews;
import com.rahul.newsdroid.Data.DataNewsPost;
import com.rahul.newsdroid.Data.DataNewsUser;

import java.util.ArrayList;
import java.util.List;

public class ActivitySearch extends AppCompatActivity {

    String searchedData;
    Parcelable state;
    String categoryGot;
    private List<DataNewsPost> blog_list;
    private List<DataNewsUser> user_list;
    private AdapterNews blogRecyclerAdapter;
    private LinearLayoutManager lnrMgr, subCatlnrMgr;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSub;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private LottieAnimationView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null){
            searchedData = getIntent().getExtras().getString("searchData");
        }

        setTitle(searchedData);

        //Start everything from here
        loadingView = findViewById(R.id.loading_search);
        recyclerView = findViewById(R.id.rv_search);

        blog_list = new ArrayList<>();
        user_list = new ArrayList<>();

        blogRecyclerAdapter = new AdapterNews(blog_list);

        lnrMgr = new LinearLayoutManager(this);
        if (state != null) {
            lnrMgr.onRestoreInstanceState(state);
        }

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this,
                lnrMgr.getOrientation());

        //Add this line if you want to show divider line between recyclerview items
        //recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setLayoutManager(lnrMgr);
        recyclerView.setAdapter(blogRecyclerAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.getItemAnimator().setChangeDuration(0);

        //loadHomePage();
        loadSearchedPosts(searchedData);
    }

    private void loadSearchedPosts(String searchedData) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query firstQuery = firebaseFirestore.collection("Posts")
                .whereEqualTo("desc", searchedData).orderBy("timestamp", Query.Direction.ASCENDING);

        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
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

                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}