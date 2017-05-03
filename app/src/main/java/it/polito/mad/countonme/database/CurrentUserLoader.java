package it.polito.mad.countonme.database;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.countonme.CountOnMeApp;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.models.User;

/**
 * Created by Khatereh on 4/28/2017.
 */

public class CurrentUserLoader implements ValueEventListener {
    private CountOnMeApp mApp;

    public CurrentUserLoader( CountOnMeApp app, String userKey ) {
        mApp = app;
        DataManager.getsInstance().getUserReference(userKey).addListenerForSingleValueEvent( this );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue( User.class );
        mApp.setCurrentUser( user );
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
