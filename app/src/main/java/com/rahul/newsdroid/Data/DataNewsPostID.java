package com.rahul.newsdroid.Data;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

@SuppressWarnings("ALL")
public class DataNewsPostID {

    @Exclude
    @SuppressWarnings("unchecked")
    public String DataNewsPostID;

    public <T extends DataNewsPostID> T withId(@NonNull final String id) {
        this.DataNewsPostID = id;
        return (T) this;
    }
}
