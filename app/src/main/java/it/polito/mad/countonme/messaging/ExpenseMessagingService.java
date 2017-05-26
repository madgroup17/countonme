package it.polito.mad.countonme.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import it.polito.mad.countonme.AppConstants;
import it.polito.mad.countonme.CountOnMeActivity;
import it.polito.mad.countonme.CountOnMeApp;
import it.polito.mad.countonme.R;
import it.polito.mad.countonme.models.User;

/**
 * Created by francescobruno on 20/05/17.
 */

public class ExpenseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ExpenseMessagingService";

    private static final String SUBMITTER_ID = "submitter_id";
    private static final String SUMBITTER_NAME = "submitter_name";
    private static final String NAME  = "name";
    private static final String SHACT_KEY = "shact_key";
    private static final String EXP_KEY = "exp_key";

    private static int sId = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if( remoteMessage != null )
            sendNotification( remoteMessage.getData() );
    }

    private void sendNotification( Map<String, String> data ) {
        if( data == null ) return;
        User currentUser = ( (CountOnMeApp) getApplication() ).getCurrentUser();
        if( currentUser == null || ! currentUser.getId().equals( data.get(SUBMITTER_ID ) ) ) {
            Intent intent = new Intent( this, CountOnMeActivity.class );
            intent.putExtra( AppConstants.FROM_NOTIFICATION, true );
            intent.putExtra( AppConstants.SHARING_ACTIVITY_KEY, data.get(SHACT_KEY) );
            intent.putExtra( AppConstants.EXPENSE_KEY, data.get(EXP_KEY) );
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
            PendingIntent pendingIntent = PendingIntent.getActivity( this, sId, intent, PendingIntent.FLAG_ONE_SHOT );
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_new_expense )
                    .setContentTitle( getResources().getString( R.string.lbl_new_expense_notification_title ) )
                    .setContentText( String.format(getResources().getString(R.string.lbl_new_expense_nofirication_boby ), data.get( NAME ), data.get( SUMBITTER_NAME ) ) )
                    .setAutoCancel(true)
                    .setSound( defaultSoundUri )
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify( sId++ , notificationBuilder.build());
        }

    }
    
}
