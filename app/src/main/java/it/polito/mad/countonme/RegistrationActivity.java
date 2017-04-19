package it.polito.mad.countonme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.polito.mad.countonme.customviews.RequiredInputTextView;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.models.User;

/**
 * Created by francescobruno on 17/04/17.
 */

public class RegistrationActivity extends AppCompatActivity
        implements DatabaseReference.CompletionListener{


    @BindView(R.id.rtv_user_name) RequiredInputTextView mRtvUserName;
    @BindView(R.id.rtv_email) RequiredInputTextView mRtvEmail;
    @BindView(R.id.rtv_password) RequiredInputTextView mRtvPassword;

    @BindView(R.id.ed_user_name) EditText mEdUserName;
    @BindView(R.id.ed_email) EditText mEdEmail;
    @BindView(R.id.ed_password) EditText mEdPassword;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.registration_layout );
        ButterKnife.bind( this );
        mProgressDialog = new ProgressDialog( this );
        mFirebaseAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(102, 187, 106)));
    }


    @OnClick(R.id.btn_user_reg)
    public void registerNewUser() {
        registerUser();
    }

    @OnClick(R.id.sign_in)
    public void gotoLogin() {
        finish();
        startActivity( new Intent( this, LoginActivity.class) );
    }


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mProgressDialog.dismiss();
        if( databaseError != null )
        {
            Toast.makeText( this, R.string.lbl_registration_error, Toast.LENGTH_SHORT).show();
            try {
                mFirebaseAuth.getCurrentUser().delete();
                mFirebaseAuth.signOut();
            } catch ( NullPointerException npex ) { /* ignored */ }
        }
        else
        {
            cleanRegistrationForm();
            finish();
            startActivity( new Intent( this, SharingActivity.class ) );
        }
    }

    private void registerUser() {
        final String username = mEdUserName.getText().toString().trim();
        final String email = mEdEmail.getText().toString().trim();
        final String password = mEdPassword.getText().toString().trim();

        if( checkData( username, email, password ) == false ) return;

        mProgressDialog.setTitle( R.string.lbl_registering_user );
        mProgressDialog.setMessage( getResources().getString( R.string.lbl_please_wait ) );
        mProgressDialog.show();

        mFirebaseAuth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ) {
                    User user = new User(task.getResult().getUser().getUid(), username, null );
                    try {
                        DataManager.getsInstance().addNewUser( user, RegistrationActivity.this);
                    } catch (InvalidDataException e) {
                        try {
                            mFirebaseAuth.getCurrentUser().delete();
                            mFirebaseAuth.signOut();
                        } catch ( NullPointerException npex ) { /* ignored */ }
                        Toast.makeText( RegistrationActivity.this, R.string.lbl_registration_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText( RegistrationActivity.this, R.string.lbl_registration_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private boolean checkData( String username, String email, String password )
    {
        boolean dataProvided = true;

        if( TextUtils.isEmpty( username ) )  {
            dataProvided = false;
            mRtvUserName.showError();
        } else {
            mRtvUserName.cleanError();
        }

        if( TextUtils.isEmpty( email) ) {
            dataProvided = false;
            mRtvEmail.showError();
        } else {
            mRtvEmail.cleanError();
        }

        if( TextUtils.isEmpty( password ) ) {
            mRtvPassword.showError();
        } else {
            mRtvPassword.cleanError();
        }

        return dataProvided;
    }

    private void cleanRegistrationForm()
    {
        mEdUserName.setText("");
        mEdEmail.setText("");
        mEdPassword.setText("");
    }

}
