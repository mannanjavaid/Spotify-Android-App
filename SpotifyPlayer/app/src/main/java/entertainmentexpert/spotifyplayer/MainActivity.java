package entertainmentexpert.spotifyplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPublic;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "617b892859ab4b148161cf0e5335b74e";
    private static final String REDIRECT_URI = "entertainmentexpert.spotifyplayer://callback";
    private static final int REQUEST_CODE = 1337;
    private List<PlaylistSimple> playList;
    private Player mPlayer;
    private UserPublic user;
    private SpotifyService webService;
    private String userId = "pespotify1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                CreatePlayer(response.getAccessToken());
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(response.getAccessToken());
                webService = api.getService();

            }
        }

    }


    private void Play(PlaylistSimple playlist) {

        webService.getPlaylistTracks(userId, playlist.id, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                List<PlaylistTrack> list = playlistTrackPager.items;
                Collections.shuffle(list);
                int count = 0;
                for (PlaylistTrack track : list) {
                    if (count == 0) {
                        mPlayer.playUri(null, track.track.uri, 0, 0);
                        mPlayer.setPlaybackBitrate(null, PlaybackBitrate.BITRATE_HIGH);
                    } else {
                        mPlayer.queue(null, track.track.uri);
                    }
                    count++;
                    Log.d("track", "");
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }






    private void getUserInfo(SpotifyService service) {
        service.getUser(userId, new Callback<UserPublic>() {
            @Override
            public void success(UserPublic userPublic, Response response) {
                user = userPublic;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Get User Info", error.getMessage());
            }
        });

    }

    private void getPlayList(SpotifyService service) {

        service.getPlaylists(userId, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("Get PlayLists", spotifyError.getMessage());
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                playList = playlistSimplePager.items;
                Play(playList.get(0));
            }
        });

    }



    @Override
    public void onLoggedIn() {
       // getUserInfo(webService);
        getPlayList(webService);
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());

        switch (playerEvent) {

            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    private void CreatePlayer(String accessToken) {
        Config playerConfig = new Config(this, accessToken, CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                spotifyPlayer.addConnectionStateCallback(MainActivity.this);
                spotifyPlayer.addNotificationCallback(MainActivity.this);
                mPlayer = spotifyPlayer;
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }
}
