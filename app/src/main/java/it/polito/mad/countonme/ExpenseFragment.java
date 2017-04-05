package it.polito.mad.countonme;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by francescobruno on 04/04/17.
 */

public class ExpenseFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
    }

    private void adjustActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expense_details_title );
    }
}
