package it.polito.mad.countonme;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.mad.countonme.models.ReportBackAction;

/**
 * Created by LinaMaria on 22/04/2017.
 */

public class Accept_Reject_SA extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FirebaseAuth mFirebaseAuth;
    //private AppFragment mCurrentFragmentSA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_reject_sa);
        final Button buttonAccept = (Button) findViewById(R.id.accept_sa);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mFirebaseAuth= FirebaseAuth.getInstance();
                com.google.firebase.auth.FirebaseUser currentUser = mFirebaseAuth.getInstance().getCurrentUser();
                String mostrar = ""+currentUser.getDisplayName()+"   "+currentUser.getUid()+"   "+currentUser.getProviderData();
                Toast.makeText(Accept_Reject_SA.this, mostrar, Toast.LENGTH_LONG).show();
                //setContentView(R.layout.login_layout);


            }
        });
        final Button buttonReject = (Button) findViewById(R.id.reject_sa);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast.makeText(Accept_Reject_SA.this,"click on reject",Toast.LENGTH_LONG).show();
            }
        });

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
