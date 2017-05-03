package it.polito.mad.countonme.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IOnDataListener;

/**
 * Created by francescobruno on 03/05/17.
 */

public abstract class DataLoader implements ValueEventListener {
    protected IOnDataListener mListener;

    public DataLoader() {

    }

    public void setOnDataListener(IOnDataListener listener ) {
        mListener = listener;
    }

    public void loadData( DatabaseReference dbReference ) throws DataLoaderException {
        try {
            dbReference.addListenerForSingleValueEvent( this );
        } catch( Exception ex ) {
            throw  new DataLoaderException( "Could not load data: " + ex.getMessage() );
        }
    }

}
