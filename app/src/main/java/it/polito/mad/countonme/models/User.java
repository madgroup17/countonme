package it.polito.mad.countonme.models;

/**
 * The user data model
 * Created by francescobruno on 17/04/17.
 */

public class User {
    private String mId;
    private String mName;
    private String mEmail;
    private String mPhotoUrl;

    public User() {
        this( null, null, null, null );
    }

    public User( String id, String name, String email, String photoUrl ) {
        mId         = id;
        mName       = name;
        mEmail      = email;
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

    public String getEmail() { return mEmail; }

    public void setEmail( String email ) { mEmail = email; }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }
}
