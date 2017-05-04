package it.polito.mad.countonme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

public class LoginActivity extends AppCompatActivity
implements DatabaseReference.CompletionListener {

    @BindView( R.id.toolbar ) Toolbar mToolbar;

    @BindView(R.id.rtv_email) RequiredInputTextView mRtvEmail;
    @BindView(R.id.rtv_password) RequiredInputTextView mRtvPassword;

    @BindView(R.id.ed_email) EditText mEdEmail;
    @BindView(R.id.ed_password) EditText mEdPassword;

    @BindView(R.id.login_button) LoginButton mFbLoginButton;



    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.login_layout );
        ButterKnife.bind( this );
        mFirebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar( mToolbar );
        setTitle( R.string.lbl_login );

        mProgressDialog = new ProgressDialog( this );
        mCallbackManager = CallbackManager.Factory.create();
        mFbLoginButton.setReadPermissions( "email", "public_profile" );

        mFbLoginButton.registerCallback( mCallbackManager, new FacebookCallback< LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, R.string.lbl_login_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btn_user_login)
    public void loginAppUser() {
        loginUser();
    }

    @OnClick(R.id.register_here)
    public void gotoRegistration() {
        finish();
        startActivity( new Intent( this, RegistrationActivity.class) );
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if( databaseError != null )
        {
            Toast.makeText( this, R.string.lbl_login_error, Toast.LENGTH_SHORT).show();
            try {
                mFirebaseAuth.getCurrentUser().delete();
                mFirebaseAuth.signOut();
            } catch ( NullPointerException npex ) { /* ignored */ }
        }
        else
        {
            finish();
            startActivity( new Intent( this, SharingActivity.class ) );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginUser() {
        String email = mEdEmail.getText().toString().trim();
        String password = mEdPassword.getText().toString().trim();

        if( checkData( email, password ) == false ) return;

        mProgressDialog.setTitle( R.string.lbl_logging_user );
        mProgressDialog.setMessage( getResources().getString( R.string.lbl_please_wait ) );
        mProgressDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            finish();
                            cleanLoginForm();
                            startActivity(new Intent(LoginActivity.this, SharingActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.lbl_login_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference ref = DataManager.getsInstance().getUserReference( task.getResult().getUser().getUid() );
                            ref.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if( dataSnapshot.getValue() == null ) {
                                                User user = new User( task.getResult().getUser().getUid(),
                                                        task.getResult().getUser().getDisplayName(),
                                                        task.getResult().getUser().getEmail(),
                                                        task.getResult().getUser().getPhotoUrl().toString() );
                                                try {
                                                    DataManager.getsInstance().addNewUser( user, LoginActivity.this );
                                                } catch (InvalidDataException e) {
                                                    try {
                                                        mFirebaseAuth.getCurrentUser().delete();
                                                        mFirebaseAuth.signOut();
                                                    } catch ( NullPointerException npex ) { /* ignored */ }
                                                    Toast.makeText( LoginActivity.this, R.string.lbl_login_error, Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                            finish();
                                            startActivity(new Intent(LoginActivity.this, SharingActivity.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.lbl_login_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private boolean checkData( String email, String password )
    {
        boolean dataProvided = true;

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

    private void cleanLoginForm()
    {
        mEdEmail.setText("");
        mEdPassword.setText("");
    }


}


