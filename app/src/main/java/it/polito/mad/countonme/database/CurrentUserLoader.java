package it.polito.mad.countonme.database;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.countonme.CountOnMeApp;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.models.User;

/**
 * Created by francescobruno on 03/05/2017.
 */

public class CurrentUserLoader extends DataLoader {

    public CurrentUserLoader() {
        super();
    }

    public void loadCurrentUser( String userKey ) throws DataLoaderException{
        loadData( DataManager.getsInstance().getUserReference( userKey) );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue( User.class );
        if( mListener != null )
            mListener.onData( user );
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        if( mListener != null )
            mListener.onData( null );
    }
}
