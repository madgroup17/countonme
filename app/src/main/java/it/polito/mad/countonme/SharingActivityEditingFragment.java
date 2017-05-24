package it.polito.mad.countonme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.polito.mad.countonme.UI.ErrorDialog;
import it.polito.mad.countonme.UI.ImageSourceDialog;
import it.polito.mad.countonme.customviews.RequiredInputTextView;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.messaging.MessagingManager;
import it.polito.mad.countonme.models.User;
import it.polito.mad.countonme.storage.StorageManager;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Khatereh on 4/13/2017.
 */

public class SharingActivityEditingFragment extends BaseFragment implements IOnDataListener,
        DatabaseReference.CompletionListener, ImageSourceDialog.IImageSourceDialogListener
{
    public static class SharingActEditingData {
        private static final String KEY_KEY = "Key";
        private static final String KEY_NEW = "Is_new";
        private static final String KEY_IMAGE = "Image";


        public Boolean isNew;
        public String key;
        public String imageUri;

        public SharingActEditingData() {
            isNew = true;
            key = null;
            imageUri = null;
        }

        public void saveInstance( Bundle outState ) {
            if( outState == null ) return;
            outState.putBoolean( KEY_NEW, isNew );
            outState.putString( KEY_KEY, key);
            outState.putString( KEY_IMAGE, imageUri );
        }

        public void loadInstance( Bundle inState ) {
            if (inState == null) return;
            isNew = inState.getBoolean(KEY_NEW);
            key = inState.getString( KEY_KEY );
            imageUri = inState.getString(KEY_IMAGE);
        }

        public void reset() {
            key = null;
            imageUri = null;
        }
    } // end of class SharingActEditingData


    private static final String ERROR_DIALOG_TAG      = "error_dialog";
    private static final String IMG_SOURCE_DIALOG_TAG = "image_source_dialog";

    private static final int RC_PHOTO_CAPTURE = 1;
    private static final int RC_PHOTO_REQUEST = 2;

    @BindView( R.id.img_sharing_activity_photo ) ImageView mIvPhoto;
    @BindView( R.id.ed_sharing_activity_name ) EditText mEdName;
    @BindView( R.id.ed_sharing_activity_description) EditText mEdDescription;
    @BindView( R.id.spin_sharing_activity_currency ) Spinner mSpnCurrency;

    @BindView( R.id.rtv_sharing_activity_name ) RequiredInputTextView mRtvSharingActivityName;
    @BindView( R.id.rtv_sharing_activity_description) RequiredInputTextView mRtvSharingActivitDescription;


    private Unbinder mUnbinder;

    private ProgressDialog mProgressDialog;
    private ErrorDialog mErrorDialog;
    private ImageSourceDialog mImgSourceDialog;

    private SharingActEditingData saeData = new SharingActEditingData();

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
           saeData.loadInstance( savedInstanceState );
        }
        else {
            saeData.isNew = (getData() == null);
            saeData.reset();
        }

        mProgressDialog = new ProgressDialog( getActivity() );
        mErrorDialog = new ErrorDialog();
        mImgSourceDialog = new ImageSourceDialog();

        // TODO: load the sharing activity data if is is not new
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillUserInteface();
        adjustActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearForm();
        setHasOptionsMenu( false );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick( R.id.img_sharing_activity_photo )
    public void onShareActivityPhotoClick() {
        mImgSourceDialog.show( getChildFragmentManager(), IMG_SOURCE_DIALOG_TAG );
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saeData.saveInstance( outState );
    }

    @Override
    public void onImageSourceSelected(int which) {
        Intent intent;
        if( mImgSourceDialog != null ) mImgSourceDialog.dismiss();
        if( which == ImageSourceDialog.TAKE_FROM_CAMERA ) {
            Uri mUriCapturedImage;
            intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
            File image = null;
            File imageDir = null;
            try {
                imageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                image = File.createTempFile( "sharing_activity_img", ".jpg", imageDir );
            } catch (IOException e) {
                Toast.makeText( getActivity(), R.string.lbl_camera_error, Toast.LENGTH_LONG).show();
                return;
            }
            if( image != null ) {
                mUriCapturedImage = FileProvider.getUriForFile(getActivity(), "it.polito.mad.countonme", image );
                saeData.imageUri = mUriCapturedImage.toString();
            } else {
                Toast.makeText( getActivity(), R.string.lbl_camera_error, Toast.LENGTH_LONG).show();
                return;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriCapturedImage);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, RC_PHOTO_CAPTURE);
            } else {
                Toast.makeText( getActivity(), R.string.lbl_camera_error, Toast.LENGTH_LONG).show();
            }
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType( "image/*" );
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult( Intent.createChooser( intent, getResources().getString( R.string.lbl_select_picture ) ), RC_PHOTO_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri = null;
        if( resultCode == RESULT_OK ) {
            if( requestCode == RC_PHOTO_REQUEST && data != null ) {
                imageUri = data.getData();
                saeData.imageUri = imageUri.toString();
            } else if( requestCode == RC_PHOTO_CAPTURE ) {
                imageUri = Uri.parse( saeData.imageUri );
            }
        }

        if( imageUri != null )
            Glide.with( mIvPhoto.getContext()).load( imageUri ).into( mIvPhoto );
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sharing_activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_sharing_activity:
                saveNewSharingActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onComplete( DatabaseError databaseError, DatabaseReference databaseReference) {
        mProgressDialog.dismiss();
        if( databaseError != null )
        {
            StorageReference strRef = StorageManager.getInstance().getSharingActivitiesStorageReference( saeData.key );
            strRef.delete();
            mErrorDialog.setDialogContent(R.string.lbl_error_could_not_save, R.string.lbl_error_please_try_again);
            mErrorDialog.show(getFragmentManager(), ERROR_DIALOG_TAG);
        }
        else
        {
            if(saeData.isNew) {
                try {
                    MessagingManager.getInstance().subscribeToSharingActivity(saeData.key);
                } catch (InvalidDataException e) {
                    e.printStackTrace();
                }
            }
            getFragmentManager().popBackStack();
        }
    }


    @Override
    public void onData(Object data) {

    }



    /* -------- PRIVATE METHODS ------------- */

    private void fillUserInteface() {
        // restore image
        if (saeData.imageUri != null)
            Glide.with(mIvPhoto.getContext()).load(Uri.parse(saeData.imageUri)).into(mIvPhoto);
    }


    private void saveNewSharingActivity() {
        closeSoftKeyboard();
        if( checkData() ) {
            mProgressDialog.setTitle( R.string.lbl_saving_sharing_activity );
            mProgressDialog.setMessage(getResources().getString(R.string.lbl_please_wait));
            mProgressDialog.show();
            // first get the new sharing activity Push Id
            DatabaseReference dbRef = DataManager.getsInstance().getSharingActivitiesReference();
            saeData.key = dbRef.push().getKey();
            // look whether there is an image to save
            if( saeData.imageUri != null )
                saveSharingActivityImage();
            else
                saveSharingActivityData( null );
        }

    }

    private boolean checkData() {
        boolean dataProvided = true;

        if( TextUtils.isEmpty( mEdName.getText().toString() ) ) {
            dataProvided =false;
            mRtvSharingActivityName.showError();
        } else {
            mRtvSharingActivityName.cleanError();
        }

        if( TextUtils.isEmpty( mEdDescription.getText().toString() ) ) {
            dataProvided = false;
            mRtvSharingActivitDescription.showError();
        } else {
            mRtvSharingActivitDescription.cleanError();
        }

        return dataProvided;
    }


    private void saveSharingActivityImage() {
        StorageReference strRef = StorageManager.getInstance().getSharingActivitiesStorageReference( saeData.key );
        UploadTask uploadTask = strRef.putFile( Uri.parse( saeData.imageUri ) );
        uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                saveSharingActivityData( taskSnapshot.getDownloadUrl() );
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                mErrorDialog.setDialogContent(R.string.lbl_error_could_not_save, R.string.lbl_error_please_try_again);
                mErrorDialog.show(getChildFragmentManager(), ERROR_DIALOG_TAG);

            }
        });
    }

    private void saveSharingActivityData( Uri imageDownloadUrl ) {
        it.polito.mad.countonme.models.SharingActivity shaAct = new it.polito.mad.countonme.models.SharingActivity();
        User currentUser = ((CountOnMeApp)getActivity().getApplication()).getCurrentUser();
        shaAct.setName( mEdName.getText().toString() );
        shaAct.setDescription( mEdDescription.getText().toString());
        shaAct.setCurrency(mSpnCurrency.getSelectedItem().toString());
        shaAct.setCreatedBy( currentUser );
        shaAct.addUser( currentUser );
        shaAct.setKey( saeData.key );
        if( imageDownloadUrl != null )
            shaAct.setImageUrl( imageDownloadUrl.toString() );
        // finally lets save the data
        try {
            DataManager.getsInstance().updateSharingActivity( shaAct, this );
        } catch ( InvalidDataException ex ) {
            mProgressDialog.dismiss();
            mErrorDialog.setDialogContent(R.string.lbl_error_could_not_save, R.string.lbl_error_please_try_again);
            mErrorDialog.show(getFragmentManager(), ERROR_DIALOG_TAG);
            StorageReference strRef = StorageManager.getInstance().getSharingActivitiesStorageReference( saeData.key );
            strRef.delete();
        }
    }


    private void clearForm()
    {
        mEdName.setText("");
        mEdDescription.setText("");
        mSpnCurrency.setSelection(0);
        mIvPhoto.setImageResource( R.drawable.ic_add_a_photo );
    }


    private void adjustActionBar() {
        if( saeData.isNew )
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.sharing_activity_add_new_title );
        else
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( R.string.sharing_activity_edit_title );
        setHasOptionsMenu( true );
    }

    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

}
