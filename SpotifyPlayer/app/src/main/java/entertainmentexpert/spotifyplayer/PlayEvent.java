package entertainmentexpert.spotifyplayer;

import com.spotify.sdk.android.player.PlaybackBitrate;

/**
 * Created by furba on 3/28/2017.
 */

public class PlayEvent {

    public String playListId;
    public PlaybackBitrate bitrate;

    public PlayEvent(String playListId, PlaybackBitrate bitrate) {
        this.bitrate = bitrate;
        this.playListId = playListId;
    }

}
