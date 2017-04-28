package it.polito.mad.countonme;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnDrawerItemListener;
import it.polito.mad.countonme.models.ReportBackAction;

public class SharingActivity extends AppCompatActivity implements IActionReportBack, IOnDrawerItemListener {
    private static final String TAG = SharingActivity.class.getName();

    private static enum AppFragment {
        SHARING_ACTIVITIES,
        EXPENSES,
        SHARING_DETAILS,
        SHARING_ACTIVITY_DETAILS,  //tabs
        SHARING_VIEW,
        EXPENSE_DETAILS,
        BALANCE,
        NUM_OF_FRAGMENTS
    };

    private AppFragment mCurrentFragment;
    private BaseFragment[] mFragmentsList = new BaseFragment[AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
    private int[] mTitlesResIds = new int[ AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerFragment mDrawerFragment;
    private DrawerLayout mDrawerLayout;

    @BindView( R.id.toolbar ) Toolbar mToolbar;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);
        ButterKnife.bind( this );
        mFragmentManager = getFragmentManager();
        setUpDrawer();
        setUpActionBar();
        loadAppFragments();
        if( savedInstanceState == null )
            showAppFragment(AppFragment.SHARING_ACTIVITIES, false);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SAVED", true);
    }

    public void showAppFragment( AppFragment fragment, boolean addToBackStack ) {
        mCurrentFragment = fragment;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, mFragmentsList[mCurrentFragment.ordinal()]);
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
            case ACTION_VIEW_SHARING_ACTIVITY:
                handleActionSharingActivityDetail(action.getActionData());
                break;
            case ACTION_MODIFY_SHARING_ACTIVITY:
                handleActionSharingActivityView(action.getActionData());
                break;
            default:
                //Toast.makeText( this, getResources().getString( R.string.temp_not_implemeted_lbl), Toast.LENGTH_SHORT ).show();
                handleActionAddNewSharingActivity(action.getActionData());
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onDrawerItemClick(int itemId) {
        switch (itemId) {
            case R.id.menu_logout:
                doLogout();
                break;
            default:
                Toast.makeText( this, R.string.temp_not_implemeted_lbl, Toast.LENGTH_SHORT ).show();
        }
    }

    /*    PRIVATE METHODS   */

    private void loadAppFragments() {
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES.ordinal() ] = new SharingsListFragment();
        mFragmentsList[ AppFragment.EXPENSES.ordinal() ]  = new ExpensesListFragment();
        mFragmentsList[ AppFragment.SHARING_DETAILS.ordinal() ] = new SharingActivityFragment();
        mFragmentsList[ AppFragment.SHARING_ACTIVITY_DETAILS.ordinal() ] = new SharingActivityDetailFragment();
        mFragmentsList[ AppFragment.EXPENSE_DETAILS.ordinal() ] = new ExpenseFragment();
        mFragmentsList[ AppFragment.BALANCE.ordinal() ] = new BalanceFragment();
        mFragmentsList[ AppFragment.SHARING_VIEW.ordinal() ] = new SharingActivityView();

    }

    private void setUpDrawer() {
        mDrawerFragment = ( DrawerFragment ) getSupportFragmentManager().findFragmentById( R.id.nav_drawer_fragment );
        mDrawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
        mDrawerFragment.setUpDrawerItemListener( this );
    }

    private void setUpActionBar() {
        setTitle( null );
        mToolbar.setNavigationIcon( R.drawable.ic_drawer );
        setSupportActionBar( mToolbar );
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled( true );
        actionBar.setHomeButtonEnabled( true );

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
    }

    private void doLogout() {
        FirebaseAuth mFirebaseAuth  = FirebaseAuth.getInstance();
        mFirebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class ) );
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

    private void handleActionSharingActivityView( Object data ) {
        mFragmentsList[ AppFragment.SHARING_VIEW.ordinal() ].setData( (String) data );
        showAppFragment( AppFragment.SHARING_VIEW, true );
    }

    private void handleActionSharingActivityDetail( Object data ) {
        mFragmentsList[ AppFragment.SHARING_ACTIVITY_DETAILS.ordinal() ].setData( (String) data );
        mFragmentsList[ AppFragment.BALANCE.ordinal() ].setData( (String) data );
        mFragmentsList[ AppFragment.EXPENSES.ordinal() ].setData( (String) data );
        showAppFragment( AppFragment.SHARING_ACTIVITY_DETAILS, true );
    }

}