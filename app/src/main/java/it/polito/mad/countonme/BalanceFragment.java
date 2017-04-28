package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.lists.UsersAdapter;
import it.polito.mad.countonme.models.*;

/**
 * Created by Khatereh on 4/18/2017.
 */

public class BalanceFragment extends BaseFragment implements ValueEventListener
{
    BarChart barChart;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;
    private UsersAdapter mUsersAdapter;
    private ArrayList<User> mShareActivityUsersList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if( savedInstanceState != null ) setData( savedInstanceState.getString( AppConstants.SHARING_ACTIVITY_KEY ) );

        View view = inflater.inflate(R.layout.balance_fragment, container, false);

        mShareActivityUsersList = new ArrayList<User>();
        mUsersAdapter = new UsersAdapter( getActivity(), mShareActivityUsersList );

        barChart = (BarChart) view.findViewById(R.id.balance_chart);
        FillChart();

        return view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user;
        int position = 0;
        mShareActivityUsersList.clear();
        for( DataSnapshot data: dataSnapshot.getChildren() ) {
            user = ( User ) data.getValue( User.class );
            mShareActivityUsersList.add( user );
        }
        mUsersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).addValueEventListener( this );
    }

    private void adjustActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.balance_title );
        setHasOptionsMenu( true );
    }

    /******************************************************************************************/

    /******************************************************************************************/

    private  void  FillChart()
    {
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

        AddValuesToBARENTRY();
        AddValuesToBarEntryLabels();

        Bardataset = new BarDataSet(BARENTRY, "Members");
        BARDATA = new BarData(BarEntryLabels,Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(BARDATA);
        barChart.invalidate();
        barChart.animateY(3000);
        barChart.setDescription("Credits");
    }

    public void AddValuesToBARENTRY(){

        BARENTRY.add(new BarEntry(-2f, 0));
        BARENTRY.add(new BarEntry(4f, 1));
        BARENTRY.add(new BarEntry(6f, 2));
        BARENTRY.add(new BarEntry(8f, 3));
        BARENTRY.add(new BarEntry(7f, 4));
        BARENTRY.add(new BarEntry(3f, 5));
    }

    public void AddValuesToBarEntryLabels(){

        BarEntryLabels.add("User1");
        BarEntryLabels.add("User2");
        BarEntryLabels.add("User3");
        BarEntryLabels.add("User4");
        BarEntryLabels.add("User5");
        BarEntryLabels.add("User6");
    }


}
