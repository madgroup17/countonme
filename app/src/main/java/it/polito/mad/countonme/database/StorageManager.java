package it.polito.mad.countonme.database;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by francescobruno on 17/05/17.
 */

public class StorageManager {

    private static final String STORAGE_SHAREACTS_FOLDER    = "shareacts";
    private static final String STORAGE_EXPENSES_FOLDER     = "expenses";

    private FirebaseStorage mStorage;

    private static final StorageManager sInstance = new StorageManager();

    private StorageManager() { mStorage = FirebaseStorage.getInstance(); }

    public static StorageManager getInstance() { return sInstance; }


    // Storage references getter

    public StorageReference getSharingActivitiesStorageReference( String id ) {
        if( id != null && id.length() != 0)
            return mStorage.getReference().child( STORAGE_SHAREACTS_FOLDER ).child( id );
        return null;
    }

    public StorageReference getExpensesStorageReference( String id ) {
        if( id != null && id.length() != 0)
            return mStorage.getReference().child( STORAGE_EXPENSES_FOLDER ).child( id );
        return null;
    }



}

