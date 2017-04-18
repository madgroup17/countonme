package it.polito.mad.countonme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by francescobruno on 17/04/17.
 */

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.splash_screen_layout );
        if( FirebaseAuth.getInstance().getCurrentUser() == null ) {
            // the user is not logged in so we will show the login activity
            startActivity( new Intent( this, LoginActivity.class ) );
        }
        else {
            // the user is logged in so we will show the application first screen
            startActivity( new Intent( this, SharingActivity.class ) );
        }
        finish();
    }
}
