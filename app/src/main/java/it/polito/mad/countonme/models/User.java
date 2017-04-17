package it.polito.mad.countonme.models;

/**
 * The user data model
 * Created by francescobruno on 17/04/17.
 */

public class User {
    private String mId;
    private String mName;
    private String mPhotoUrl;

    public User() {
        this( null, null, null );
    }

    public User( String id, String name, String photoUrl ) {
        mId         = id;
        mName       = name;
        mPhotoUrl   = photoUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }
}
