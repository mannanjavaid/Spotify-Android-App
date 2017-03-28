package entertainmentexpert.spotifyplayer;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by furba on 3/26/2017.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String token;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("firebase Token", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //  sendRegistrationToServer(refreshedToken);
    }

    public String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }
}
