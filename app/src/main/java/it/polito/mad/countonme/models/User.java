package it.polito.mad.countonme.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The user data model
 * Created by francescobruno on 17/04/17.
 */

@IgnoreExtraProperties
public class User {
    private String mId; // for convenience
    private String mName;
    private String mEmail;
    private String mPhotoUrl;

    public User() {
    }

    public User(String id, String name, String email, String photoUrl) {
        mId = id;
        mName = name;
        mEmail = email;
        mPhotoUrl = photoUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }
    
}