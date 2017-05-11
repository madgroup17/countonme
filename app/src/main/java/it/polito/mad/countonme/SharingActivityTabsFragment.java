package it.polito.mad.countonme;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import it.polito.mad.countonme.UI.FragmentTabHost;

/**
 * Created by Khatereh on 4/27/2017.
 */

public class SharingActivityTabsFragment extends BaseFragment
{
    private static final String TAB_TAG_SHARING_ACTIVITY_DETAILS = "Sharing_Activity_Details";
    private static final String TAB_TAG_EXPENSES_LIST            = "Expenses_List";
    private static final String TAB_TAG_BALANCE_DETAILS          = "Balance_Details";

    FragmentTabHost mTabHost;
    FragmentManager mChildFragmentManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.sharing_activity_details_tab_fragment, container, false);

        if( savedInstanceState != null) {
            setData( savedInstanceState.getString( AppConstants.SHARING_ACTIVITY_KEY ) );
        }
        mTabHost = (FragmentTabHost) view.findViewById(R.id.tabhost);
        mChildFragmentManager = getChildFragmentManager();
        mTabHost.setup(getActivity(), mChildFragmentManager, R.id.container);

        // Add each tab
        Bundle bundle = new Bundle();
        bundle.putString( AppConstants.SHARING_ACTIVITY_KEY, (String) getData() );
        mTabHost.addTab(mTabHost.newTabSpec( TAB_TAG_SHARING_ACTIVITY_DETAILS ).setIndicator( getResources().getString(R.string.sharing_activity_details_title) ), SharingActivityDetailsFragment.class, bundle);
        mTabHost.addTab(mTabHost.newTabSpec( TAB_TAG_EXPENSES_LIST ).setIndicator( getResources().getString(R.string.expenses_title) ), ExpensesListFragment.class, bundle);
        mTabHost.addTab(mTabHost.newTabSpec( TAB_TAG_BALANCE_DETAILS ).setIndicator( getResources().getString(R.string.balance_title) ), BalanceFragment.class, bundle);

        mTabHost.setCurrentTabByTag( TAB_TAG_SHARING_ACTIVITY_DETAILS );

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString( AppConstants.SHARING_ACTIVITY_KEY, (String) getData());
    }
}
