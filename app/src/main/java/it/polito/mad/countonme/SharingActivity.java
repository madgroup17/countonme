package it.polito.mad.countonme;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.models.ReportBackAction;

public class SharingActivity extends AppCompatActivity implements IActionReportBack {
    private static final String TAG = SharingActivity.class.getName();

    private static enum AppFragment {
        SHARING_ACTIVITIES,
        EXPENSES,
        SHARING_DETAILS,
        EXPENSE_DETAILS,
        BALANCE,
        NUM_OF_FRAGMENTS
    };

    private AppFragment mCurrentFragment;
    private BaseFragment[] mFragmentsList = new BaseFragment[AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
    private int[] mTitlesResIds = new int[ AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);
        mFragmentManager = getFragmentManager();
        loadAppFragments();
        if( savedInstanceState == null )
            showAppFragment(AppFragment.SHARING_ACTIVITIES, false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(102, 187, 106)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SAVED", true);
    }

    public void showAppFragment( AppFragment fragment, boolean addToBackStack ) {
        mCurrentFragment = fragment;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(android.R.id.content, mFragmentsList[mCurrentFragment.ordinal()]);
        if ( addToBackStack == true ) transaction.addToBackStack(fragment.name());
        transaction.commit();
    }

    @Override
    public void onAction(ReportBackAction action) {
        switch (action.getAction()) {
            case ACTION_VIEW_SHARING_ACTIVITIES_LIST:
                handleActionViewSharingActivitiesList( action.getActionData() );
                break;
            case ACTION_VIEW_EXPENSES_LIST:
                handleActionViewExpensesList( action.getActionData() );
                break;
            case ACTION_ADD_NEW_EXPENSE:
                handleActionAddNewExpense( action.getActionData() );
                break;
            case ACTION_VIEW_BALANCE:
                handleActionBALANCE(action.getActionData());
                break;
            default:
                //Toast.makeText( this, getResources().getString( R.string.temp_not_implemeted_lbl), Toast.LENGTH_SHORT ).show();
                handleActionAddNewSharingActivity(action.getActionData());
                break;
        }
    }

    /*    PRIVATE METHODS   */

    private void loadAppFragments() {
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = new SharingsListFragment();
        mFragmentsList[ AppFragment.EXPENSES.ordinal() ]  = new ExpensesListFragment();
        mFragmentsList[ AppFragment.SHARING_DETAILS.ordinal() ] = new SharingActivityFragment();
        mFragmentsList[ AppFragment.EXPENSE_DETAILS.ordinal() ] = new ExpenseFragment();
        mFragmentsList[ AppFragment.BALANCE.ordinal() ] = new BalanceFragment();
    }


    // Action handlers

    private void handleActionViewSharingActivitiesList( Object data ) {
        showAppFragment( AppFragment.SHARING_ACTIVITIES, false );
    }

    private void handleActionViewExpensesList( Object data )
    {
        mFragmentsList[ AppFragment.EXPENSES.ordinal() ].setData( (String) data );
        showAppFragment( AppFragment.EXPENSES, true );
    }

    private void handleActionAddNewExpense( Object data ) {
        mFragmentsList[ AppFragment.EXPENSE_DETAILS.ordinal() ].setData( (String) data );
        showAppFragment( AppFragment.EXPENSE_DETAILS, true );
    }

    private void handleActionAddNewSharingActivity( Object data ) {
        mFragmentsList[ AppFragment.SHARING_DETAILS.ordinal() ].setData( (String) data );
        showAppFragment( AppFragment.SHARING_DETAILS, true );
    }

    private void handleActionBALANCE( Object data ) {
        mFragmentsList[ AppFragment.BALANCE.ordinal() ].setData( (String) data );
        showAppFragment( AppFragment.BALANCE, true );
    }

}