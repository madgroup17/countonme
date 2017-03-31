package it.polito.mad.countonme;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by Khatereh on 3/31/2017.
 */

public class BalanceActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        BalanceListFragment balanceListFragment = new BalanceListFragment();
        fragmentTransaction.replace(android.R.id.content, balanceListFragment );
        fragmentTransaction.commit();

    }
}
