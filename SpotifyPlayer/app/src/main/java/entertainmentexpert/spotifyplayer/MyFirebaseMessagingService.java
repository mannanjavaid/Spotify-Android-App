package entertainmentexpert.spotifyplayer;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.spotify.sdk.android.player.PlaybackBitrate;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by furba on 3/26/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> dataMap = remoteMessage.getData();
            String playlist = dataMap.containsKey("playList") ? dataMap.get("playList") : null;
            String quality = dataMap.containsKey("quality") ? dataMap.get("quality") : null;

            String startTime = dataMap.containsKey("StartTime") ? dataMap.get("StartTime") : null;
            String stopTime = dataMap.containsKey("StopTime") ? dataMap.get("StopTime") : null;
            PlaybackBitrate bitRate = PlaybackBitrate.valueOf(quality);

            EventBus.getDefault().post(new PlayEvent(playlist, bitRate, startTime, stopTime));
        }

    }

}
