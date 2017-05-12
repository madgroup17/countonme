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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.models.User;

/**
 * Created by Khatereh on 4/13/2017.
 */

public class SharingActivityEditingFragment extends BaseFragment implements IOnDataListener, DatabaseReference.CompletionListener
{
    private static final String PICTURE_SELECTION_DIALOG = "Picture_Selection";
    private static final String SAVE_STATE_NEW_DATA = "New_Data";

    @BindView( R.id.img_sharing_activity_photo ) ImageView mIvPhoto;
    @BindView( R.id.ed_sharing_activity_name ) EditText txtName;
    @BindView( R.id.ed_sharing_activity_description) EditText txtDescription;
    @BindView( R.id.spin_sharing_activity_currency ) Spinner spnCurrency;

    private Unbinder mUnbinder;

    private boolean isAddNewSharing;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate(R.layout.sharing_activity_editing_fragment, container, false);
        mUnbinder = ButterKnife.bind( this, view );
        if( savedInstanceState != null ) {
            isAddNewSharing = savedInstanceState.getBoolean( SAVE_STATE_NEW_DATA );
        }

        isAddNewSharing  = ( getData() == null );

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick( R.id.img_sharing_activity_photo )
    public void onShareActivityPhotoClick() {
    }

    /******************************************************************************************/

    /******************************************************************************************/
    // Methods

    private void saveNewActivity()
    {
        if(IsValid())
        {
            it.polito.mad.countonme.models.SharingActivity shaAct = GetSharingActivity();

            try
            {
                DataManager.getsInstance().addNewSharingActivity( shaAct, this);
            }
            catch ( InvalidDataException ex ) {
                Toast.makeText( getActivity(), R.string.lbl_saving_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private it.polito.mad.countonme.models.SharingActivity GetSharingActivity()
    {
        it.polito.mad.countonme.models.SharingActivity shaAct = new it.polito.mad.countonme.models.SharingActivity();

        User currentUser = ((CountOnMeApp)getActivity().getApplication()).getCurrentUser();
        shaAct.setName( txtName.getText().toString() );
        shaAct.setDescription(txtDescription.getText().toString());
        shaAct.setCurrency(spnCurrency.getSelectedItem().toString());
        shaAct.setCreatedBy( currentUser );

        shaAct.addUser( currentUser );

        return shaAct;
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



    @Override
    public void onData(Object data) {

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
