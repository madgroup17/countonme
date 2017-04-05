package it.polito.mad.countonme;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.models.ReportBackAction;

public class SharingActivity extends AppCompatActivity implements IActionReportBack {
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
        showAppFragment( AppFragment.SHARING_ACTIVITIES, false );
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable( Color.rgb( 102, 187, 106 )) );
    }

    public void showAppFragment( AppFragment fragment, boolean addToBackStack ) {
        if( fragment == mCurrentFragment ) return; // there is no need to change in this case
        mCurrentFragment = fragment;
        getSupportActionBar().setTitle( mTitlesResIds[ mCurrentFragment.ordinal() ] );
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace( android.R.id.content, mFragmentsList[ mCurrentFragment.ordinal() ] );
        if( addToBackStack == true ) transaction.addToBackStack( fragment.name() );
        transaction.commit();
    }


    /*public void addNewSharingActivity( View view ) {
        it.polito.mad.countonme.models.SharingActivity prova = new it.polito.mad.countonme.models.SharingActivity(
                "My sharing activity", "This is a simple sharing activity used to test the code plm", "", "euro"
        );
        try {
            DataManager.getsInstance().addNewSharingActivity(prova, null);
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    } */

    @Override
    public void onAction(ReportBackAction action) {
        switch (action.getAction()) {
            case ACTION_VIEW_EXPENSES_LIST:
                handleActionViewExpensesList( action.getActionData() );
                break;
            default:
                Toast.makeText( this, getResources().getString( R.string.temp_not_implemeted_lbl), Toast.LENGTH_SHORT ).show();
                break;
        }
    }

    private void loadAppFragments() {
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = new SharingsListFragment();
        mFragmentManager.beginTransaction().add( mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ], "primo" ).commit();
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


    // Action handlers
    private void handleActionViewExpensesList( Object data )
    {
        String shActKey = (String) data;
        showAppFragment( AppFragment.EXPENSES, true );
    }

}
