package it.polito.mad.countonme;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LinaMaria on 31/03/2017.
 */

public class ExpenseListFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_list_fragment, container, false);
        return view;
    }
}
