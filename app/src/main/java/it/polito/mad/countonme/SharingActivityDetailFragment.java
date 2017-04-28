package it.polito.mad.countonme;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

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

        String ExpenseTitle = getResources().getString(R.string.expenses_title);
        String BalanceTitle = getResources().getString(R.string.balance_title);
        String SharingActTitle = getResources().getString(R.string.sharing_activity_details_title);

        // Add each tab
        mTabHost.addTab(mTabHost.newTabSpec("SharingAct").setIndicator(SharingActTitle), SharingActivityView.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Expense").setIndicator(ExpenseTitle), ExpensesListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Balance").setIndicator(BalanceTitle), BalanceFragment.class, null);
        mTabHost.setCurrentTabByTag("SharingAct");

        /*mTabHost.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTabHost.setCurrentTabByTag("second");
            }
        }, 5000);*/

        return view;

    }



}
