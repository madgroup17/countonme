package it.polito.mad.countonme;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import static android.app.PendingIntent.getActivity;

/**
 * Created by LinaMaria on 14/04/2017.
 */

public class LinkSharing {

    public static Intent shareActivity(Activity currentActivity)
    {
            Intent sendIntent = new Intent();
            sendIntent.setType("text/plain");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,R.string.message_to_share);
            //"Hey check out my app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus");



            return sendIntent;
    }
}
