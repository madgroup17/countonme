package it.polito.mad.countonme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.polito.mad.countonme.customviews.RequiredInputTextView;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.lists.UsersAdapter;
import it.polito.mad.countonme.models.*;

/**
 * Created by francescobruno on 04/04/17.
 */

public class ExpenseFragment extends BaseFragment implements DatabaseReference.CompletionListener,
        ValueEventListener {

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


    private UsersAdapter mUsersAdapter;
    private ArrayList<User> mShareActivityUsersList;

    private ProgressDialog mProgressDialog;

    private String mSelectedPayer;
    //attributes for notification management:
    NotificationManager notificationManager;
   // boolean isNotificActive =false;
    int notifID=33;
    private String path;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);
        ButterKnife.bind(this, view);
        mSelectedPayer = null;
        if( savedInstanceState != null ) {
            setData( savedInstanceState.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
            mSelectedPayer = savedInstanceState.getString( AppConstants.USER_KEY );
        }
        mShareActivityUsersList = new ArrayList<User>();
        mUsersAdapter = new UsersAdapter( getActivity(), mShareActivityUsersList );
        mPaidBySpinner.setAdapter( mUsersAdapter );

        mProgressDialog = new ProgressDialog( getActivity() );

        return view;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user;
        int position = 0;
        mShareActivityUsersList.clear();
        for( DataSnapshot data: dataSnapshot.getChildren() ) {
            user = ( User ) data.getValue( User.class );
            mShareActivityUsersList.add( user );
            if( mSelectedPayer != null && mSelectedPayer.equals(user.getId() ) )
                position = mShareActivityUsersList.indexOf( user );
        }
        mUsersAdapter.notifyDataSetChanged();
        mPaidBySpinner.setSelection( position );
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, ( String ) getData() );
        outState.putString(AppConstants.USER_KEY, ( (User) mPaidBySpinner.getSelectedItem() ).getId() );
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
            sendNotificationFromNewExpense( null );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        DataManager.getsInstance()
                .getSharingActivityUsersReference( ( String ) getData() )
                .addListenerForSingleValueEvent( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        setHasOptionsMenu( false );
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


    /******************************************************/
    /*                 PRIVATE METHODS                    */
    /******************************************************/

    private void saveNewExpense()
    {
        if( checkData() )
        {
            Expense newExpense = new Expense();
            newExpense.setName(mName.getText().toString());
            newExpense.setDescription(mDescription.getText().toString());
            newExpense.setAmount(Double.valueOf(mAmount.getText().toString()));
            newExpense.setExpenseCurrency(mCurrency.getSelectedItem().toString());
            newExpense.setPayer( (User) mPaidBySpinner.getSelectedItem() );
            try {

                mProgressDialog.setTitle( R.string.lbl_saving_expense);
                mProgressDialog.setMessage( getResources().getString( R.string.lbl_please_wait ) );
                mProgressDialog.show();
                DataManager.getsInstance().addNewExpense((String) getData(), newExpense, this);//fragment

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
        if( getData() instanceof String )
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_add_new_title );
        else
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_details_title );
        setHasOptionsMenu( true );
    }

    private void sendNotificationFromNewExpense( String expenseKey ){
        Context context = getActivity().getApplicationContext();
        //getResources().getString( R.string.notificationTitle )
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle(getResources().getString( R.string.notificationTitle ))//("CountOnMe")//
                .setContentText(getResources().getString( R.string.notificationDetail ))//("New Expense Created")//
                .setTicker(getResources().getString( R.string.notificationDetail ))//("New Expense Created")
                .setSmallIcon(R.drawable.img_sharing_default);

        Intent moreInfoIntent = new Intent(context, SharingActivity.class);
        moreInfoIntent.putExtra("NOTIFICATION", true );

        moreInfoIntent.putExtra( "ExpenseKey", "pippo" );

        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(context);
        tStackBuilder.addParentStack(SharingActivity.class);
        tStackBuilder.addNextIntent(moreInfoIntent);
        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifID,notificationBuilder.build());

    }

}
