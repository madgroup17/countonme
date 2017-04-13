package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.models.ReportBackAction;

/**
 * Created by francescobruno on 11/04/17.
 */

public class LoginFragment extends BaseFragment implements FirebaseAuth.AuthStateListener {
    private CallbackManager mCallbackManager;
    private FirebaseAuth mCountOnMeAuth;

    private LoginButton mLoginButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCountOnMeAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = ( LoginButton ) view.findViewById( R.id.login_button );
        mLoginButton.setReadPermissions( "email", "public_profile" );
        mLoginButton.setFragment( this );
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                // TODO: add on login error dialog
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCountOnMeAuth.addAuthStateListener( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        mCountOnMeAuth.removeAuthStateListener( this );
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult( requestCode, resultCode, data);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = mCountOnMeAuth.getCurrentUser();
        if( user != null ) {
            // TODO: Add the user into the database asking for the name to be used
            Activity parentActivity  = getActivity();
            if( parentActivity instanceof IActionReportBack) {
                ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_SHARING_ACTIVITIES_LIST, null) );
            }
        }
    }

    private void adjustActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.login_title );
    }


    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mCountOnMeAuth.signInWithCredential( credential )
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if ( ! task.isSuccessful() ) {
                            Toast.makeText( getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
