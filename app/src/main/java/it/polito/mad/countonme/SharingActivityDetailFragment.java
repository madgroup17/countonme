package it.polito.mad.countonme;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.countonme.interfaces.FragmentTabHost;

/**
 * Created by Khatereh on 4/27/2017.
 */

public class SharingActivityDetailFragment extends BaseFragment
{

    FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.sharing_activity_detail, container, false);

        mTabHost = (FragmentTabHost) view.findViewById(R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.container);

        // Add each tab
        mTabHost.addTab(mTabHost.newTabSpec("first").setIndicator("first"), ExpensesListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("second").setIndicator("second"), BalanceFragment.class, null);



        mTabHost.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTabHost.setCurrentTabByTag("second");
            }
        }, 5000);

        return view;

    }

}
