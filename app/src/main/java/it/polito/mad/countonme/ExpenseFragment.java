package it.polito.mad.countonme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.lists.UsersAdapter;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.User;

/**
 * Created by francescobruno on 04/04/17.
 */

public class ExpenseFragment extends BaseFragment implements DatabaseReference.CompletionListener,
        ValueEventListener {
    @BindView(R.id.expense_name) TextView mName;
    @BindView(R.id.expense_description) TextView mDescription;
    @BindView(R.id.expense_amount) TextView mAmount;
    @BindView(R.id.currency_spinner) Spinner mCurrency;
    @BindView(R.id.paidby_spinner) Spinner mPaidBySpinner;

    private UsersAdapter mUsersAdapter;
    private ArrayList<User> mShareActivityUsersList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);
        ButterKnife.bind(this, view);
        mShareActivityUsersList = new ArrayList<User>();
        mUsersAdapter = new UsersAdapter( getActivity(), mShareActivityUsersList );
        mPaidBySpinner.setAdapter( mUsersAdapter );
        return view;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user;
        mShareActivityUsersList.clear();
        for( DataSnapshot data: dataSnapshot.getChildren() ) {
            user = ( User ) data.getValue( User.class );
            mShareActivityUsersList.add( user );
        }
        mUsersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    // Methods

    private void saveNewExpense()
    {
        if(IsValid())
        {
            Expense newExpense = new Expense();
            newExpense.setName(mName.getText().toString());
            newExpense.setDescription(mDescription.getText().toString());
            newExpense.setAmount(Double.valueOf(mAmount.getText().toString()));
            newExpense.setExpenseCurrency(mCurrency.getSelectedItem().toString());
            newExpense.setPayer( (User) mPaidBySpinner.getSelectedItem() );
            try {
                DataManager.getsInstance().addNewExpense((String) getData(), newExpense, this);//fragment
            } catch (InvalidDataException ex) {
                Toast.makeText(getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean IsValid()
    {
        if(mName.getText().toString().isEmpty())
        {
            Toast.makeText( getActivity(), getResources().getString( R.string.err_name), Toast.LENGTH_SHORT ).show();
            return false;
        }

        if(mDescription.getText().toString().isEmpty())
        {
            Toast.makeText( getActivity(), getResources().getString( R.string.err_description), Toast.LENGTH_SHORT ).show();
            return false;
        }

        if(mAmount.getText().toString().isEmpty())
        {
            Toast.makeText( getActivity(), getResources().getString( R.string.err_amount), Toast.LENGTH_SHORT ).show();
            return false;
        }
        else
        {
            String strAmount = mAmount.getText().toString();
            Double Amount = Double.parseDouble(strAmount);

            if(Amount == 0)
            {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_amount_range), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void ClearForm()
    {
        mName.setText("");
        mDescription  .setText("");
        mCurrency.setSelection(0);
        mAmount.setText("");
    }

    //Events

    @Override
    public void onComplete( DatabaseError databaseError, DatabaseReference databaseReference) {
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

    //ActionBar

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

    private void adjustActionBar() {
        if( getData() instanceof String )
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_add_new_title );
        else
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_details_title );
        setHasOptionsMenu( true );
    }


    // create an action bar button

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.expense_menu, menu);
       // Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.anim_alpha);
       // animation.startNow();//@+id/share_sharing_activity

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_expense:
                saveNewExpense();
                return true;
            case R.id.share_sharing_activity:
                   //Animation animAlpha = AnimationUtils.loadAnimation(item.,R.anim.anim_alpha);
                try{
                    Intent sendIntent = LinkSharing.shareActivity(getActivity(),(String) getData());
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.select_app)));
                    return true;
                }catch(Exception e){
                    Toast.makeText(getActivity(), getResources().getString(R.string.lbl_error_sharing_link), Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
