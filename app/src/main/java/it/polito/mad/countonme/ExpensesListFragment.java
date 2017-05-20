package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.lists.ExpenseAdapter;
import it.polito.mad.countonme.models.*;
import it.polito.mad.countonme.swiper.SwipeHelperExpenses;

/**
 * Fragment for expenses list visualization
 * Created by francescobruno on 04/04/17.
 */

public class ExpensesListFragment extends BaseFragment implements  View.OnClickListener, ValueEventListener, IOnListItemClickListener{//}, View.OnLongClickListener{
    private FloatingActionButton mActionButton;
    private RecyclerView mExpensesRv;
    private ExpenseAdapter mExpensesAdapter;
    private List<Expense> mExpensesList;
    public static ArrayList<Expense>selection_list = new ArrayList<>();
    static int counter;
    public Activity currentActivity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args;
        counter=0;
        if( savedInstanceState != null ) {
            setData(savedInstanceState.getString(AppConstants.SHARING_ACTIVITY_KEY));
            selection_list= (ArrayList<Expense>) savedInstanceState.getSerializable(AppConstants.SELECTION_LIST_EXPENSES);
            counter=savedInstanceState.getInt(AppConstants.COUNTER_EXPENSES);
            updateCounter(counter);

        }else {
            args = getArguments();
            if(args != null ) setData( args.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        }
        View view = inflater.inflate(R.layout.expenses_list_fragment, container, false);
        mActionButton = ( FloatingActionButton ) view.findViewById( R.id.fabexp );
        mActionButton.setOnClickListener( this );
        mExpensesRv = (RecyclerView)view.findViewById(R.id.expenses_list);
        mExpensesList = new ArrayList<Expense>();
        mExpensesAdapter = new ExpenseAdapter(getActivity(),mExpensesList,this,this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mExpensesRv.setLayoutManager(layoutManager);
        mExpensesRv.setAdapter(mExpensesAdapter);
        mExpensesRv.addItemDecoration(new SimpleDividerItemDecoration( getActivity() ) );

        ItemTouchHelper.Callback callback = new SwipeHelperExpenses(mExpensesAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mExpensesRv);

        currentActivity=getActivity();
        //selection_list=null;
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, ( String ) getData() );
        outState.putInt(AppConstants.COUNTER_EXPENSES,counter);
        outState.putSerializable(AppConstants.SELECTION_LIST_EXPENSES,selection_list);
        //outState.putParcelableArrayList("selection_list", (ArrayList<? extends Parcelable>) selection_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCounter(counter);
        if(counter==0){
            selection_list.clear();
        }

        Bundle args = getArguments();
        if(args != null ) setData( args.getString( AppConstants.SHARING_ACTIVITY_KEY ) );

        DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).addValueEventListener( this );
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).showLoadingDialog();
        //counter=0;
        //
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).removeEventListener( this );
    }

    @Override
    public void onDataChange( DataSnapshot dataSnapshot ) {
        Double total = 0.0;
        it.polito.mad.countonme.models.Expense tmp;
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
            ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_ADD_NEW_EXPENSE, getData() ) );
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void adjustActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.expenses_title );
        setHasOptionsMenu( true );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.expense_menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_expense:
                mExpensesAdapter.updateAdapter(selection_list);
                adjustActionBar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /******************************************************************************************/

    public void prepareSelection(Expense infoData){
        boolean exist = checkexistence(infoData,selection_list);
        if(!exist){
            selection_list.add(infoData);
            counter++;
            updateCounter(counter);
        }else{
            if(counter==1) {
                selection_list.remove(infoData);
                selection_list.clear();
                counter--;
                adjustActionBar();
            }else{
                selection_list.remove(infoData);
                counter--;
                updateCounter(counter);
            }
        }
    }

    private boolean checkexistence(Expense infoData, ArrayList<Expense> selection_list) {
        boolean foundit=false;
        for(Expense exp :selection_list){
            if(exp.getKey().equals(infoData.getKey())){
                foundit=true;
            }
        }
        return foundit;
    }

    public void updateCounter(int counter){
        if(counter==0) {
            adjustActionBar();
            //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("0 item selected");// R.string.lbl_any_selected );
        }else{
            //String sToShow = String.format("" + counter + " " + R.string.lbl_item_selected); //gets a number ....
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(counter + " item selected");// sToShow );
        }
    }

}
