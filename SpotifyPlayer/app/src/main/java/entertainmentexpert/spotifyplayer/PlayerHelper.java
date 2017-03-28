package entertainmentexpert.spotifyplayer;

import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackBitrate;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by furba on 3/28/2017.
 */

public class PlayerHelper implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private List<PlaylistTrack> list;
    private static Player mPlayer;
    private static String userId;
    private static SpotifyService webService;

    public void PlayerHelper(SpotifyService service) {
        webService = service;
    }

    public void CreatePlayer(MainActivity activity, String accessToken, String CLIENT_ID) {
        Config playerConfig = new Config(activity, accessToken, CLIENT_ID);
        Spotify.getPlayer(playerConfig, activity, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                spotifyPlayer.addConnectionStateCallback(PlayerHelper.this);
                spotifyPlayer.addNotificationCallback(PlayerHelper.this);
                mPlayer = spotifyPlayer;
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }


    public void Play(String playlistId, String id) {
        userId = id;
        webService.getPlaylistTracks(userId, playlistId, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                list = playlistTrackPager.items;
                Collections.shuffle(list);
                mPlayer.setPlaybackBitrate(null, PlaybackBitrate.BITRATE_HIGH);
                mPlayer.playUri(null, list.get(0).track.uri, 0, 0);
                list.remove(0);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }


    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("player Helper", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            case kSpPlaybackNotifyAudioDeliveryDone:
                if (list.size() > 0) {
                    mPlayer.playUri(null, list.get(0).track.uri, 0, 0);

                    list.remove(0);
                }
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("player Helper", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }
}
