package it.polito.mad.countonme;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Khatereh on 3/31/2017.
 */

public class BalanceListFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.balance_list_fragment, container, false);
        return view;
    }
}
