package it.polito.mad.countonme.models;

/**
 * The sharing activity data model
 * Created by francescobruno on 03/04/17.
 */

public class SharingActivity {
    private String mName;
    private String mDescription;
    private String mImageUrl;
    private String mCurrency;

    public SharingActivity() {
        this( null, null, null, null );
    }

    public SharingActivity( String name, String description, String imageUrl, String currency ) {
        mName = name;
        mDescription = description;
        mImageUrl = imageUrl;
        mCurrency = currency;
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

}
