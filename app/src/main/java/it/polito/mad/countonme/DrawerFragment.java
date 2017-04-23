package it.polito.mad.countonme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.polito.mad.countonme.data.NavigationDrawerData;
import it.polito.mad.countonme.interfaces.IOnDrawerItemListener;
import it.polito.mad.countonme.lists.DrawerAdapter;

public class DrawerFragment extends Fragment {

    @BindView(R.id.drawer_list) RecyclerView mRecyclerView;

    public DrawerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.drawer_layout, container, false );
        ButterKnife.bind( this, view );

        return( view );
    }

    public void setUpDrawerItemListener(IOnDrawerItemListener listener ) {
        setUpRecyclerView( listener );
    }

    private void setUpRecyclerView(IOnDrawerItemListener listener ) {
        DrawerAdapter adapter = new DrawerAdapter(getActivity(), NavigationDrawerData.getNavDrawerItemList(), listener );
        mRecyclerView.setAdapter( adapter );
        mRecyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
    }


}
