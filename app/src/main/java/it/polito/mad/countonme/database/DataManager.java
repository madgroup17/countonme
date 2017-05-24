package it.polito.mad.countonme.database;

import android.net.UrlQuerySanitizer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private String expenseKey;

    public String getExpenseKey() {
        return expenseKey;
    }

    public void setExpenseKey(String expenseKey) {
        this.expenseKey = expenseKey;
    }

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


    public void updateSharingActivity( SharingActivity sharingActivity, DatabaseReference.CompletionListener completionListener ) throws  InvalidDataException {
        if( sharingActivity == null ) throw new InvalidDataException( "Invalid Sharing Activity has been provided" );
        DatabaseReference reference = mDatabase.getReference( CHILD_SHARING_ACTIVITIES );
        Map<String, Object> updates = new HashMap<>();
        updates.put( "/" + sharingActivity.getKey() + "/",  sharingActivity );
        reference.updateChildren( updates, completionListener);
    }

   /* public void addNewSharingActivity(SharingActivity activity, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        if( activity == null ) throw new InvalidDataException("Invalid Sharing Activity has been provided" );
        DatabaseReference reference = mDatabase.getReference();
        // create a new sharing activity and add the data in the user simultaneusly
        String shaActKey = reference.child(CHILD_SHARING_ACTIVITIES).push().getKey();
        String belongSaKey = reference.child( CHILD_USER_BELONGS_SHARING_ACTIVITIES + "/" + activity.getCreatedBy().getId()).push().getKey();
        Map<String, Object> updates = new HashMap<>();
        activity.setKey( shaActKey );
        updates.put( "/" + CHILD_SHARING_ACTIVITIES + "/" + shaActKey + "/", activity );
        updates.put( "/" + CHILD_USER_BELONGS_SHARING_ACTIVITIES + "/" + activity.getCreatedBy().getId() + "/" + belongSaKey + "/",  true );
        reference.updateChildren( updates, completionListener );

    }*/

    // Expenses management

    public void updateExpense( String parentKey, Expense expense, DatabaseReference.CompletionListener completionListener ) throws  InvalidDataException {
        if( parentKey == null || parentKey.length() == 0 || expense == null ) throw new InvalidDataException("Invalid data has been provided");
        DatabaseReference reference = mDatabase.getReference( CHILD_EXPENSES + "/" + parentKey );
        Map<String, Object> updates = new HashMap<>();
        updates.put( "/" + expense.getKey() + "/", expense );
        reference.updateChildren( updates, completionListener);
    }



    public String addNewExpense(String parentKey, Expense expense, DatabaseReference.CompletionListener completionListener)  throws InvalidDataException {
        if( parentKey == null || parentKey.length() == 0 || expense == null ) throw new InvalidDataException("Invalid data has been provided");
        DatabaseReference reference = mDatabase.getReference( CHILD_EXPENSES + "/" + parentKey );
        expenseKey = reference.push().getKey();
        expense.setKey( expenseKey );
        Map<String, Object> updates = new HashMap<>();
        updates.put( "/" + expenseKey + "/", expense );
        reference.updateChildren( updates, completionListener);
        return expenseKey;
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
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        return salist;
    }

    public void leaveSharingActivity(String activityKey, final String currentUser){
        if(activityKey!=null && currentUser!=null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(CHILD_SHARING_ACTIVITIES + "/" + activityKey + "/" + CHILD_USERS);
            Query leaveSharingActivityQuery = reference.orderByKey();
            leaveSharingActivityQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot usersAux : dataSnapshot.getChildren()) {
                        if (usersAux.getKey().equals(currentUser)) {
                            usersAux.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void deleteSharingActivity(final String activityKey) {
        if(activityKey!=null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(CHILD_SHARING_ACTIVITIES);
            Query removeSharingActivityQuery = reference.orderByKey().equalTo(activityKey);
            removeSharingActivityQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot sharingActivityAux : dataSnapshot.getChildren()) {
                        if (sharingActivityAux.getKey().equals(activityKey)) {
                            sharingActivityAux.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            final String namePhoto = "shareacts/" + activityKey + ".jpg";
            StorageReference riversRef = storageRef.child(namePhoto);
            riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }
    public void deleteExpenseBySharActivity(String SharingActivity) {
        if (SharingActivity != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(CHILD_EXPENSES );
            Query deleteAllExpenses = reference.orderByKey().equalTo( SharingActivity);
            deleteAllExpenses.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()){
                        expenseSnapshot.getRef().removeValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {  }
            });
        }
    }
    public void deleteExpense(String ParentSharingActivityKey, final String ExpenseKey){
        if(ParentSharingActivityKey!=null && ExpenseKey != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(CHILD_EXPENSES + "/" + ParentSharingActivityKey);//mDatabase.getReference( CHILD_EXPENSES + "/" + ParentSharingActivityKey + "/" + ExpenseKey );
            Query removeExpenseQuery = reference.orderByKey().equalTo(ExpenseKey);
            removeExpenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                        if (expenseSnapshot.getKey().equals(ExpenseKey)) {
                            expenseSnapshot.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            final String namePhoto = "expenses/" + ExpenseKey + ".jpg";
            StorageReference riversRef = storageRef.child(namePhoto);
            riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
    public static boolean checkExpRelofShaAct(String SharingActivity){
        boolean toReturn=false;
        if(SharingActivity!=null){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(CHILD_EXPENSES + "/" + SharingActivity);
            if(reference!=null){
                toReturn=true;
            }
        }
        return toReturn;
    }

    public static ArrayList<Expense> checkExpensesRelatedOfSharingActivity(String SharingActivity) {
        final ArrayList<Expense> listToReturn = new ArrayList<>();
        if (SharingActivity != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(CHILD_EXPENSES + "/" + SharingActivity);
            //TODO:get the wholelist of expeses envolved on this sharingactivity
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(CHILD_EXPENSES);
            Query getExpQuery = ref2.child(SharingActivity);
            Query getExpensesQuery = reference.orderByKey().equalTo(SharingActivity);
            getExpQuery.orderByValue().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Hello", "ello");
                    if (dataSnapshot.getValue() != null) {
                        Expense exp = dataSnapshot.getValue(Expense.class);
                        listToReturn.add(exp);
                    }else{
                        Log.d("Hello", "ello");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            getExpensesQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Hello", "ello");
                    if (dataSnapshot.getValue() != null) {
                        Expense exp = dataSnapshot.getValue(Expense.class);
                        listToReturn.add(exp);
                    }else{
                        Log.d("Hello", "ello");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
/*
            getExpensesQuery.addChildEventListener(new ChildEventListener() {
                                                       @Override
                                                       public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                       }

                                                       @Override
                                                       public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                           Log.d("Hello", "ello");
                                                           if (dataSnapshot.getValue() != null) {
                                                               Expense exp = dataSnapshot.getValue(Expense.class);
                                                               listToReturn.add(exp);
                                                           }else{
                                                               Log.d("Hello", "ello");
                                                           }
                                                       }

                                                       @Override
                                                       public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                           Log.d("Hello", "ello");
                                                           if (dataSnapshot.getValue() != null) {
                                                               Expense exp = dataSnapshot.getValue(Expense.class);
                                                               listToReturn.add(exp);
                                                           }else{
                                                               Log.d("Hello", "ello");
                                                           }
                                                       }

                                                       @Override
                                                       public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                       }

                                                       @Override
                                                       public void onCancelled(DatabaseError databaseError) {
                                                       }
                /*@Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Hello", "ello");
                    if (dataSnapshot.getValue() != null) {
                        Expense exp = dataSnapshot.getValue(Expense.class);
                        listToReturn.add(exp);
                    }else{
                        Log.d("Hello", "ello");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {  }
                */
  //      });
/*
            getExpQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("Hello", "ello");
                    if (dataSnapshot.getValue() != null) {
                        Expense exp = dataSnapshot.getValue(Expense.class);
                        listToReturn.add(exp);
                    }else{
                        Log.d("Hello", "ello");
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d("Hello", "ello");
                    if (dataSnapshot.getValue() != null) {
                        Expense exp = dataSnapshot.getValue(Expense.class);
                        listToReturn.add(exp);
                    }else{
                        Log.d("Hello", "ello");
                    }
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

                /*@Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Hello", "ello");
                    if (dataSnapshot.getValue() != null) {
                        Expense exp = dataSnapshot.getValue(Expense.class);
                        listToReturn.add(exp);
                    }
                    else{
                        Log.d("Hello", "ello");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }*/
  //          });


        if (listToReturn.isEmpty()) {
            return null;
        } else {
            return listToReturn;
        }
    }


}
