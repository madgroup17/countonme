package it.polito.mad.countonme.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Created by Khatereh on 5/8/2017.
 */

public class SharingActivityLoader extends DataLoader {

    public SharingActivityLoader() {
        super();
    }

    public void loadSharingActivity( String sharingActivityKey ) throws DataLoaderException {
        loadData( DataManager.getsInstance().getSharingActivityReference( sharingActivityKey ) );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        SharingActivity shAct = dataSnapshot.getValue( SharingActivity.class );
        if( mListener != null )
            mListener.onData( shAct );
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        if( mListener != null )
            mListener.onData( null );
    }
}
