package it.polito.mad.countonme.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

import it.polito.mad.countonme.AppConstants;

/**
 * Created by francescobruno on 07/05/17.
 */

public class DatePicker extends DialogFragment {

    DatePickerDialog.OnDateSetListener mListener;
    Date mCurrentDate;

    public DatePicker() {
       setRetainInstance( true );
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        if( savedInstanceState != null )
            mCurrentDate = ( Date ) savedInstanceState.getSerializable( AppConstants.SAVE_STATE_KEY_DATE );
        c.setTime( mCurrentDate );
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), mListener, year, month, day);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(AppConstants.SAVE_STATE_KEY_DATE, mCurrentDate );
    }

    public void setDateSetListener(DatePickerDialog.OnDateSetListener listener ) {
        mListener = listener;
    }

    public void setDate( Date date ) {
        mCurrentDate = date;
    }



}
