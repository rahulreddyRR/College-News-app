package com.rahul.newsdroid;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahul.newsdroid.Adapter.AdapterCategorySub;
import com.rahul.newsdroid.Adapter.AdapterNews;
import com.rahul.newsdroid.Data.DataNewsPost;
import com.rahul.newsdroid.Data.DataNewsUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityHomeCategory extends AppCompatActivity {

    Parcelable state;
    String categoryGot;
    private List<DataNewsPost> blog_list;
    private List<DataNewsUser> user_list;
    private AdapterNews blogRecyclerAdapter;
    private AdapterCategorySub categorySubAdapter;
    private LinearLayoutManager lnrMgr, subCatlnrMgr;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSub;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private FloatingActionButton newPost;
    private LottieAnimationView loadingView;

    //String categoryGot = "";
    //Save recyclerview state
    /* Here starts the sub strings */
    ArrayList<String> sublist_cricket = new ArrayList<>(Arrays.asList("BCA Student"));
    ArrayList<String> sublist_football = new ArrayList<>(Arrays.asList("MCA Student"));
    ArrayList<String> sublist_tennis = new ArrayList<>(Arrays.asList("B.Sc Student"));
    ArrayList<String> sublist_more = new ArrayList<>(Arrays.asList("M.Sc Student"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            categoryGot = getIntent().getExtras().getString("CategoryPassed");
        }

        loadingView = findViewById(R.id.loading_category);
        recyclerView = findViewById(R.id.rv_category_home);
        recyclerViewSub = findViewById(R.id.rv_category_subs);

        subCatlnrMgr = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Intent accountIntent = new Intent(ActivityHomeCategory.this, ActivityAccount.class);
            startActivity(accountIntent);
        }

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

        loadCategorySubs();

        loadCategoryPage(categoryGot);
    }

    public ActivityHomeCategory(){}

    private void loadCategorySubs() {
        if (categoryGot.equals("BCA Staff Admin")) {
            categorySubAdapter = new AdapterCategorySub(this, sublist_cricket);

        } else if (categoryGot.equals("MCA Staff Admin")) {
            categorySubAdapter = new AdapterCategorySub(this, sublist_football);

        } else if (categoryGot.equals("B.Sc Staff Admin")) {
            categorySubAdapter = new AdapterCategorySub(this, sublist_tennis);

        } else if (categoryGot.equals("M.Sc Staff Admin")) {
            categorySubAdapter = new AdapterCategorySub(this, sublist_more);
        }

        recyclerViewSub.setVisibility(View.VISIBLE);

        subCatlnrMgr.setStackFromEnd(false);
        subCatlnrMgr.setReverseLayout(false);
        recyclerViewSub.setLayoutManager(subCatlnrMgr);
        recyclerViewSub.setAdapter(categorySubAdapter);
        recyclerViewSub.setHasFixedSize(true);
        recyclerViewSub.setNestedScrollingEnabled(false);
        recyclerViewSub.getItemAnimator().setChangeDuration(0);
    }

    private void loadCategoryPage(String categorySelected) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query firstQuery = firebaseFirestore.collection("Posts")
                .whereEqualTo("category", categorySelected).orderBy("timestamp", Query.Direction.DESCENDING);

        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots !=null && !documentSnapshots.isEmpty()) {
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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
