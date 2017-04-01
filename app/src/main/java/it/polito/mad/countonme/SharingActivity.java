package it.polito.mad.countonme;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SharingActivity extends AppCompatActivity {
    private static final String TAG = SharingActivity.class.getName();
    private static final int NUM_OF_FRAGM = 2;

    public static enum AppFragment {
        SHARING_ACTIVITIES,
        DETAIL,
        EXPENSES,
        SHARING_DETAILS,
        EXPENSE_DETAILS
    };

    private AppFragment mCurrentFragment;
    private Fragment[] mFragmentsList = new Fragment[ NUM_OF_FRAGM ];
    private int[] mTitlesResIds = new int[ NUM_OF_FRAGM ];
    private FragmentManager mFragmentManager;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        mFragmentManager = getSupportFragmentManager();
        loadBarTitles();
        loadAppFragments();
        showAppFragment( AppFragment.SHARING_ACTIVITIES );
    }

    public void showAppFragment( AppFragment fragment ) {
        if( fragment == mCurrentFragment ) return; // there is no need to change in this case
        mCurrentFragment = fragment;
        getSupportActionBar().setTitle( mTitlesResIds[ mCurrentFragment.ordinal() ] );
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        transaction.replace( android.R.id.content, mFragmentsList[ mCurrentFragment.ordinal() ] ).commit();
    }

    private void loadAppFragments() {
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = new SharingListFragment() ;
        mFragmentsList[ AppFragment.DETAIL.ordinal() ] = new DetailFragment() ;
        // load of other fragments here
    }

    private void loadBarTitles() {
        mTitlesResIds[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = R.string.sharing_activities_title;
        mTitlesResIds[ AppFragment.DETAIL.ordinal() ] = R.string.detail;
        // add other titles here
    }

}
