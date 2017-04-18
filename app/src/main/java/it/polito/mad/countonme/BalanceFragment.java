package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Khatereh on 4/18/2017.
 */

public class BalanceFragment extends BaseFragment
{
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.balance_fragment, container, false);


        return view;
    }


}
