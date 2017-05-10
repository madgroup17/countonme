package it.polito.mad.countonme;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.polito.mad.countonme.UI.DatePicker;
import it.polito.mad.countonme.customviews.RequiredInputTextView;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.database.ExpenseLoader;
import it.polito.mad.countonme.database.SharingActivityLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.lists.UsersAdapter;
import it.polito.mad.countonme.models.*;
import it.polito.mad.countonme.networking.ImageFromUrlTask;

/**
 * Created by francescobruno on 04/04/17.
 */

public class ExpenseEditingFragment extends BaseFragment implements DatabaseReference.CompletionListener,
       DatePickerDialog.OnDateSetListener, IOnDataListener {

    public static class ExpenseEditingData {

        private static final String KEY_NEW             = "Is_New";
        private static final String KEY_MONEY_TRANSFER  = "Is_Money_Transfer";
        private static final String KEY_SHARE_EVENLY    = "Is_Share_Evenly";
        private static final String KEY_SHAACTKEY       = "Share_Activity_Key";
        private static final String KEY_EXPKEY          = "Expense_Key";
        private static final String KEY_PAYER           = "Payer";
        private static final String KEY_DATE            = "Date";

        public Boolean isNew;
        public Boolean isMoneyTransfer;
        public Boolean isSharedEvenly;
        public String shaActKey;
        public String expKey;
        public String payerId;
        public Date expenseDate;

        public ExpenseEditingData() {
            isNew           = true;
            isMoneyTransfer = false;
            isSharedEvenly  = true;
            shaActKey       = null;
            expKey          = null;
            payerId         = null;
            expenseDate     = new Date();
        }

        public void saveInstance( Bundle outState ) {
            if( outState == null ) return;
            outState.putBoolean( KEY_NEW, isNew );
            outState.putBoolean( KEY_MONEY_TRANSFER, isMoneyTransfer );
            outState.putBoolean( KEY_SHARE_EVENLY, isSharedEvenly );
            outState.putString( KEY_SHAACTKEY, shaActKey);
            outState.putString( KEY_EXPKEY, expKey );
            outState.putString( KEY_PAYER, payerId );
            outState.putSerializable( KEY_DATE, expenseDate );
        }

        public void loadInstance( Bundle inState ) {
            if( inState == null ) return;
            isNew = inState.getBoolean( KEY_NEW );
            isMoneyTransfer = inState.getBoolean( KEY_MONEY_TRANSFER );
            isSharedEvenly = inState.getBoolean( KEY_SHARE_EVENLY );
            shaActKey = inState.getString( KEY_SHAACTKEY );
            expKey  = inState.getString( KEY_EXPKEY );
            payerId = inState.getString( KEY_PAYER );
            expenseDate = ( Date ) inState.getSerializable( KEY_DATE );
        }

    }


    private static final String DATE_PICKER_TAG = "date_picker";

    @BindView( R.id.rtv_expense_name )  RequiredInputTextView mRtvExpenseName;
    @BindView( R.id.rtv_expense_description ) RequiredInputTextView mRtvExpenseDescription;
    @BindView( R.id.rtv_expense_amount ) RequiredInputTextView mRtvExpenseAmount;
    @BindView( R.id.rtv_expense_currency ) RequiredInputTextView mRtvExpenseCurrency;
    @BindView( R.id.rtv_expense_payer ) RequiredInputTextView mRtvExpensePayer;

    @BindView( R.id.img_expense_photo ) ImageView mImage;
    @BindView( R.id.ed_expense_name ) EditText mName;
    @BindView( R.id.ed_expense_description ) EditText mDescription;
    @BindView( R.id.ed_expense_amount ) EditText mAmount;
    @BindView( R.id.spin_expense_currency ) Spinner mCurrency;
    @BindView( R.id.spin_expense_paidby ) Spinner mPaidBySpinner;
    @BindView( R.id.tv_expense_date ) TextView mTvDate;
    @BindView( R.id.sw_money_transfer ) Switch mSwMoneyTransfer;
    @BindView( R.id.sw_share_evenly ) Switch mSwShareEvenly;

    @BindView( R.id.ll_sharing_info) LinearLayout mLlSharingInfo;

    private Unbinder mUnbinder;

    private UsersAdapter mUsersAdapter;
    private SharingActivity mSharingActivity;
    private Expense mExpense;

    private ProgressDialog mProgressDialog;
    private DatePicker mDatePickerDialog;
    private DateFormat mDateFormat;

    private SharingActivityLoader mSharingActivityLoader;
    private ExpenseLoader mExpenseLoader;

    private ArrayList<User> mUsersList;

    private ExpenseEditingFragment.ExpenseEditingData eeData = new ExpenseEditingFragment.ExpenseEditingData();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_editing_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if( savedInstanceState == null ) {
            eeData.shaActKey = ( String ) getData( AppConstants.SHARING_ACTIVITY_KEY );
            eeData.expKey = (String) getData( AppConstants.EXPENSE_KEY );
            eeData.isNew  = (eeData.shaActKey != null );
        } else {
            eeData.loadInstance( savedInstanceState );
        }

        mUsersList = new ArrayList<User>();
        mUsersAdapter = new UsersAdapter( getActivity(), mUsersList );
        mPaidBySpinner.setAdapter( mUsersAdapter );

        mProgressDialog = new ProgressDialog( getActivity() );
        createDatePickerDialog();
        initializeViewContent();

        if( eeData.isNew ) {
            mSharingActivityLoader = new SharingActivityLoader();
            mSharingActivityLoader.setOnDataListener( this );
        } else {
            mExpenseLoader = new ExpenseLoader();
            mExpenseLoader.setOnDataListener( this );
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        try {
            if (eeData.isNew)
                mSharingActivityLoader.loadSharingActivity(eeData.shaActKey);
            else
                mExpenseLoader.loadExpense(eeData.shaActKey, eeData.expKey);
        } catch (DataLoaderException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        setHasOptionsMenu( false );
    }


    @Override
    public void onData( Object data ) {
        if( data instanceof SharingActivity )
            fillNewExpense( (SharingActivity ) data );
        else
            fillExistingExpense( ( Expense ) data );
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        eeData.saveInstance( outState );
    }

    @Override
    public void onComplete( DatabaseError databaseError, DatabaseReference databaseReference) {
        mProgressDialog.dismiss();
        if( databaseError != null )
        {
            Toast.makeText( getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
        }
        else
        {
            ClearForm();
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), R.string.lbl_expense_saved, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.expense_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_expense:
                saveNewExpense();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick( R.id.img_expense_photo )
    public void pickExpensePhoto() {
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( Intent.createChooser( intent, "Select Picture"), AppConstants.GET_IMAGE_REQUEST);
    }

    @OnClick( R.id.tv_expense_date )
    public void pickExpenseDate() {
        mDatePickerDialog.show( getFragmentManager(), DATE_PICKER_TAG );
    }

    @OnCheckedChanged( R.id.sw_money_transfer )
    public void moneyTransferChanged( boolean state ) {
        eeData.isMoneyTransfer = state;
    }

    @OnCheckedChanged( R.id.sw_share_evenly )
    public void shareEvenlyChanged( boolean state ) {
        eeData.isSharedEvenly = state;
        mLlSharingInfo.setVisibility( state ? View.GONE : View.VISIBLE  );
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int date) {
        updateDate( year, month, date );
        mTvDate.setText( mDateFormat.format( eeData.expenseDate ) );
        mDatePickerDialog.setDate( eeData.expenseDate );
    }

    /******************************************************/
    /*                 PRIVATE METHODS                    */
    /******************************************************/

    private void fillNewExpense( SharingActivity activity ) {

        if( activity == null ) return;
        User user;
        LayoutInflater inflater = LayoutInflater.from( getActivity() );
        mUsersList.clear();
        for(Map.Entry<String, User> entry : activity.getUsers().entrySet() ) {
            user = entry.getValue();
            mUsersList.add( user );
            // set the views for expenses sharing
            View child = inflater.inflate( R.layout.share_editing_item, null );
            ImageView userPhoto = ( ImageView ) child.findViewById( R.id.iv_user );
            TextView userName = (TextView) child.findViewById( R.id.tv_name );
            new ImageFromUrlTask( userPhoto, R.drawable.default_user_photo, true ).execute( user.getPhotoUrl() );
            userName.setText( user.getName() );
            child.setTag( R.id.id_user, user );
            mLlSharingInfo.addView( child );
        }
        mUsersAdapter.notifyDataSetChanged();

        initializeViewContent();
    }

    private void fillExistingExpense( Expense expense ) {
        if( expense == null ) return;
        initializeViewContent();
    }

    private void initializeViewContent() {
        mTvDate.setText( mDateFormat.format( eeData.expenseDate ) );
        mSwMoneyTransfer.setChecked( eeData.isMoneyTransfer );
        mSwShareEvenly.setChecked( eeData.isSharedEvenly );
        // select the chosen payer
        String payerId;
        if( eeData.payerId != null ) payerId = eeData.payerId;
        else payerId =  ((CountOnMeApp) getActivity().getApplication() ).getCurrentUser().getId();
        for( User user: mUsersList ) {
            if( user.getId().equals( payerId ) ) {
                mPaidBySpinner.setSelection(mUsersList.indexOf(user));
                break;
            }
        }

    }

    private void createDatePickerDialog() {
        mDateFormat = new SimpleDateFormat( getString( R.string.fmt_date ) );
        mDatePickerDialog = new DatePicker();
        mDatePickerDialog.setDateSetListener( this );
        mDatePickerDialog.setDate( eeData.expenseDate );
    }

    private void saveNewExpense()
    {
        //TODO need to distinguish between save and update
        if( checkData() )
        {
            Expense newExpense = new Expense();
            newExpense.setName(mName.getText().toString());
            newExpense.setDescription(mDescription.getText().toString());
            newExpense.setAmount(Double.valueOf(mAmount.getText().toString()));
            newExpense.setExpenseCurrency(mCurrency.getSelectedItem().toString());
            newExpense.setPayer( (User) mPaidBySpinner.getSelectedItem() );
            newExpense.setIsMoneyTransfer( eeData.isMoneyTransfer );
            newExpense.setIsSharedEvenly( eeData.isSharedEvenly );
            newExpense.setDate( eeData.expenseDate );
            newExpense.setParentSharingActivityId( eeData.shaActKey );

            if( eeData.isSharedEvenly == false ) {
                for (int idx = 0; idx < mLlSharingInfo.getChildCount(); idx++) {
                    View view = mLlSharingInfo.getChildAt(idx);
                    String strAmount  =  ( (EditText) view.findViewById( R.id.ed_amount ) ).getText().toString();
                    Double amount;
                    try {
                        amount = Double.parseDouble(strAmount);
                    } catch ( NumberFormatException e ) {
                        amount = 0.0;
                    }
                    User user = ( User ) view.getTag( R.id.id_user );
                    newExpense.addShare( user.getId(), new Share(user, amount ) );
                }
            }

            try {

                mProgressDialog.setTitle( R.string.lbl_saving_expense);
                mProgressDialog.setMessage( getResources().getString( R.string.lbl_please_wait ) );
                mProgressDialog.show();
                DataManager.getsInstance().addNewExpense(eeData.shaActKey, newExpense, this);//fragment

            } catch (InvalidDataException ex) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkData()
    {
        boolean dataProvided = true;

        if( TextUtils.isEmpty( mName.getText().toString() ) ) {
            dataProvided =  false;
            mRtvExpenseName.showError();
        } else {
            mRtvExpenseName.cleanError();
        }

        if( TextUtils.isEmpty( mDescription.getText().toString() ) ) {
            dataProvided = false;
            mRtvExpenseDescription.showError();
        } else {
            mRtvExpenseDescription.cleanError();
        }

        if( TextUtils.isEmpty( mAmount.getText().toString() ) ||
                Double.parseDouble( mAmount.getText().toString() ) <= 0.0001f ) {
            dataProvided = false;
            mRtvExpenseAmount.showError();
        } else {
            mRtvExpenseAmount.cleanError();
        }

        // TODO: add check for currency and payer

        return dataProvided;
    }

    private void ClearForm()
    {
        mName.setText( "" );
        mDescription.setText( "" );
        mAmount.setText( "" );
    }

    private void adjustActionBar() {
        if( eeData.isNew )
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_add_new_title );
        else
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_details_title );
        setHasOptionsMenu( true );
    }

    private void updateDate( int year, int month, int date ) {
        Calendar c = Calendar.getInstance();
        c.set( year, month, date );
        eeData.expenseDate.setTime( c.getTimeInMillis() );
    }
}
