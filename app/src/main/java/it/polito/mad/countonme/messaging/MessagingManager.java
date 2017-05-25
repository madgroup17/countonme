package it.polito.mad.countonme.messaging;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import it.polito.mad.countonme.exceptions.InvalidDataException;

/**
 * Created by francescobruno on 20/05/17.
 */

public class MessagingManager {

    private FirebaseMessaging mMessaging;

    private static final MessagingManager sInstance = new MessagingManager();

    private MessagingManager() { mMessaging = FirebaseMessaging.getInstance(); }

    public static MessagingManager getInstance() { return sInstance; }

    public void  subscribeToSharingActivity( String shactKey ) throws InvalidDataException {
        if( shactKey != null && shactKey.length() > 0 )
            mMessaging.subscribeToTopic( shactKey );
        else
            throw new InvalidDataException( "subscribeToSharingActivity: Invalid topic" );
    }

    public void unsubscribeFromSharingActivity( String shactKey ) throws InvalidDataException {
        if( shactKey != null && shactKey.length() >0 )
            mMessaging.unsubscribeFromTopic( shactKey );
        else
            throw new InvalidDataException( "unsubscribeFromSharingActivity: Invalid topic" );
    }

}
