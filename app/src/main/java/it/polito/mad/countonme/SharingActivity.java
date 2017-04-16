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

public class SharingActivity extends AppCompatActivity implements IActionReportBack, FirebaseAuth.AuthStateListener {
    private static final String TAG = SharingActivity.class.getName();

    private static enum AppFragment {
        LOGIN,
        SHARING_ACTIVITIES,
        EXPENSES,
        SHARING_DETAILS,
        EXPENSE_DETAILS,

        NUM_OF_FRAGMENTS
    };

    // Authorization management
    private FirebaseAuth mCountOnMeAuth;

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
        // get firebase authentication instance
        mCountOnMeAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCountOnMeAuth.addAuthStateListener( this );
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCountOnMeAuth.removeAuthStateListener( this );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SAVED", true);
    }

    public void showAppFragment( AppFragment fragment, boolean addToBackStack ) {
        if (fragment == mCurrentFragment) return; // there is no need to change in this case
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
            default:
                //Toast.makeText( this, getResources().getString( R.string.temp_not_implemeted_lbl), Toast.LENGTH_SHORT ).show();
                handleActionAddNewSharingActivity(action.getActionData());
                break;
        }
    }


    // from FirebaseAuth.AuthStateListener interface
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        /*FirebaseUser user = firebaseAuth.getCurrentUser();
        if( user == null ) {
            getFragmentManager().popBackStack();
            showAppFragment(AppFragment.LOGIN, false);
        }*/
    }

    /*    PRIVATE METHODS   */

    private void loadAppFragments() {
        //mFragmentsList[ AppFragment.LOGIN.ordinal() ] = new LoginFragment();
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = new SharingsListFragment();
        mFragmentsList[ AppFragment.EXPENSES.ordinal() ]  = new ExpensesListFragment();
        mFragmentsList[ AppFragment.SHARING_DETAILS.ordinal() ] = new SharingActivityFragment();
        mFragmentsList[ AppFragment.EXPENSE_DETAILS.ordinal() ] = new ExpenseFragment();
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

}
