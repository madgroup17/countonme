package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.models.Expense;

/**
 * Created by francescobruno on 04/04/17.
 */

public class ExpenseFragment extends BaseFragment implements DatabaseReference.CompletionListener {
    TextView mName,
             mDescription,
             mAmount;

    Spinner mCurrency;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);
        mName = (TextView) view.findViewById( R.id.expense_name );
        mDescription = (TextView)view.findViewById( R.id.expense_description);
        mAmount = (TextView)view.findViewById( R.id.expense_amount);
        mCurrency = (Spinner)view.findViewById( R.id.currency_spinner );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
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


    private void adjustActionBar() {
        if( getData() instanceof String )
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_add_new_title );
        else
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_details_title );
        setHasOptionsMenu( true );
    }


    private void saveNewExpense() {
        Expense newExpense = new Expense();
        newExpense.setName( mName.getText().toString() );
        newExpense.setDescription( mDescription.getText().toString() );
        newExpense.setAmount( Double.valueOf( mAmount.getText().toString() ) );
        newExpense.setExpenseCurrenty( mCurrency.getSelectedItem().toString() );
        try {
            DataManager.getsInstance().addNewExpense((String) getData(), newExpense, this);
        } catch ( InvalidDataException ex ) {
            Toast.makeText( getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onComplete( DatabaseError databaseError, DatabaseReference databaseReference) {
        if( databaseError != null )
            Toast.makeText( getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
        Toast.makeText( getActivity(), R.string.lbl_expense_saved, Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

}
