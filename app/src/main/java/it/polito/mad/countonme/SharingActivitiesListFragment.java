package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.lists.SharingActivitiesAdapter;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;

public class SharingActivitiesListFragment extends BaseFragment implements ValueEventListener, IOnListItemClickListener, View.OnClickListener {
    private RecyclerView mSharActsRv;
    private SharingActivitiesAdapter mSharActsAdapter;
    private List<SharingActivity> mSharActsList;
    private FloatingActionButton mActionButton;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Intent intentback = getActivity().getIntent();
        if(intentback.getData()!=null) {
            Activity parentActivity  = getActivity();
            if( parentActivity instanceof IActionReportBack ) {
                ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACCEPT_REJECT_SA_FRAGMENT,intentback.getData()));
            }
        }
        View view = inflater.inflate(R.layout.sharing_activities_list_fragment, container, false);

        mActionButton = ( FloatingActionButton ) view.findViewById( R.id.fab );
        mActionButton.setOnClickListener( this );

        mSharActsRv = ( RecyclerView ) view.findViewById( R.id.sharing_activities_list );
        mSharActsList = new ArrayList<SharingActivity>();
        mSharActsAdapter = new SharingActivitiesAdapter( getActivity(), mSharActsList, this );
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSharActsRv.setLayoutManager( layoutManager );
        mSharActsRv.setAdapter( mSharActsAdapter );
        mSharActsRv.addItemDecoration(new SimpleDividerItemDecoration( getActivity() ) );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        DataManager.getsInstance().getSharingActivitiesReference().addValueEventListener( this );
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).showLoadingDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).hideLoadingDialog();
        DataManager.getsInstance().getSharingActivitiesReference().removeEventListener( this );
    }

    @Override
    public void onDataChange( DataSnapshot dataSnapshot ) {
        SharingActivity tmp;
        String userId = ((CountOnMeApp )getActivity().getApplication()).getCurrentUser().getId();
        mSharActsList.clear();
        for ( DataSnapshot data : dataSnapshot.getChildren() ) {
            tmp = (SharingActivity) data.getValue( SharingActivity.class );
            for( Map.Entry<String, User> entry : tmp.getUsers().entrySet() ) {
                if( entry.getKey().equals( userId ) )
                    mSharActsList.add(tmp);
            }
        }
        mSharActsAdapter.notifyDataSetChanged();
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).hideLoadingDialog();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).hideLoadingDialog();
    }

    @Override
    public void onItemClick( Object clickedItem ) {
        SharingActivity activity = (SharingActivity) clickedItem;
        Activity parentActivity  = getActivity();
        if( parentActivity instanceof IActionReportBack ) {
            ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_VIEW_SHARING_ACTIVITY_DETAIL_TABS, ((SharingActivity) clickedItem).getKey()));
        }
    }

    @Override
    public void onClick(View v) {
        // we just have the floating action button to manage here
        Activity parentActivity  = getActivity();
        if( parentActivity instanceof IActionReportBack) {
            ((IActionReportBack) parentActivity).onAction( new ReportBackAction( ReportBackAction.ActionEnum.ACTION_ADD_NEW_SHARING_ACTIVITY, null) );
        }
    }

    private void adjustActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.sharing_activities_title );
    }


}
