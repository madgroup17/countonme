package it.polito.mad.countonme.models;

import android.os.Parcel;
import android.os.Parcelable;

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
public class User implements Parcelable {
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

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User( Parcel in ) {
        mId = in.readString();
        mName = in.readString();
        mEmail = in.readString();
        mPhotoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString( mId );
        out.writeString( mName );
        out.writeString( mEmail );
        out.writeString( mPhotoUrl );
    }
}