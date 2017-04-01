package it.polito.mad.countonme;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SharingsListFragment extends ListFragment {
    private RecyclerView sharingActivitiesRV;
    private RecyclerView.Adapter sharingActivitiesRVAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sharing_list_fragment, container, false);


        return view;
    }
}

