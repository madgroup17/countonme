package it.polito.mad.countonme.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Created by francescobruno on 10/05/17.
 */

public class ExpenseLoader extends DataLoader {

    public ExpenseLoader() { super(); }

    public void loadExpense( String shareActKey, String expenseKey ) throws DataLoaderException {
        loadData( DataManager.getsInstance().getExpenseReference( shareActKey, expenseKey ) );
    }

    @Override
    public void onDataChange( DataSnapshot dataSnapshot ) {
        Expense expense = dataSnapshot.getValue( Expense.class );
        if( mListener != null )
            mListener.onData( expense );
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        if( mListener != null )
            mListener.onData( null );
    }
}
