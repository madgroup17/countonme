package it.polito.mad.countonme.database;

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

    public DatabaseReference getExpenseReference( String expenseKey) {
        return mDatabase.getReference( CHILD_EXPENSES + "/" + expenseKey );
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

    public void addNewExpense(Expense expense, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        try {
            addNewData( expense, CHILD_EXPENSES, completionListener );
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
