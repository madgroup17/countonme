package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.polito.mad.countonme.business.Balance;
import it.polito.mad.countonme.database.ExpensesListLoader;
import it.polito.mad.countonme.database.SharingActivityLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.models.*;

/**
 * Created by Khatereh on 4/18/2017.
 */

public class BalanceFragment extends BaseFragment implements IOnDataListener
{

    private static NumberFormat mFormatter = new DecimalFormat("#0.00");

    BarChart barChart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;

    TextView tvMySpend;
    TextView tvMyCredit;
    TextView tvMyDebt;
    
    LinearLayout mOwesLayout;

    List<Debt> DebtList;

    private List<Expense> mExpenseList;
    private Map<String, User> mUsers;

    private SharingActivityLoader mSharingActivityLoader;
    private ExpensesListLoader mExpensesListLoader;

    private List<DebtValue> mDebtValueList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.balance_fragment, container, false);

        if (savedInstanceState != null)
            setData(savedInstanceState.getString(AppConstants.SHARING_ACTIVITY_KEY));
        else {
            Bundle args = getArguments();
            if (args != null) setData(args.getString(AppConstants.SHARING_ACTIVITY_KEY));
        }
        tvMySpend = (TextView) view.findViewById(R.id.my_spend);
        tvMyCredit = (TextView) view.findViewById(R.id.my_credit);
        tvMyDebt = (TextView) view.findViewById(R.id.my_debt);

        barChart = (BarChart) view.findViewById(R.id.balance_chart);
        
        mOwesLayout = (LinearLayout) view.findViewById( R.id.owes_container ); 

        mSharingActivityLoader = new SharingActivityLoader();
        mSharingActivityLoader.setOnDataListener( this );

        mExpensesListLoader = new ExpensesListLoader();
        mExpensesListLoader.setOnDataListener( this );


        return view;
    }

    @Override
    public void onData( Object data ) {
        if( data instanceof SharingActivity ) {
            mUsers = ( (SharingActivity) data ).getUsers();
            try {
                mExpensesListLoader.loadExpensesList( (String) getData() );
            } catch (DataLoaderException e) {
                e.printStackTrace();
            }
            catch (Exception exp)
            {}
        } else {
            mExpenseList = ( ArrayList<Expense> ) data;
            FillChart();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, (String) getData());
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        try {
            mSharingActivityLoader.loadSharingActivity( (String) getData() );
        } catch (DataLoaderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void adjustActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.balance_title);
        setHasOptionsMenu(true);
    }

    /******************************************************************************************/

    /******************************************************************************************/

    private void FillChart() {
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

        FillData();

        Bardataset = new BarDataSet(BARENTRY, "Members");
        BARDATA = new BarData(BarEntryLabels, Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(BARDATA);
        barChart.invalidate();
        barChart.animateY(3000);
        barChart.setDescription("Credits");

        BARDATA.notifyDataChanged();
    }

    private void FillData() {
        if ( mExpenseList != null && mUsers != null && mExpenseList.size() != 0 && mUsers.size() != 0) {
            Balance BalanceClass = new Balance(mExpenseList, mUsers);

            tvMySpend.setText(mFormatter.format((BalanceClass.GetMySpend())) + "");
            tvMyCredit.setText(mFormatter.format((BalanceClass.GetMyCredit())) + "");
            tvMyDebt.setText(mFormatter.format((BalanceClass.GetMyDept())) + "");

            DebtList = BalanceClass.getDebtList();
            AddDebtsToBARENTRY();

            mDebtValueList = BalanceClass.GetOwsList(DebtList);

            fillOwes();
        }
    }

    private void AddDebtsToBARENTRY() {
        int Index = 0;
        for (Iterator<Debt> i = DebtList.iterator(); i.hasNext(); ) {
            Debt item = i.next();
            BARENTRY.add(new BarEntry(item.getCredit().intValue(), Index));
            String Name = item.getUser().getName();
            if (Name == null) Name = item.getUser().getEmail();
            BarEntryLabels.add(Name);
            Index++;
        }
    }

    private void fillOwes() {
        LayoutInflater myInflater = LayoutInflater.from( getActivity() );
        
        for ( DebtValue debt: mDebtValueList ) {
            View child = myInflater.inflate(R.layout.debt_item, null);
            TextView tvDebter = (TextView) child.findViewById( R.id.debter_name);
            TextView tvAmout = (TextView) child.findViewById(R.id.owes_amount);
            TextView tvSpendName = (TextView) child.findViewById(R.id.spend_name);
           
            String Debter = "";
            if(debt.getDebterUser().getName()!=null)
                Debter = debt.getDebterUser().getName();
            else
                Debter = debt.getDebterUser().getEmail();

            String Spend = "";
            if(debt.getUser().getName()!=null)
                Spend = debt.getUser().getName();
            else
                Spend = debt.getUser().getEmail();

            tvDebter.setText(Debter);
            tvSpendName.setText(Spend);
            tvAmout.setText( mFormatter.format( debt.getAmount() ) + "" );
            
            mOwesLayout.addView( child );


        }
    }
}
