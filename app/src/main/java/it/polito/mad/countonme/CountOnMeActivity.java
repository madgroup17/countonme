package it.polito.mad.countonme;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.polito.mad.countonme.database.CurrentUserLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.interfaces.IOnDrawerItemListener;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.User;

public class CountOnMeActivity extends AppCompatActivity implements IActionReportBack, IOnDrawerItemListener, IOnDataListener {

    private static final String TAG = CountOnMeActivity.class.getName();

    private static enum AppFragment {
        SHARING_ACTIVITIES_LIST_FRAGMENT,       // show the list of the sharing activities for the current user
        SHARING_ACTIVITY_TABS_FRAGMENT,         // show the details of a sharing activity
        SHARING_ACTIVITY_EDITING_FRAGMENT,      // show the UI for insert/modify a sharing activity
        SHARING_ACTIVITY_INVITATION_FRAGMENT,   // show the UI for accepting to partecipate to a sharing activity
        EXPENSE_DETAILS_FRAGMENT,               // show the details if an expense
        EXPENSE_EDITING_FRAGMENT,               // show the UI for insert/modify an expense
        EXPENSE_SURVEY_FRAGMENT,                // show the UI for an expense survey
        ACCEPT_REJECT_SA_FRAGMENT,              // show the UI for accept or reject
        NUM_OF_FRAGMENTS                        // NOTE: must be always the last entry
    };

    private AppFragment mCurrentFragment;
    private BaseFragment[] mFragmentsList = new BaseFragment[AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
    private int[] mTitlesResIds = new int[ AppFragment.NUM_OF_FRAGMENTS.ordinal() ];
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerFragment mDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mLoadingProgressDialog;
    private boolean mShowShalist;
    private boolean mIsLoadingUser;

    @BindView( R.id.toolbar ) Toolbar mToolbar;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initProgressDialog();
        // check whether the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if( currentUser == null ) {
            // no user logged in so we switch to the login activity
            finish();
            startActivity( new Intent(this, LoginActivity.class) );
        } else {
            // if there is no current user already loaded at application level it will be loaded
            if( ( (CountOnMeApp)getApplication() ).getCurrentUser() == null )
            {
                CurrentUserLoader userDataLoader = new CurrentUserLoader();
                userDataLoader.setOnDataListener( this );
                mIsLoadingUser = true;
                try {
                    showLoadingDialog();
                    userDataLoader.loadCurrentUser( currentUser.getUid() );
                } catch (DataLoaderException e) {
                    finish(); // can't load the user exit
                }
            }
        }
        mShowShalist = ( savedInstanceState == null);
        setContentView(R.layout.activity_sharing);
        ButterKnife.bind( this );
        mFragmentManager = getFragmentManager();
        setUpDrawer();
        setUpActionBar();
        loadAppFragments();

