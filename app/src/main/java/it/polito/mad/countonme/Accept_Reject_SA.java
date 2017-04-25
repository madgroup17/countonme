package it.polito.mad.countonme;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import it.polito.mad.countonme.business.retreiveData;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.models.*;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Created by LinaMaria on 22/04/2017.
 */

public class Accept_Reject_SA extends AppCompatActivity  implements  ValueEventListener {
    private FragmentManager mFragmentManager;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private TextView mNameSADetails;
    private TextView mDetailsSADetails;
    private TextView mCurrencySADetails;

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Here the data arrives
        Log.d("Hello", "ello"); // here are your data as you can see

        SharingActivity myAtivity = (SharingActivity) dataSnapshot.getValue( SharingActivity.class );
        mNameSADetails.setText(myAtivity.getName());
        mDetailsSADetails.setText(myAtivity.getDescription());
        mCurrencySADetails.setText(myAtivity.getCurrency());

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_reject_sa);
        mDatabase=FirebaseDatabase.getInstance().getReference();


        // I can assume that the sharing activty key arrives here as intent's data
        Intent intent = getIntent();
        path=intent.getData().getPath();
        path=path.substring(1);


        DataManager.getsInstance().getSharingActivityReference( path ).addListenerForSingleValueEvent( this );

        mNameSADetails = (TextView) findViewById(R.id.sharing_activity_name_sa);
        mDetailsSADetails = (TextView) findViewById(R.id.sharing_activity_description_sa);
        mCurrencySADetails =(TextView) findViewById(R.id.sharing_activity_currency_sa);

        SharingActivity model = new SharingActivity();
/*        if(savedInstanceState==null){
            retreiveData retreive = new retreiveData();
            model.setKey(path);
            retreive.retreiveSharingActivity(model,path);
            mNameSADetails.setText(model.getName());
        }*/

       final Button buttonAccept = (Button) findViewById(R.id.accept_sa);

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if( FirebaseAuth.getInstance().getCurrentUser() == null ) {
                    Toast.makeText(Accept_Reject_SA.this, "no logueado", Toast.LENGTH_LONG).show();
                    //finish();
                }
                else {
                    // the user is logged in so we will show the application first screen
                    Intent llave =  new Intent(Intent.ACTION_SEND);
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String mostrar= "   "+currentFirebaseUser.getUid()+"    "+ path;
                    Toast.makeText(Accept_Reject_SA.this,mostrar, Toast.LENGTH_LONG).show();
                }
            }
        });
        final Button buttonReject = (Button) findViewById(R.id.reject_sa);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast.makeText(Accept_Reject_SA.this,"click on reject",Toast.LENGTH_LONG).show();
            }
        });


       class AcceptRejectHolder extends RecyclerView.ViewHolder{ // what the holder is doing here????


            TextView mNameSADetails;
            TextView mDetailsSADetails;
            TextView mCurrencySADetails;


            public AcceptRejectHolder(View v) {
                super(v);
                mNameSADetails = (TextView) v.findViewById(R.id.sharing_activity_name_sa);
                mDetailsSADetails = (TextView) v.findViewById(R.id.sharing_activity_description_sa);
                mCurrencySADetails = (TextView) v.findViewById(R.id.sharing_activity_currency_sa);
            }
        }

    }

    public void onAction(ReportBackAction action) {
        switch (action.getAction()) {
            default:
                //Toast.makeText( this, getResources().getString( R.string.temp_not_implemeted_lbl), Toast.LENGTH_SHORT ).show();
                handleActionAcceptRejectSA(action.getActionData());
                break;
        }
    }
    private void handleActionAcceptRejectSA( Object data ) {
        //mFragmentsList[ SharingActivity.AppFragment.SHARING_DETAILS.ordinal() ].setData( (String) data );
       // showAppFragment( Accept_Reject_SA.AppFragment.SHARING_DETAILS, true );
    }
    /*public void showAppFragment(Accept_Reject_SA.AppFragment fragment, boolean addToBackStack ) {
        mCurrentFragmentSA = fragment;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(android.R.id.content, mFragmentsList[mCurrentFragmentSA.ordinal()]);
        if ( addToBackStack == true ) transaction.addToBackStack(fragment.name());
        transaction.commit();
    }*/
}


