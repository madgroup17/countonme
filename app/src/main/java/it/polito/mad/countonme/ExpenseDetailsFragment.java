package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExpenseDetailsFragment extends BaseFragment  {
    private TextView ExpenseDetailTV;
    public ExpenseDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_details_fragment, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.SHARING_ACTIVITY_KEY, ( String ) getData() );
    }

    @Override
    public void onResume() {
        super.onResume();
        //DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).addValueEventListener( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        //DataManager.getsInstance().getSharActExpensesReference( ( String ) getData() ).removeEventListener( this );
    }


}
