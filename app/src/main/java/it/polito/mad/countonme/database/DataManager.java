package it.polito.mad.countonme.database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.SharingActivity;


/**
 * The interface with the firebase database
 * Created by francescobruno on 03/04/17.
 */

public class DataManager {
    private static final String CHILD_SHARING_ACTIVITY = "shareacts";
    private static final String CHILD_EXPENSE = "expenses";
    private static final String CHILD_USERS = "users";


    public static final DataManager sInstance = new DataManager();

    private FirebaseDatabase mDatabase;

    private  DataManager() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    public static DataManager getsInstance() {
        return sInstance;
    }


    // Sharing activities management

    public void addNewSharingActivity(SharingActivity activity, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        try {
            addNewData(activity, CHILD_SHARING_ACTIVITY, completionListener);
        } catch( InvalidDataException ex ) {
            throw new InvalidDataException("Invalid Sharing Activity has been provided" );
        }
    }


    // Expenses management

    public void addNewExpense(Expense expense, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        try {
            addNewData( expense, CHILD_EXPENSE, completionListener );
        } catch( InvalidDataException ex ) {
            throw new InvalidDataException("Invalid Expense has been provided" );
        }
    }


    private void addNewData( Object data, String key, DatabaseReference.CompletionListener listener ) throws InvalidDataException{
        if ( data == null || key == null ) {
            throw new InvalidDataException();
        }
        DatabaseReference reference = mDatabase.getReference( key );
        reference.push().setValue( data, listener );
    }


}
