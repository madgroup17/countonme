package it.polito.mad.countonme;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.lists.SharingActivitiesAdapter;
import it.polito.mad.countonme.models.SharingActivity;

public class SharingsListFragment extends Fragment implements ValueEventListener {
    private RecyclerView mSharActsRv;
    private SharingActivitiesAdapter mSharActsAdapter;
    private List<SharingActivity> mSharActsList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sharing_list_fragment, container, false);
        mSharActsRv = ( RecyclerView ) view.findViewById( R.id.sharing_activity_list );
        mSharActsList = new ArrayList<SharingActivity>();
        mSharActsAdapter = new SharingActivitiesAdapter( getActivity(), mSharActsList );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getsInstance().getSharingActivitiesReference().addValueEventListener( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharingActivitiesReference().removeEventListener( this );
    }

    @Override
    public void onDataChange( DataSnapshot dataSnapshot ) {
        // create the data list here
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
