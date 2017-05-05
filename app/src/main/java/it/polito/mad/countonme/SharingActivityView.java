package it.polito.mad.countonme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.countonme.business.LinkSharing;
import it.polito.mad.countonme.database.DataManager;

/**
 * Created by Khatereh on 4/28/2017.
 */

public class SharingActivityView extends BaseFragment implements ValueEventListener {
    EditText txtName;
    EditText txtDescription;
    Spinner spnCurrency;

    private String path;
    private DatabaseReference mDatabase;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sharing_activity_fragment, container, false);
        Bundle args = getArguments();
        if (args != null) setData(args.getString("sharingkey"));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DataManager.getsInstance().getSharingActivityReference((String) getData()).addListenerForSingleValueEvent(this);

        spnCurrency = (Spinner) view.findViewById(R.id.spin_sharing_activity_currency);
        txtName = (EditText) view.findViewById(R.id.ed_sharing_activity_name);
        txtDescription = (EditText) view.findViewById(R.id.ed_sharing_activity_description);

        return view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        it.polito.mad.countonme.models.SharingActivity myActivity = (it.polito.mad.countonme.models.SharingActivity) dataSnapshot.getValue(it.polito.mad.countonme.models.SharingActivity.class);
        txtName.setText(myActivity.getName());
        txtDescription.setText(myActivity.getDescription());
        SetCurrency(myActivity.getCurrency());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        setHasOptionsMenu(false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sharing_activity_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_sharing_activity:
                try {
                    Intent sendIntent = LinkSharing.shareActivity(getActivity(), (String) getData());
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.select_app)));
                    return true;
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.lbl_error_sharing_link), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void adjustActionBar()
    {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.sharing_activity_details_title );
        setHasOptionsMenu(true);
    }

    private void SetCurrency(String CurrencyValue) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCurrency.setAdapter(adapter);
        if (!CurrencyValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(CurrencyValue);
            spnCurrency.setSelection(spinnerPosition);
        }
    }
}
