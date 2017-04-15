package entertainmentexpert.spotifyplayer;

import com.spotify.sdk.android.player.PlaybackBitrate;

/**
 * Created by furba on 3/28/2017.
 */

public class PlayEvent {

    public String playListId;
    public PlaybackBitrate bitrate;
    public String startTimeHour;
    public String startTimeMinute;
    public String stopTimeHour;
    public String stopTimeMinutes;


    public PlayEvent(String playListId, PlaybackBitrate bitrate, String startTime, String stopTime) {
        this.bitrate = bitrate;
        this.playListId = playListId;
        String[] startTimeToken = startTime.split("_");
        this.startTimeHour = startTimeToken[0];
        this.startTimeMinute = startTimeToken[1];
        String[] stopTimeToken = stopTime.split("_");
        this.stopTimeHour = stopTimeToken[0];
        this.stopTimeMinutes = stopTimeToken[1];
    }

}
