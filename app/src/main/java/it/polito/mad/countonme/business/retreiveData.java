package it.polito.mad.countonme.business;

import android.content.Intent;
import android.os.AsyncTask;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Created by LinaMaria on 25/04/2017.
 */
public class  retreiveData {

    public void retreiveSharingActivity(final SharingActivity model, String key){
        DatabaseReference ref = DataManager.getsInstance().getSharingActivityReference(key);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //dataSnapshot.getValue();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String name = (String) messageSnapshot.child("name").getValue();
                    //String message = (String) messageSnapshot.child("message").getValue();
                    model.setName(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String name = (String) messageSnapshot.child("name").getValue();
                    //String message = (String) messageSnapshot.child("message").getValue();
                    model.setName(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*retreiveSharingActivityTask task = new retreiveSharingActivityTask(key,model);
        task.execute(model);*/
    }
}

class retreiveSharingActivityTask extends AsyncTask<SharingActivity,Void,Void> {

    String key;
    SharingActivity model;

    public retreiveSharingActivityTask(String key, SharingActivity model) {
        this.key = key;
        this.model = model;
    }



    @Override
    protected Void doInBackground(SharingActivity... params) {
        DatabaseReference s = DataManager.getsInstance().getSharingActivityReference(key);


        s.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model=dataSnapshot.getValue(SharingActivity.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        s.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded (DataSnapshot dataSnapshot,String s1) {
                model=dataSnapshot.getValue(SharingActivity.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        return null;
    }
}