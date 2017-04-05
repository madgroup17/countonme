package it.polito.mad.countonme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.lists.ExpenseAdapter;
import it.polito.mad.countonme.models.*;
import it.polito.mad.countonme.models.SharingActivity;

/**
 * Fragment for expenses list visualization
 * Created by francescobruno on 04/04/17.
 */

public class ExpensesListFragment extends Fragment implements  View.OnClickListener, ValueEventListener {
    private FloatingActionButton mActionButton;
    private RecyclerView mExpensesRv;
    private ExpenseAdapter mExpensesAdapter;
    private List<Expense> mExpensesList;
    private TextView mTotalBalance;



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
        mTotalBalance=(TextView)view.findViewById(R.id.total_balance);
        mExpensesRv = (RecyclerView)view.findViewById(R.id.expenses_list);
        mExpensesList = new ArrayList<Expense>();
        mExpensesAdapter = new ExpenseAdapter(getActivity(),mExpensesList,null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mExpensesRv.setLayoutManager(layoutManager);
        mExpensesRv.setAdapter(mExpensesAdapter);
        mExpensesRv.addItemDecoration(new SimpleDividerItemDecoration( getActivity() ) );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        DataManager.getsInstance().getSharActExpensesReference("-KgyKEU7hgUSKEKNiIpr").addValueEventListener( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharActExpensesReference("-KgyKEU7hgUSKEKNiIpr").removeEventListener( this );
    }

    @Override
    public void onDataChange( DataSnapshot dataSnapshot ) {
        Double total = 0.0;
        it.polito.mad.countonme.models.Expense tmp;
        mExpensesList.clear();
        for ( DataSnapshot data : dataSnapshot.getChildren() ) {
            tmp = (it.polito.mad.countonme.models.Expense) data.getValue( Expense.class );
            total+=tmp.getAmount();
            mExpensesList.add( tmp );
        }
        mExpensesAdapter.notifyDataSetChanged();
        mTotalBalance.setText(total.doubleValue()+"");
    }

    @Override
    public void onClick(View v) {
        // we just have the floating action button to manage here
        Activity parentActivity  = getActivity();
        if( parentActivity instanceof IActionReportBack) {
            ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_ADD_NEW_EXPENSE, null) );
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    private void adjustActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expenses_title );
    }
}
