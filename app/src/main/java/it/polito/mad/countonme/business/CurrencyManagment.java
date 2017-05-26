package it.polito.mad.countonme.business;

import android.content.Context;

import it.polito.mad.countonme.R;



/**
 * Created by Khatereh on 5/26/2017.
 */

public  class CurrencyManagment
{
    public  enum enumCurrency
    {
        Doller(0),Euro(1),Pesos(2);
        private int value;

        private enumCurrency(int value) {
            this.value = value;
        }

        public static enumCurrency GetValue(int index)
        {
            switch (index) {
                case 0:
                    return Doller;
                case 1:
                    return Euro;
                case 2:
                    return Pesos;
                default:
                    return null;
            }
        }

    }

    public static String GetText(int Index, Context context )
    {
        enumCurrency eCurrency = enumCurrency.GetValue(Index);
        switch (eCurrency)
        {
            case Doller:
                return context.getString(R.string.currency_dollar_lbl);
            case Euro:
                return context.getString(R.string.currency_euro_lbl);
            case Pesos:
                return context.getString(R.string.currency_pesetas_lbl);
            default:
                return "";

        }
    }



}
