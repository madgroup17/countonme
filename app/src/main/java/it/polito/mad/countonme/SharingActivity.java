package it.polito.mad.countonme;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class SharingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        SharingsListFragment sharingListFragment = new SharingsListFragment();
        fragmentTransaction.replace(android.R.id.content, sharingListFragment );
        fragmentTransaction.commit();

    }
}
