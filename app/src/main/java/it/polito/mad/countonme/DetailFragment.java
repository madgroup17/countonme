package it.polito.mad.countonme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DetailFragment extends Fragment {
    private TabLayout tabLayout;
    //ViewPager
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        tabLayout= (TabLayout) view.findViewById(R.id.tab);
        //Put here the view pager
        //tabLayout.setupWithViewPager();
        /*
        tabLayout.getTabAt(0).setText("Detail");
        tabLayout.getTabAt(1).setText("Expense");
        tabLayout.getTabAt(2).setText("Balance");*/
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity().getBaseContext(),R.color.colorAccent));
        return view;
    }

}
