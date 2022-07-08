package com.rlmax.smartbincollect;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //Called when a message is received.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {//code goes here}

        //Called when a new token for the default Firebase project is generated.

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
