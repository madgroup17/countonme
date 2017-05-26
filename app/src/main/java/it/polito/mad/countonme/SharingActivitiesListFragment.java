package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.database.SharingActivityListLoader;
import it.polito.mad.countonme.interfaces.IActionReportBack;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.interfaces.IOnListItemClickListener;
import it.polito.mad.countonme.lists.SharingActivitiesAdapter;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.ReportBackAction;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;
import it.polito.mad.countonme.swiper.SwipeHelperExpenses;
import it.polito.mad.countonme.swiper.SwipeHelperSharingActivities;

public class SharingActivitiesListFragment extends BaseFragment implements ValueEventListener, IOnListItemClickListener, View.OnClickListener {
    private FloatingActionButton mActionButton;
    private RecyclerView mSharActsRv;
    private SharingActivitiesAdapter mSharActsAdapter;
    private List<SharingActivity> mSharActsList;
    public static ArrayList<SharingActivity>selection_list = new ArrayList<>();
    public static int counter;
    private SharingActivityListLoader mSharingActivityListLoader;
    public String userId;



    public static int getCounter(){
        return counter;
    }
    public static void setCounter(int counter){
        SharingActivitiesListFragment.counter=counter;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args=null;
        counter=0;
        final IOnListItemClickListener mListener=this;
        final SharingActivitiesListFragment shrActlf =this;
        if(savedInstanceState !=null){
            //setData(savedInstanceState.getString(AppConstants.SHARING_ACTIVITY_KEY));
            selection_list=(ArrayList<SharingActivity>)savedInstanceState.getSerializable(AppConstants.SELECTION_LIST_SHRACTIVITIES);
            counter = savedInstanceState.getInt(AppConstants.COUNTER_SHRACTIVITIES);
            updateCounter(counter);
        }

        //args=getArguments();
        if (((CountOnMeApp) getActivity().getApplication()).getCurrentUser() != null) {
            userId = ((CountOnMeApp) getActivity().getApplication()).getCurrentUser().getId();
        }
        mSharingActivityListLoader = new SharingActivityListLoader();
        mSharingActivityListLoader.setOnDataListener(new IOnDataListener() {
            @Override
            public void onData(Object data) {
                mSharActsList = (ArrayList<SharingActivity>)data;
                mSharActsAdapter = new SharingActivitiesAdapter(getActivity().getBaseContext(),mSharActsList,mListener,userId,shrActlf,selection_list);
                //(Context context, List<SharingActivity> data, IOnListItemClickListener listener, String currentUser ,SharingActivitiesListFragment sharingActivitiesListFragment)
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mSharActsRv.setLayoutManager(layoutManager);
                mSharActsRv.setAdapter(mSharActsAdapter);
                mSharActsRv.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
                mSharActsAdapter.notifyDataSetChanged();
            }
        });

        Intent intentback = getActivity().getIntent();
        if (intentback.getData() != null) {
            Activity parentActivity = getActivity();
            if (parentActivity instanceof IActionReportBack) {
                ((IActionReportBack) parentActivity).onAction(new ReportBackAction(ReportBackAction.ActionEnum.ACCEPT_REJECT_SA_FRAGMENT, intentback.getData()));
            }
        }
        View view = inflater.inflate(R.layout.sharing_activities_list_fragment, container, false);
        mActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mActionButton.setOnClickListener(this);
        mSharActsRv = (RecyclerView) view.findViewById(R.id.sharing_activities_list);
        mSharActsList = new ArrayList<SharingActivity>();
        mSharActsAdapter = new SharingActivitiesAdapter(getActivity().getBaseContext(),mSharActsList,mListener,userId,shrActlf,selection_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mSharActsRv.setLayoutManager(layoutManager);
        mSharActsRv.setAdapter(mSharActsAdapter);
        mSharActsRv.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
       // mSharActsAdapter = new SharingActivitiesAdapter(getActivity(), mSharActsList, this, userId,this);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        ItemTouchHelper.Callback callback = new SwipeHelperSharingActivities(mSharActsAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mSharActsRv);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(AppConstants.COUNTER_SHRACTIVITIES,counter);
        outState.putSerializable(AppConstants.SELECTION_LIST_SHRACTIVITIES,selection_list);
        Log.v("Test SALF","onSaveinstance");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCounter(counter);
        if(counter==0){
            selection_list.clear();
        }
        Bundle args = getArguments();
        //adjustActionBar();->esta no la tiene expeseslistfragment
        DataManager.getsInstance().getSharingActivitiesReference().addValueEventListener( this );
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity() ).showLoadingDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity()).hideLoadingDialog();
        DataManager.getsInstance().getSharingActivitiesReference().removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        FirebaseMessaging fbMessaging = FirebaseMessaging.getInstance();
        SharingActivity tmp;
        userId = ((CountOnMeApp )getActivity().getApplication()).getCurrentUser().getId();
        mSharActsList.clear();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            tmp = (SharingActivity) data.getValue(SharingActivity.class);
            for (Map.Entry<String, User> entry : tmp.getUsers().entrySet()) {
                if (entry.getKey().equals(getUserId()))//userId ) )
                    mSharActsList.add(tmp);
            }
        }
        mSharActsAdapter.notifyDataSetChanged();
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity()).hideLoadingDialog();
    }

    @Override
    public void onItemClick(Object clickedItem) {
        SharingActivity activity = (SharingActivity) clickedItem;
        Log.v("Test SALF","onItemClick "+activity.getKey());
        Activity parentActivity = getActivity();
        if (parentActivity instanceof IActionReportBack) {
            ((IActionReportBack) parentActivity).onAction(new ReportBackAction(ReportBackAction.ActionEnum.ACTION_VIEW_SHARING_ACTIVITY_DETAIL_TABS, ((SharingActivity) clickedItem).getKey()));
        }
    }

    @Override
    public void onClick(View v) {
        // we just have the floating action button to manage here
        Activity parentActivity = getActivity();
        if (parentActivity instanceof IActionReportBack) {
            ((IActionReportBack) parentActivity).onAction(new ReportBackAction(ReportBackAction.ActionEnum.ACTION_ADD_NEW_SHARING_ACTIVITY, null));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        ((it.polito.mad.countonme.CountOnMeActivity) getActivity()).hideLoadingDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void adjustActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.sharing_activities_title);
        setHasOptionsMenu( true );
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sharing_activity_menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_sharing_activity:
                Log.v("Test SALF","onoptionsItemSelected "+selection_list.size());
                mSharActsAdapter.updateAdapter(selection_list);
                Log.v("Test SALF","calling adjustActionbar "+selection_list.size());
                adjustActionBar();
                Log.v("Test SALF","setting counter=0 "+counter);
                counter=0;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /******************************************************************************************/

    public void prepareSelection(SharingActivity infoData){
        Log.v("Test SALF","prepareSelection "+infoData.getKey());
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

    private boolean checkexistence(SharingActivity infoData, ArrayList<SharingActivity> selection_list) {
        boolean foundit=false;
        for(SharingActivity aux:selection_list){
            String ToPrint = aux.getKey()+"-"+aux.getDescription()+"-"+aux.getName()+"-"+aux.getCurrency();
            Log.v("test: ",ToPrint);
        }
        if(infoData!=null && selection_list!=null) {
            for (SharingActivity shrAct : selection_list) {
                if (shrAct.getKey()!=null) {
                    if (shrAct.getKey().equals(infoData.getKey())) {
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
