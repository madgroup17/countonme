package it.polito.mad.countonme.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Created by LinaMaria on 25/05/2017.
 */

public class SharingActivityListLoader extends DataLoader{

    public SharingActivityListLoader(){
        super();
    }
    public void loadSharingActivityList() throws DataLoaderException{
        loadData(DataManager.getsInstance().getSharingActivitiesReference());
    }
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<SharingActivity> sharingActivityList = new ArrayList<SharingActivity>();
        for(DataSnapshot data: dataSnapshot.getChildren()){
            sharingActivityList.add((SharingActivity)data.getValue(SharingActivity.class));
        }
        if(mListener!=null){
            mListener.onData(sharingActivityList);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        if(mListener!=null){
            mListener.onData(null);
        }
    }
}
