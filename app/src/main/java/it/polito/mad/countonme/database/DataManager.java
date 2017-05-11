package it.polito.mad.countonme.database;

import android.net.UrlQuerySanitizer;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private static final String CHILD_SHARING_ACTIVITIES_USERS = "users";
    private static final String CHILD_EXPENSES = "expenses";
    private static final String CHILD_USERS = "users";
    private static final String CHILD_USER_BELONGS_SHARING_ACTIVITIES = "belongsSharingActivities";

    ArrayList<SharingActivity> salist = new ArrayList<>();

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

    public DatabaseReference getSharingActivityUsersReference( String activityKey ) {
        return mDatabase.getReference( CHILD_SHARING_ACTIVITIES + "/" + activityKey + "/" + CHILD_SHARING_ACTIVITIES_USERS );
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

    public DatabaseReference getUserBelongsSharingActivitiesReference( String userKey ) {
        return mDatabase.getReference( CHILD_USER_BELONGS_SHARING_ACTIVITIES + "/" + userKey );
    }


    // Users management
    public void addNewUser( User user, DatabaseReference.CompletionListener completionListener ) throws InvalidDataException
    {
        try {
            addNewDataWithId( user, CHILD_USERS, user.getId(), completionListener );
        } catch( InvalidDataException ex ) {
            throw new InvalidDataException( "Invalid User has been provided" );
        }
    }

    // Sharing activities management

    public void addNewSharingActivity(SharingActivity activity, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        DatabaseReference reference = mDatabase.getReference();
        if( activity == null ) throw new InvalidDataException("Invalid Sharing Activity has been provided" );
        // create a new sharing activity and add the data in the user simultaneusly
        String shaActKey = reference.child(CHILD_SHARING_ACTIVITIES).push().getKey();
        String belongSaKey = reference.child( CHILD_USER_BELONGS_SHARING_ACTIVITIES + "/" + activity.getCreatedBy().getId()).push().getKey();
        Map<String, Object> updates = new HashMap<>();
        activity.setKey( shaActKey );
        updates.put( "/" + CHILD_SHARING_ACTIVITIES + "/" + shaActKey + "/", activity );
        updates.put( "/" + CHILD_USER_BELONGS_SHARING_ACTIVITIES + "/" + activity.getCreatedBy().getId() + "/" + belongSaKey + "/",  true );
        reference.updateChildren( updates, completionListener );

    }

    // Expenses management

    public void addNewExpense(String parentKey, Expense expense, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        if( parentKey == null || parentKey.length() == 0) throw new InvalidDataException("Invalid data has been provided");
        DatabaseReference reference = mDatabase.getReference( CHILD_EXPENSES + "/" + parentKey );
        String expenseKey = reference.push().getKey();
        expense.setKey( expenseKey );
        Map<String, Object> updates = new HashMap<>();
        updates.put( "/" + expenseKey + "/", expense );
        reference.updateChildren( updates, completionListener);
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

    //Lina:
    private void fetchData (DataSnapshot dataSnapshot){
        salist.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            SharingActivity salist = ds.getValue(SharingActivity.class);
            for(DataSnapshot ds1  : dataSnapshot.getChildren()) {
                User user = ds1.getValue(User.class);
                salist.addUser(user);
            }
        }
    }

    public ArrayList<SharingActivity> retrieve(){
        getDbReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return salist;
    }


}
