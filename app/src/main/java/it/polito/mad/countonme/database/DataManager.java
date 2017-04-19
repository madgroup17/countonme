package it.polito.mad.countonme.database;

import android.net.UrlQuerySanitizer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;


/**
 * The interface with the firebase database
 * Created by francescobruno on 03/04/17.
 */

public class DataManager {
    private static final String CHILD_SHARING_ACTIVITIES = "shareacts";
    private static final String CHILD_EXPENSES = "expenses";
    private static final String CHILD_USERS = "users";


    public static final DataManager sInstance = new DataManager();

    private FirebaseDatabase mDatabase;

    private  DataManager() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    public static DataManager getsInstance() {
        return sInstance;
    }


    // References getter
    public DatabaseReference getDbReference() {
        return mDatabase.getReference();
    }

    public DatabaseReference getSharingActivitiesReference() {
        return mDatabase.getReference( CHILD_SHARING_ACTIVITIES );
    }

    public DatabaseReference getSharingActivityReference( String activityKey ) {
        return mDatabase.getReference( CHILD_SHARING_ACTIVITIES + "/" + activityKey );
    }

    public DatabaseReference getExpensesReference() {
        return mDatabase.getReference( CHILD_EXPENSES );
    }

    public DatabaseReference getSharActExpensesReference( String shActKey ) {
        return mDatabase.getReference( CHILD_EXPENSES + "/" + shActKey );
    }

    public DatabaseReference getExpenseReference( String shActKey, String expKey ) {
        return mDatabase.getReference( CHILD_EXPENSES + "/" + shActKey + "/" + expKey );
    }

    public DatabaseReference getUserReference( String userKey ) {
        return mDatabase.getReference( CHILD_USERS + "/" + userKey );
    }


    // Users management
    public void addNewUser(User user, DatabaseReference.CompletionListener completionListener ) throws InvalidDataException
    {
        try {
            addNewDataWithId( user, CHILD_USERS, user.getId(), completionListener );
        } catch( InvalidDataException ex ) {
            throw new InvalidDataException( "Invalid User has been provided" );
        }
    }

    // Sharing activities management

    public void addNewSharingActivity(SharingActivity activity, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        try {
            addNewData(activity, CHILD_SHARING_ACTIVITIES, completionListener);
        } catch( InvalidDataException ex ) {
            throw new InvalidDataException("Invalid Sharing Activity has been provided" );
        }
    }


    // Expenses management

    public void addNewExpense(String parentKey, Expense expense, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        try {
            if( parentKey == null || parentKey.length() == 0) throw new InvalidDataException();
            addNewData( expense, CHILD_EXPENSES + "/" + parentKey, completionListener );
        } catch( InvalidDataException ex ) {
            throw new InvalidDataException("addNewExpense: Invalid parameters has been provided" );
        }
    }


    private void addNewData( Object data, String key, DatabaseReference.CompletionListener listener ) throws InvalidDataException{
        if ( data == null || key == null ) {
            throw new InvalidDataException();
        }
        DatabaseReference reference = mDatabase.getReference( key );
        reference.push().setValue( data, listener );
    }

    private void addNewDataWithId( Object data, String key, String id, DatabaseReference.CompletionListener listener ) throws InvalidDataException{
        if ( data == null || key == null || id == null ) {
            throw new InvalidDataException();
        }
        DatabaseReference reference = mDatabase.getReference( key );
        reference.child( id ).setValue( data, listener );
    }


}
