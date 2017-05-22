package it.polito.mad.countonme.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import it.polito.mad.countonme.R;

/**
 * Created by francescobruno on 20/05/17.
 */

public class ImageSourceDialog extends DialogFragment {

    public static final int TAKE_FROM_CAMERA    = 0;
    public static final int TAKE_FORM_GALLERY   = 1;

    public interface IImageSourceDialogListener {
        public void onImageSourceSelected( int which );
    }

    private IImageSourceDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // look whether the fragment implements the correct interface
        // and deliver message to it later
        try {
            mListener = ( IImageSourceDialogListener ) getParentFragment();
        } catch( ClassCastException e ) {
            // can't use the parent as lister
            throw new ClassCastException(  " Must implement IImageSourceDialogListner" );
        }
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setTitle(R.string.lbl_add_picture_from)
                .setItems(R.array.image_sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int positionIdx) {
                        if( mListener != null )
                            mListener.onImageSourceSelected( positionIdx == 0 ? TAKE_FROM_CAMERA : TAKE_FORM_GALLERY );
                    }
                })
                .setCancelable( true );
        return builder.create();
    }

}