        if( mIsLoadingUser == false ) {
            if( mShowShalist == true ){
                Intent intentback = getIntent();
                if(intentback.getData()==null){
                    showAppFragment(AppFragment.SHARING_ACTIVITIES_LIST_FRAGMENT, false);
                }else{
                    //showAppFragment(AppFragment.ACCEPT_REJECT_SA_FRAGMENT,false);
                    handleActionAcceptRejectSAFragment(intentback.getData());
                }
            }
                manageCallingIntent();
        }
    }

    @Override
    public void onData(Object data) {
        hideLoadingDialog();
        ( (CountOnMeApp)getApplication() ).setCurrentUser( (User) data );
        if( mShowShalist );
            showAppFragment(AppFragment.SHARING_ACTIVITIES_LIST_FRAGMENT, false);
        manageCallingIntent();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean( AppConstants.SAVE_STATE_KEY_SAVED, true );
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
            case ACTION_VIEW_SHARING_ACTIVITY_DETAIL_TABS:
                handleActionViewSharingActivityDetailTabs( action.getActionData() );
                break;
            case ACTION_ADD_NEW_SHARING_ACTIVITY: // pass through
            case ACTION_EDIT_SHARING_ACTIVITY:
                handleActionEditSharingActivity( action.getActionData() );
                break;
            case ACTION_ACCEPT_SHARING_ACTIVITY:
                handleActionAcceptSharingActivity( action.getActionData() );
                break;
            case ACTION_VIEW_EXPENSE_DETAILS:
                handleActionViewExpenseDetails( action.getActionData() );
                break;
            case ACTION_ADD_NEW_EXPENSE:
                handleActionAddNewExpense( action.getActionData() );
                break;
            case ACTION_EDIT_EXPENSE:
                handleActionEditExpense( action.getActionData() );
                break;
            case ACTION_ACCEPT_EXPENSE_SURVEY:
                handleActionAcceptExpenseSurvey( action.getActionData() );
                break;
            case ACCEPT_REJECT_SA_FRAGMENT:
                handleActionAcceptRejectSAFragment(action.getActionData());
                break;
            default:
                Log.w( TAG, "Unknown/Not implemented action: " + action.getAction().name() );
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

    public void showLoadingDialog() {
        mLoadingProgressDialog.show();
    }

    public void hideLoadingDialog() {
        mLoadingProgressDialog.dismiss();
    }


    /* ---------------------  PRIVATE METHODS ------------------- */

    private void manageCallingIntent() {
        Intent callingIntent = getIntent();
        String key;
        if ((key = callingIntent.getStringExtra(AppConstants.EXPENSE_KEY)) != null)
            handleActionViewExpenseDetails(key);
    }

    private void loadAppFragments() {
        mFragmentsList[ AppFragment.SHARING_ACTIVITIES_LIST_FRAGMENT.ordinal() ] = new SharingActivitiesListFragment();
        mFragmentsList[ AppFragment.SHARING_ACTIVITY_TABS_FRAGMENT.ordinal() ] = new SharingActivityTabsFragment();
        mFragmentsList[ AppFragment.SHARING_ACTIVITY_EDITING_FRAGMENT.ordinal() ] = new SharingActivityEditingFragment();
        mFragmentsList[ AppFragment.SHARING_ACTIVITY_INVITATION_FRAGMENT.ordinal() ] = null; // TODO: add it if really needed otherwise remove all
        mFragmentsList[ AppFragment.EXPENSE_DETAILS_FRAGMENT.ordinal() ] = new ExpenseDetailsFragment();
        mFragmentsList[ AppFragment.EXPENSE_EDITING_FRAGMENT.ordinal() ] = new ExpenseEditingFragment();
        mFragmentsList[ AppFragment.EXPENSE_SURVEY_FRAGMENT.ordinal() ] = null; // TODO: add it if really needed otherwise remove all
        mFragmentsList[ AppFragment.ACCEPT_REJECT_SA_FRAGMENT.ordinal()] = new Accept_Reject_SA_Fragment();
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
        ( ( CountOnMeApp ) getApplication()).setCurrentUser( null );
        finish();
        startActivity(new Intent(this, LoginActivity.class ) );
    }

    private void initProgressDialog() {
        mLoadingProgressDialog = new ProgressDialog( this );
        mLoadingProgressDialog.setTitle( R.string.lbl_loading_data );
        mLoadingProgressDialog.setMessage( getResources().getString( R.string.lbl_please_wait ) );
    }


    // Action handlers

    /**
     * Shows the fragment containing the list of sharing activities for the current logged in user
     * @param data
     */
    private void handleActionViewSharingActivitiesList( Object data ) {
        showAppFragment( AppFragment.SHARING_ACTIVITIES_LIST_FRAGMENT, false );
    }

    /**
     * Shows the fragment containing all the details about a chosen sharing activity.
     * The fragment is the one with the three tabs.
     * @param data
     */
    private void  handleActionViewSharingActivityDetailTabs( Object data ) {
        mFragmentsList[ AppFragment.SHARING_ACTIVITY_TABS_FRAGMENT.ordinal()].setData( data );
        showAppFragment( AppFragment.SHARING_ACTIVITY_TABS_FRAGMENT, true );
    }

    /**
     * Shows the fragment for either add a new or modifying an existing sharing activity
     * @param data
     */
    private void handleActionEditSharingActivity( Object data ) {
        mFragmentsList[ AppFragment.SHARING_ACTIVITY_EDITING_FRAGMENT.ordinal()].setData( data );
        showAppFragment( AppFragment.SHARING_ACTIVITY_EDITING_FRAGMENT, true );
    }

    /**
     * Shows the fragment with all the information about the sharing activity the user is going
     * to join if he/she accepts
     * @param data
     */
    private void handleActionAcceptSharingActivity( Object data ) {
        Toast.makeText(this, R.string.temp_not_implemeted_lbl, Toast.LENGTH_SHORT).show();
    }


    /**
     * Shows the fragment containig the details about a chosen expense
     * @param data
     */
    private void handleActionViewExpenseDetails( Object data ) {
        mFragmentsList[ AppFragment.EXPENSE_DETAILS_FRAGMENT.ordinal() ].setData( data );
        showAppFragment( AppFragment.EXPENSE_DETAILS_FRAGMENT, true );
    }


    /**
     * Shows the fragment for adding a new expense
     * @param data
     */
    private void handleActionAddNewExpense( Object data ) {
        mFragmentsList[ AppFragment.EXPENSE_EDITING_FRAGMENT.ordinal() ].setData( AppConstants.SHARING_ACTIVITY_KEY, data );
        showAppFragment( AppFragment.EXPENSE_EDITING_FRAGMENT, true );
    }

    /**
     * Shows the fragment for modifying an existing expense
     * @param data
     */
    private void handleActionEditExpense( Object data ) {
        mFragmentsList[ AppFragment.EXPENSE_EDITING_FRAGMENT.ordinal() ].setData( AppConstants.EXPENSE_KEY, data );
        showAppFragment( AppFragment.EXPENSE_EDITING_FRAGMENT, true );
    }


    /**
     * Shows the fragment with all the information about the expense survey the user has
     * to either accept or refuse
     * @param data
     */
    private void handleActionAcceptExpenseSurvey( Object data ) {
        Toast.makeText(this, R.string.temp_not_implemeted_lbl, Toast.LENGTH_SHORT).show();
    }

    private void handleActionAcceptRejectSAFragment(Object actionData) {
        Intent intent = this.getIntent();
      String  path = intent.getData().getPath();
        path = path.substring(1);
        mFragmentsList[ AppFragment.ACCEPT_REJECT_SA_FRAGMENT.ordinal() ].setData( path );
        showAppFragment( AppFragment.ACCEPT_REJECT_SA_FRAGMENT, true );
    }



}