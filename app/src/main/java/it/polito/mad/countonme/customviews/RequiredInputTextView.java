package it.polito.mad.countonme.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import it.polito.mad.countonme.R;

/**
 * A custom Text View able to show missing required fielsd
 * Created by francescobruno on 17/04/17.
 */

public class RequiredInputTextView extends android.support.v7.widget.AppCompatTextView {
    private String mStrError;
    private String mStr;

    private int mColorError;
    private int mColor;

    public RequiredInputTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray customAttrs = context.obtainStyledAttributes( attrs, R.styleable.RequiredInputTextView, 0, 0 );
        mStr = (String) getText();
        mColor = getCurrentTextColor();
        try {
            mStrError = customAttrs.getString( R.styleable.RequiredInputTextView_error_text );
            mColorError = customAttrs.getColor( R.styleable.RequiredInputTextView_error_color, Color.BLACK );
        } finally {
            customAttrs.recycle();
        }
    }


    public void showError() {
        setTextColor( mColorError );
        setText( mStr + " (" + mStrError + ")" );
    }

    public void cleanError() {
        setTextColor( mColor);
        setText( mStr );
    }

    public String getStrError() {
        return mStrError;
    }

    public void setStrError( final String error ) {
        mStrError = error;
    }

    public void setSrtError( final int resId ) {
        mStrError = getResources().getString( resId );
    }

}
