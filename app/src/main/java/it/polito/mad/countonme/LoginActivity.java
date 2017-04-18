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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.polito.mad.countonme.customviews.RequiredInputTextView;

/**
 * Created by francescobruno on 17/04/17.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.rtv_email) RequiredInputTextView mRtvEmail;
    @BindView(R.id.rtv_password) RequiredInputTextView mRtvPassword;

    @BindView(R.id.ed_email) EditText mEdEmail;
    @BindView(R.id.ed_password) EditText mEdPassword;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.login_layout );
        ButterKnife.bind( this );
        mProgressDialog = new ProgressDialog( this );
        mFirebaseAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(102, 187, 106)));
        if( mFirebaseAuth.getInstance().getCurrentUser() != null ) {
            finish();
            // the user is logged in so we will show the first application screen
            startActivity( new Intent( this, SharingActivity.class ) );
        }
    }

    @OnClick(R.id.btn_user_login)
    public void registerNewUser() {
        loginUser();
    }

    @OnClick(R.id.register_here)
    public void gotoRegistration() {
        finish();
        startActivity( new Intent( this, RegistrationActivity.class) );
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
                        if( task.isSuccessful() ) {
                            finish();
                            cleanLoginForm();
                            startActivity( new Intent( LoginActivity.this, SharingActivity.class ) ) ;
                        } else {
                            Toast.makeText( LoginActivity.this, R.string.lbl_login_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
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


