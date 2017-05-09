package it.polito.mad.countonme.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The sharing activity data model
 * Created by francescobruno on 03/04/17.
 */

@IgnoreExtraProperties
public class SharingActivity {
    private String mName;
    private String mDescription;
    private String mImageUrl;
    private String mCurrency;
    private String mKey;
    private User mCreatedBy;
    private Map<String, User> mUsers = new HashMap<>();

    public SharingActivity() {
    }

    public SharingActivity( String name, String description, String imageUrl, String currency ) {
        mName = name;
        mDescription = description;
        mImageUrl = imageUrl;
        mCurrency = currency;
        mUsers = new HashMap<String, User>();
    }

    public void setName( String name ) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setDescription( String description ) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setCurrency( String currency ) {
        mCurrency = currency;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey( String key ) {
        mKey = key;
    }

    public Map<String, User> getUsers() { return mUsers; }

    public void setUsers( Map<String, User> users ) { mUsers = users; }

    public void setCreatedBy( User user ) { mCreatedBy = user; }

    public User getCreatedBy() { return mCreatedBy; }

    @Exclude
    public void addUser( User user ) {
        if( user == null ) return;
        mUsers.put( user.getId(), user );
    }
}
