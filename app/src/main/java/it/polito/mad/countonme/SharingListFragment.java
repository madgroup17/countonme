package it.polito.mad.countonme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SharingListFragment extends Fragment {
    private RecyclerView sharingActivitiesRV;
    private RecyclerView.Adapter sharingActivitiesRVAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sharing_list_fragment2, container, false);

        sharingActivitiesRV = (RecyclerView) view.findViewById(R.id.sharing_activities_rv);
        sharingActivitiesRV.setHasFixedSize(true);
        ArrayList<String> list = new ArrayList<>();
        list.add("Hello1");
        list.add("Hello1");



        sharingActivitiesRVAdapter = new SharingListAdapter(list,getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        sharingActivitiesRV.setLayoutManager(layoutManager);
        sharingActivitiesRV.setAdapter(sharingActivitiesRVAdapter);
        return view;
    }

}
