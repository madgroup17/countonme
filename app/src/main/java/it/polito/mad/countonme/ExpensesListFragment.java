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
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.database.ExpensesListLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnDataListener;
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
    public static int counter;
    public Activity currentActivity;
    private ExpensesListLoader mExpensesListLoader;

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        ExpensesListFragment.counter = counter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = null;
        counter=0;
        final IOnListItemClickListener mListener = this;
        final ExpensesListFragment elf=this;
        if( savedInstanceState != null ) {
            Log.v("portrade","savedInstanceOnCreate");
            setData(savedInstanceState.getString(AppConstants.SHARING_ACTIVITY_KEY));
            selection_list= (ArrayList<Expense>) savedInstanceState.getSerializable(AppConstants.SELECTION_LIST_EXPENSES);
            counter=savedInstanceState.getInt(AppConstants.COUNTER_EXPENSES);
            updateCounter(counter);
        }
            args = getArguments();
            if (args != null) setData(args.getString(AppConstants.SHARING_ACTIVITY_KEY));

            //TODO Download Expenses List
            mExpensesListLoader = new ExpensesListLoader();
            mExpensesListLoader.setOnDataListener(new IOnDataListener() {
                @Override
                public void onData(Object data) {
                    mExpensesList = ( ArrayList<Expense> ) data;
                    mExpensesAdapter = new ExpenseAdapter(getActivity().getBaseContext(),mExpensesList,mListener,elf,selection_list);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    mExpensesRv.setLayoutManager(layoutManager);
                    mExpensesRv.setAdapter(mExpensesAdapter);
                    mExpensesRv.addItemDecoration(new SimpleDividerItemDecoration( getActivity() ) );
                    mExpensesAdapter.notifyDataSetChanged();
                }
            });
        try {
            mExpensesListLoader.loadExpensesList(args.getString( AppConstants.SHARING_ACTIVITY_KEY ));
        } catch (DataLoaderException e) {

        }
        Log.v("Test","Array "+selection_list.size());
        View view = inflater.inflate(R.layout.expenses_list_fragment, container, false);
        mActionButton = ( FloatingActionButton ) view.findViewById( R.id.fabexp );
        mActionButton.setOnClickListener( this );
        mExpensesRv = (RecyclerView)view.findViewById(R.id.expenses_list);
        mExpensesList = new ArrayList<Expense>();
        ItemTouchHelper.Callback callback = new SwipeHelperExpenses(mExpensesAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mExpensesRv);
        currentActivity=getActivity();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, ( String ) getData() );
        outState.putInt(AppConstants.COUNTER_EXPENSES,counter);
        outState.putSerializable(AppConstants.SELECTION_LIST_EXPENSES,selection_list);
        Log.v("portrade","onSaveinstance");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_expense:
                mExpensesAdapter.updateAdapter(selection_list);
                adjustActionBar();
                counter=0;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /******************************************************************************************/

    public void prepareSelection(Expense infoData){
        boolean exist = checkexistence(infoData,selection_list);
        Log.v("prepare selection: 1",""+counter);
        if(!exist){
            selection_list.add(infoData);
            Log.v("prepare selection: 2",""+counter);
            counter++;
            Log.v("prepare selection: 3",""+counter);
            updateCounter(counter);
        }else{
            if(counter==1) {
                selection_list.remove(infoData);
                selection_list.clear();
                Log.v("prepare selection: 4",""+counter);
                counter--;
                Log.v("prepare selection: 5",""+counter);
                adjustActionBar();
            }else{
                selection_list.remove(infoData);
                Log.v("prepare selection: 6",""+counter);
                counter--;
                Log.v("prepare selection: 7",""+counter);
                updateCounter(counter);
            }
        }
    }

    private boolean checkexistence(Expense infoData, ArrayList<Expense> selection_list) {
        boolean foundit=false;
        for(Expense aux:selection_list){
            String ToPrint = aux.getKey()+"-"+aux.getDescription()+"-"+aux.getName()+"-"+aux.getExpenseCurrency();
            Log.v("test: ",ToPrint);
        }
        if(infoData!=null && selection_list!=null) {
            for (Expense exp : selection_list) {
                if(exp!=null){
                    if (exp.getKey().equals(infoData.getKey())) {
                        foundit = true;
                    }
                }
            }
        }
        return foundit;
    }

    public void updateCounter(int counter){
        if(counter==0) {
            adjustActionBar();
        }else{
            //String sToShow = String.format("" + counter + " " + R.string.lbl_item_selected); //gets a number ....
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(counter + " item selected");// sToShow );
        }
    }

}
