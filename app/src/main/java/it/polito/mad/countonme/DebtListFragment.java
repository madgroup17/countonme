package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.business.Balance;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.lists.DebtAdapter;
import it.polito.mad.countonme.models.Debt;
import it.polito.mad.countonme.models.DebtValue;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.User;

/**
 * Created by Khatereh on 5/7/2017.
 */

public class DebtListFragment extends BaseFragment implements  ValueEventListener, IOnListItemClickListener
{
    private DatabaseReference mDatabase;

    List<Debt> DebtList;
    private List<Expense> ExpenseList;
    private Map<String, User> mUsers;

    private RecyclerView mDebtValueRv;
    private DebtAdapter mDebtValueAdapter;
    private List<DebtValue> mDebtValueList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.debt_list_fragment, container, false);

        Bundle args;
        if( savedInstanceState != null ) setData( savedInstanceState.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        else {
            args = getArguments();
            if(args != null ) setData( args.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DataManager.getsInstance().getSharingActivityReference((String) getData()).addListenerForSingleValueEvent(this);
        DataManager.getsInstance().getSharActExpensesReference((String) getData()).addValueEventListener(this);

        mDebtValueRv = (RecyclerView)view.findViewById(R.id.debt_list);
        mDebtValueList = new ArrayList<DebtValue>();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString( AppConstants.SHARING_ACTIVITY_KEY, (String) getData());
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        if(mUsers == null || mUsers.size()==0) {
            it.polito.mad.countonme.models.SharingActivity myActivity = (it.polito.mad.countonme.models.SharingActivity) dataSnapshot.getValue(it.polito.mad.countonme.models.SharingActivity.class);
            mUsers = myActivity.getUsers();
        }

        it.polito.mad.countonme.models.Expense tmp;
        ExpenseList = new ArrayList<Expense>();

        try {
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                //if(data.getValue() instanceof Expense)
                {
                    tmp = (it.polito.mad.countonme.models.Expense) data.getValue(Expense.class);
                    ExpenseList.add(tmp);
                }
            }
        }
        catch (Exception exp )
        {}
        finally {
            FillData();
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onItemClick( Object clickedItem ) {
        DebtValue model = (DebtValue) clickedItem;
        Activity parentActivity  = getActivity();
    }



    @Override
    public void onResume()
    {
        super.onResume();
        adjustActionBar();
        DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).addValueEventListener( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).removeEventListener( this );
    }

    private void adjustActionBar()
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.balance_title);
        setHasOptionsMenu(true);
    }

    /******************************************************************************************/

    /******************************************************************************************/
    private void FillData()
    {
        if(ExpenseList != null && mUsers != null && ExpenseList.size()!=0 && mUsers.size()!=0)
        {
            Balance BalanceClass = new Balance(ExpenseList, mUsers);
            DebtList = BalanceClass.getDebtList();

            mDebtValueList = BalanceClass.GetOwsList(DebtList);

            mDebtValueAdapter = new DebtAdapter(getActivity(),mDebtValueList,this);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mDebtValueRv.setLayoutManager(layoutManager);
            mDebtValueRv.setAdapter(mDebtValueAdapter);
            mDebtValueRv.addItemDecoration(new SimpleDividerItemDecoration( getActivity() ) );
        }
    }
}
