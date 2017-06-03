package it.polito.mad.countonme.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by francescobruno on 09/05/17.
 */

public class Share implements Parcelable {

    private User mUser;
    private Double mAmount;

    public Share() {
        this( null, 0.0 );
    }

    public Share( User user, Double amount ) {
        mUser   = user;
        mAmount = amount;
    }

    public void setUser( User user ) { mUser = user; }

    public User getUser() { return mUser; }

    public void setAmount( Double amount ) { mAmount = amount; }

    public Double getAmount() { return mAmount; }


    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(mAmount);
        out.writeParcelable( mUser, 0 );
    }

    public static final Parcelable.Creator<Share> CREATOR
            = new Parcelable.Creator<Share>() {
        public Share createFromParcel(Parcel in) {
            return new Share(in);
        }

        public Share[] newArray(int size) {
            return new Share[size];
        }
    };

    private Share(Parcel in) {
        mAmount = in.readDouble();
        mUser = in.readParcelable( ClassLoader.getSystemClassLoader() );
    }

    @Override
    public int describeContents() {
        return 0;
    }




}
