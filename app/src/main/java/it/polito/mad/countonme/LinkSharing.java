package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.ArrayList;

import it.polito.mad.countonme.database.DataManager;

import static android.app.PendingIntent.getActivity;

/**
 * Created by LinaMaria on 14/04/2017.
 */

public class LinkSharing {

    //Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);//makes me extend LinkSharing to Context and implement context class methods

    public static Intent shareActivity(Activity currentActivity,String key)
    {
            /*Intent sendIntent = new Intent();
            sendIntent.setType("text/plain");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,R.string.message_to_share);*/
            //"Hey check out my app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus");

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            //i.setData(Uri.parse("market://details?id=it.polito.mad.countonme"));
            i.putExtra(Intent.EXTRA_SUBJECT, "CountOnMe\n");
            //R.string.message_to_share
            String sAux = "\n"+currentActivity.getResources().getString(R.string.message_to_share);//"\nLet me recommend you this application\n\n";
            //getExpenseReference
            //(String)getData(),currentActivity,this
            //String[] db = currentActivity.getApplication().databaseList();
            //System.out.println(db.toString());
            //DataManager.getsInstance().getExpensesReference();//addNewExpense((String) getData(), newExpense, this);
            sAux=sAux+"\n"+Uri.parse("https://www.countonme.com/")+key;
        //sAux=sAux+"\n"+Uri.parse("https://countonme-80940.firebaseio.com/users");
            //sAux=sAux+"\n"+Uri.parse("https://www.francesco.com/jihdougosofsfihs");

        //("https://countonme-80940.firebaseio.com/shareacts/-KgyKEU7hgUSKEKNiIpr");
        //("https://console.firebase.google.com/project/countonme-80940/database/data/shareacts/-KgyKEU7hgUSKEKNiIpr");
        // ("market://details?id=it.polito.mad.countonme");
//            sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            return i;
    }
}
