package it.polito.mad.countonme;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by LinaMaria on 31/03/2017.
 */

public class ExpenseListActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_list);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        ExpenseListFragment expenseListFragment = new ExpenseListFragment();
        fragmentTransaction.replace(android.R.id.content, expenseListFragment );
        fragmentTransaction.commit();

    }
}
