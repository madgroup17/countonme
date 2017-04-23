package it.polito.mad.countonme;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;

/**
 * Created by Khatereh on 4/13/2017.
 */

public class SharingActivityFragment extends BaseFragment implements DatabaseReference.CompletionListener
{
    EditText txtName;
    EditText txtDescription;
    Spinner spnCurrency;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.sharing_activity_fragment, container, false);

        spnCurrency = (Spinner) view.findViewById(R.id.currency_spinner);
        txtName = (EditText) view.findViewById(R.id.sharing_activity_name);
        txtDescription = (EditText) view.findViewById(R.id.sharing_activity_description);

        return view;
    }

    /******************************************************************************************/

    /******************************************************************************************/
    // Methods

    private void saveNewActivity()
    {
        if(IsValid())
        {
            it.polito.mad.countonme.models.SharingActivity model = GetSharingActivity();

            try
            {
                DataManager.getsInstance().addNewSharingActivity( model, this);
            }
            catch ( InvalidDataException ex ) {
                Toast.makeText( getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private it.polito.mad.countonme.models.SharingActivity GetSharingActivity()
    {
        it.polito.mad.countonme.models.SharingActivity model = new it.polito.mad.countonme.models.SharingActivity();

        model.setName(txtName.getText().toString());
        model.setDescription(txtDescription.getText().toString());
        model.setCurrency(spnCurrency.getSelectedItem().toString());

        return model;
    }

    private boolean IsValid()
    {
        if(txtName.getText().toString().isEmpty())
        {
            Toast.makeText( getActivity(), getResources().getString( R.string.err_name), Toast.LENGTH_SHORT ).show();
            return false;
        }

        if(txtDescription.getText().toString().isEmpty())
        {
            Toast.makeText( getActivity(), getResources().getString( R.string.err_description), Toast.LENGTH_SHORT ).show();
            return false;
        }

        return true;
    }

    private void ClearForm()
    {
        txtName.setText("");
        txtDescription  .setText("");
        spnCurrency.setSelection(0);
    }

    /******************************************************************************************/

    /******************************************************************************************/
    //Events

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if( databaseError != null )
        {
            Toast.makeText(getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
        }
        else
        {
            ClearForm();
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), R.string.succ_save, Toast.LENGTH_SHORT).show();
        }
    }

    /******************************************************************************************/

    /******************************************************************************************/
    //ActionBar

    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        setHasOptionsMenu( false );
    }

    private void adjustActionBar() {
        //if( getData() instanceof String )
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.sharing_activity_add_new_title );
        //else
            //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.sharing_activity_details_title );
        setHasOptionsMenu( true );
    }

    /******************************************************************************************/

    /******************************************************************************************/
    // create an action bar button

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sharing_activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_sharing_activity:
                saveNewActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
