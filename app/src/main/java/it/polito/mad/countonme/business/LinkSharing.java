package it.polito.mad.countonme.business;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.ArrayList;

import it.polito.mad.countonme.R;
import it.polito.mad.countonme.database.DataManager;

import static android.app.PendingIntent.getActivity;

/**
 * Created by LinaMaria on 14/04/2017.
 */

public class LinkSharing {

    //Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);//makes me extend LinkSharing to Context and implement context class methods

    public static Intent shareActivity(Activity currentActivity,String key)
    {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "CountOnMe\n");
            String sAux = "\n"+currentActivity.getResources().getString(R.string.message_to_share);
            sAux=sAux+"\n"+Uri.parse("https://www.countonme.com/")+key;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            return i;
    }
}
