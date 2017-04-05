package it.polito.mad.countonme;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.mad.countonme.Graphics.SimpleDividerItemDecoration;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.interfaces.OnListItemClickListener;
import it.polito.mad.countonme.lists.SharingActivitiesAdapter;
import it.polito.mad.countonme.models.SharingActivity;

public class SharingsListFragment extends Fragment implements ValueEventListener, OnListItemClickListener {
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
        DataManager.getsInstance().getSharingActivitiesReference().addValueEventListener( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.getsInstance().getSharingActivitiesReference().removeEventListener( this );
    }

    @Override
    public void onDataChange( DataSnapshot dataSnapshot ) {
        SharingActivity tmp;
        mSharActsList.clear();
        for ( DataSnapshot data : dataSnapshot.getChildren() ) {
            tmp = (SharingActivity) data.getValue( SharingActivity.class );
            tmp.setKey( (String) data.getKey() );
            mSharActsList.add( tmp );
        }
        mSharActsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onItemClick( Object clickedItem ) {
        SharingActivity activity = (SharingActivity) clickedItem;
        Toast.makeText( getActivity(), "ho selezionato " + activity.getName(), Toast.LENGTH_LONG ).show();

    }
}
