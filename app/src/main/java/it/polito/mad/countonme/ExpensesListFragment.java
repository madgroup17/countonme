package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.lists.ExpenseAdapter;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.ReportBackAction;

/**
 * Fragment for expenses list visualization
 * Created by francescobruno on 04/04/17.
 */

public class ExpensesListFragment extends BaseFragment implements  View.OnClickListener, ValueEventListener, IOnListItemClickListener {
    private FloatingActionButton mActionButton;
    private RecyclerView mExpensesRv;
    private ExpenseAdapter mExpensesAdapter;
    private List<Expense> mExpensesList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args;
        if( savedInstanceState != null ) setData( savedInstanceState.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        else {
            args = getArguments();
            if(args != null ) setData( args.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        }
        View view = inflater.inflate(R.layout.expenses_list_fragment, container, false);
        mActionButton = ( FloatingActionButton ) view.findViewById( R.id.fabexp );
        mActionButton.setOnClickListener( this );
        mExpensesRv = (RecyclerView)view.findViewById(R.id.expenses_list);
        mExpensesList = new ArrayList<Expense>();
        mExpensesAdapter = new ExpenseAdapter(getActivity(),mExpensesList,this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mExpensesRv.setLayoutManager(layoutManager);
        mExpensesRv.setAdapter(mExpensesAdapter);
        mExpensesRv.addItemDecoration(new SimpleDividerItemDecoration( getActivity() ) );

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, ( String ) getData() );
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if(args != null ) setData( args.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).addValueEventListener( this );
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).showLoadingDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).removeEventListener( this );
    }

    @Override
    public void onDataChange( DataSnapshot dataSnapshot ) {
        mExpensesList.clear();
        for ( DataSnapshot data : dataSnapshot.getChildren() ) {
            mExpensesList.add( (it.polito.mad.countonme.models.Expense) data.getValue( Expense.class ) );
        }
        mExpensesAdapter.notifyDataSetChanged();
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).hideLoadingDialog();
    }

    @Override
    public void onItemClick( Object clickedItem ) {
        Activity parentActivity  = getActivity();
        ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_EXPENSE_DETAILS, clickedItem ));

    }

    @Override
    public void onClick(View v) {
        // we just have the floating action button to manage here
        Activity parentActivity  = getActivity();
        if( parentActivity instanceof IActionReportBack) {
            Bundle bundle = new Bundle();
            bundle.putString( AppConstants.SHARING_ACTIVITY_KEY, (String) getData());
            bundle.putString(AppConstants.MODE,AppConstants.NEW_MODE);
            ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_ADD_NEW_EXPENSE, getData() ) );
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void adjustActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expenses_title );
        setHasOptionsMenu( true );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.expense_menu_list, menu);
    }

}
