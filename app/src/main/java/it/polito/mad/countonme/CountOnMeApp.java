package it.polito.mad.countonme;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;

import it.polito.mad.countonme.models.User;

/**
 * Exended application class for data global sharing
 * Created by francescobruno on 29/03/17.
 */

public class CountOnMeApp extends MultiDexApplication
{
    private static User mCurrentUser;

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser( User currentUser ) {
        mCurrentUser = currentUser;
    }

}
