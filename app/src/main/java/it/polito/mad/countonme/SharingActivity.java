package it.polito.mad.countonme;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class SharingActivity extends Activity {
    private static final String TAG = SharingActivity.class.getName();
    private static final int NUM_OF_FRAGM = 1;

    private static enum AppFragment {
        SHARING_ACTIVITIES,
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
        mFragmentManager = getFragmentManager();
        loadBarTitles();
        loadAppFragments();
        showAppFragment( AppFragment.SHARING_ACTIVITIES );
    }

    public void showAppFragment( AppFragment fragment ) {
        if( fragment == mCurrentFragment ) return; // there is no need to change in this case
        mCurrentFragment = fragment;
        getActionBar().setTitle( mTitlesResIds[ mCurrentFragment.ordinal() ] );
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace( android.R.id.content, mFragmentsList[ mCurrentFragment.ordinal() ] ).commit();
    }

    private void loadAppFragments() {
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = new SharingsListFragment() ;
        // load of other fragments here
    }

    private void loadBarTitles() {
        mTitlesResIds[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = R.string.sharing_activities_title;
        // add other titles here
    }

}
