package it.polito.mad.countonme;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;

public class SharingActivity extends AppCompatActivity {
    private static final String TAG = SharingActivity.class.getName();

    private static enum AppFragment {
        SHARING_ACTIVITIES,
        EXPENSES,
        SHARING_DETAILS,
        EXPENSE_DETAILS,

        NUM_OF_FRAGMENTS
    };

    private AppFragment mCurrentFragment;
    private Fragment[] mFragmentsList = new Fragment[ AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
    private int[] mTitlesResIds = new int[ AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
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
        getSupportActionBar().setTitle( mTitlesResIds[ mCurrentFragment.ordinal() ] );
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace( android.R.id.content, mFragmentsList[ mCurrentFragment.ordinal() ] ).commit();
    }

    public void addNewSharingActivity( View view ) {
        it.polito.mad.countonme.models.SharingActivity prova = new it.polito.mad.countonme.models.SharingActivity(
                "My sharing activity", "This is a simple sharing activity used to test the code plm", "", "euro"
        );
        try {
            DataManager.getsInstance().addNewSharingActivity(prova, null);
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    private void loadAppFragments() {
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = new SharingsListFragment();
        mFragmentsList[ AppFragment.EXPENSES.ordinal() ]  = new ExpensesListFragment();
        mFragmentsList[ AppFragment.SHARING_DETAILS.ordinal() ] = null;
        mFragmentsList[ AppFragment.EXPENSE_DETAILS.ordinal() ] = new ExpenseFragment();
        // load of other fragments here
    }

    private void loadBarTitles() {
        mTitlesResIds[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = R.string.sharing_activities_title;
        mTitlesResIds[ AppFragment.EXPENSES.ordinal() ] = R.string.expenses_title;
        mTitlesResIds[ AppFragment.SHARING_DETAILS.ordinal() ] = R.string.sharing_activity_details_title;
        mTitlesResIds[ AppFragment.EXPENSE_DETAILS.ordinal() ] = R.string.expense_details_title;
        // add other titles here
    }

}
