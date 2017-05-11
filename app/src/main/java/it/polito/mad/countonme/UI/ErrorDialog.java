package it.polito.mad.countonme.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import it.polito.mad.countonme.R;

/**
 * Created by francescobruno on 10/05/17.
 */

public class ErrorDialog extends DialogFragment {

    private int mTitleResId;
    private int mMessageResId;

    public void setDialogContent( int titleResId, int messageResId ) {
        mTitleResId = titleResId;
        mMessageResId = messageResId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setTitle( mTitleResId )
            .setCancelable( false)
            .setMessage( mMessageResId )
            .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });
        return builder.create();
    }
}
