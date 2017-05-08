package it.polito.mad.countonme.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.User;

/**
 * Created by Khatereh on 5/8/2017.
 */

public class ExpensesListLoader extends DataLoader {

    public ExpensesListLoader() {
        super();
    }

    public void loadExpensesList( String sharingActivityKey ) throws DataLoaderException {
        loadData( DataManager.getsInstance().getSharActExpensesReference( sharingActivityKey ) );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            expenseList.add( ( Expense ) data.getValue( Expense.class ) );
        }
        if( mListener != null )
            mListener.onData( expenseList );
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        if( mListener != null )
            mListener.onData( null );
    }

}
