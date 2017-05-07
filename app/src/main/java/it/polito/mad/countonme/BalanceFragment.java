package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.lists.DebtAdapter;
import it.polito.mad.countonme.lists.ExpenseAdapter;
import it.polito.mad.countonme.lists.UsersAdapter;
import it.polito.mad.countonme.models.*;

/**
 * Created by Khatereh on 4/18/2017.
 */

public class BalanceFragment extends BaseFragment implements ValueEventListener {
    BarChart barChart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;

    TextView tvMySpend;
    TextView tvMyCredit;
    TextView tvMyDebt;

    List<Debt> DebtList;

    private DatabaseReference mDatabase;

    private List<Expense> ExpenseList;
    private Map<String, User> mUsers;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DataManager.getsInstance().getSharingActivityReference((String) getData()).addListenerForSingleValueEvent(this);
        DataManager.getsInstance().getSharActExpensesReference((String) getData()).addValueEventListener(this);

        tvMySpend = (TextView) view.findViewById(R.id.my_spend);
        tvMyCredit = (TextView) view.findViewById(R.id.my_credit);
        tvMyDebt = (TextView) view.findViewById(R.id.my_debt);

        barChart = (BarChart) view.findViewById(R.id.balance_chart);
        //FillChart();


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, (String) getData());
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (mUsers == null || mUsers.size() == 0) {
            it.polito.mad.countonme.models.SharingActivity myActivity = (it.polito.mad.countonme.models.SharingActivity) dataSnapshot.getValue(it.polito.mad.countonme.models.SharingActivity.class);
            if (myActivity != null)
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
        } catch (Exception exp) {
        } finally {
            FillChart();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        DataManager.getsInstance().getSharActExpensesReference((String) getData()).addValueEventListener(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharActExpensesReference((String) getData()).removeEventListener(this);
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

        //AddValuesToBARENTRY();
        //AddValuesToBarEntryLabels();

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
        if (ExpenseList != null && mUsers != null && ExpenseList.size() != 0 && mUsers.size() != 0) {
            Balance BalanceClass = new Balance(ExpenseList, mUsers);

            tvMySpend.setText(String.valueOf(BalanceClass.GetMySpend()));
            tvMyCredit.setText(String.valueOf(BalanceClass.GetMyCredit()));
            tvMyDebt.setText(String.valueOf(BalanceClass.GetMyDept()));

            DebtList = BalanceClass.getDebtList();
            AddDebtsToBARENTRY();
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

    private void AddValuesToBARENTRY() {

        BARENTRY.add(new BarEntry(-2f, 0));
        BARENTRY.add(new BarEntry(4f, 1));
        BARENTRY.add(new BarEntry(6f, 2));
        BARENTRY.add(new BarEntry(8f, 3));
        BARENTRY.add(new BarEntry(7f, 4));
        BARENTRY.add(new BarEntry(3f, 5));
    }

    private void AddValuesToBarEntryLabels() {

        BarEntryLabels.add("User1");
        BarEntryLabels.add("User2");
        BarEntryLabels.add("User3");
        BarEntryLabels.add("User4");
        BarEntryLabels.add("User5");
        BarEntryLabels.add("User6");
    }
}
