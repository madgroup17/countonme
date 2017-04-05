package it.polito.mad.countonme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.models.*;

/**
 * Fragment for expenses list visualization
 * Created by francescobruno on 04/04/17.
 */

public class ExpensesListFragment extends Fragment implements View.OnClickListener {
    private FloatingActionButton mActionButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expenses_list_fragment, container, false);
        mActionButton = ( FloatingActionButton ) view.findViewById( R.id.fabexp );
        mActionButton.setOnClickListener( this );
        return view;
    }

    @Override
    public void onClick(View v) {
        // we just have the floating action button to manage here
        Activity parentActivity  = getActivity();
        if( parentActivity instanceof IActionReportBack) {
            ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_ADD_NEW_EXPENSE, null) );
        }
    }
}
