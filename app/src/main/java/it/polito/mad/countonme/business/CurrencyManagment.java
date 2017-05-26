package it.polito.mad.countonme.business;

import android.content.Context;

import it.polito.mad.countonme.R;



/**
 * Created by Khatereh on 5/26/2017.
 */

public  class CurrencyManagment
{
    public enum enumCurrency {
        DOLLAR,
        EURO,
        PESOS
    }

    public static String GetText( int index, Context context )
    {
        enumCurrency eCurrency = enumCurrency.values()[index];
        switch ( eCurrency )
        {
            case DOLLAR:
                return context.getString(R.string.currency_dollar_lbl);
            case EURO:
                return context.getString(R.string.currency_euro_lbl);
            case PESOS:
                return context.getString(R.string.currency_pesetas_lbl);
            default:
                return "";

        }
    }



}
