package it.polito.mad.countonme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.models.*;

/**
 * Created by Khatereh on 4/28/2017.
 */

public class SharingActivityView extends BaseFragment implements ValueEventListener
{
    EditText txtName;
    EditText txtDescription;
    Spinner spnCurrency;

    private String path;
    private DatabaseReference mDatabase;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.sharing_activity_fragment, container, false);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        /*Intent intent = getActivity().getIntent();
        path = intent.getData().getPath();
        path = path.substring(1);*/
       DataManager.getsInstance().getSharingActivityReference((String) getData()).addListenerForSingleValueEvent(this);

        spnCurrency = (Spinner) view.findViewById(R.id.currency_spinner);
        txtName = (EditText) view.findViewById(R.id.sharing_activity_name);
        txtDescription = (EditText) view.findViewById(R.id.sharing_activity_description);

        return view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        it.polito.mad.countonme.models.SharingActivity myActivity = (it.polito.mad.countonme.models.SharingActivity) dataSnapshot.getValue(it.polito.mad.countonme.models.SharingActivity.class);
        //txtName.setText(myActivity.getName());
        //txtDescription.setText(myActivity.getDescription());
        //spnCurrency.setSe(myActivity.getCurrency());
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }
}
