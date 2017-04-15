package entertainmentexpert.spotifyplayer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.koushikdutta.ion.Ion;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "617b892859ab4b148161cf0e5335b74e";
    private static final String CLIENT_SECRET = "723d0942ecdf47d6b910e945e74e46e0";
    private static final String REDIRECT_URI = "entertainmentexpert.spotifyplayer://callback";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final int LOGIN_REQUEST_CODE = 1;
    private static final int ADMIN_REQUEST_CODE = 2;
    private List<PlaylistSimple> playList;
    private Player mPlayer;
    private UserPublic user;
    private SpotifyService webService;
    private String userId = "pespotify1";
    private List<PlaylistTrack> list;
    private PlaybackBitrate bitRate = PlaybackBitrate.BITRATE_HIGH;
    private String AccessCode;
    private String AccessToken;
    private String RefreshToken;
    private SpotifyTokenService tokenService;
    private Date expireTime;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        if (i != null) {

            Bundle b = i.getExtras();
            if (b != null) {
                boolean isAlarm = b.getBoolean("Alarm");
                if (!isAlarm) {
                    this.finish();
                    System.exit(0);

                }

            }

        }

        imageView = (ImageView) findViewById(R.id.imageView2);
        SharedPreferences sharedpreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        String imageUrl = sharedpreferences.getString("Image", "");
        Ion.with(imageView).load(imageUrl);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyFirebaseInstanceIDService fb = new MyFirebaseInstanceIDService();
        fb.getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("Playlist");
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.CODE,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        builder.setShowDialog(false);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.admin:

                startActivityForResult(new Intent(this, AdminActivity.class), LOGIN_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SPOTIFY_REQUEST_CODE:
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
                if (response.getType() == AuthenticationResponse.Type.CODE) {
                    AccessCode = response.getCode();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://accounts.spotify.com/").addConverterFactory(GsonConverterFactory.create())
                            .build();

                    tokenService = retrofit.create(SpotifyTokenService.class);
                    GetAccessToken();

                }
                break;

            case LOGIN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Intent i = new Intent(this, RequestActivity.class);
                    i.putExtra("accessToken", AccessToken);
                    startActivityForResult(i, ADMIN_REQUEST_CODE);
                }
                break;

            case ADMIN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Request is Sent.", Toast.LENGTH_SHORT).show();
                } else if (resultCode == -11) {

                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                } else if (resultCode == -111) {

                    SharedPreferences sharedpreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE);
                    String imageUrl = sharedpreferences.getString("Image", "");
                    Ion.with(imageView).load(imageUrl);

                } else {
                    Toast.makeText(this, "Request is not Sent.", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    private void GetAccessToken() {
        Call<TokenResponceModel> call = tokenService.getRefreshToken(AccessCode, "authorization_code", REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        call.enqueue(new retrofit2.Callback<TokenResponceModel>() {
            @Override
            public void onResponse(Call<TokenResponceModel> call, retrofit2.Response<TokenResponceModel> response) {

                RefreshToken = response.body().getRefreshToken();
                AccessToken = response.body().getAccessToken();
                int expireIn = response.body().getExpiresIn();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, expireIn - 1000);
                expireTime = calendar.getTime();
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(AccessToken);
                webService = api.getService();
                CreatePlayer(AccessToken);
            }

            @Override
            public void onFailure(Call<TokenResponceModel> call, Throwable t) {

            }
        });
    }

    private void RefreshAccessToken() {

        Call<RefreshTokenResponceModel> call = tokenService.getAccessTokenFromRefreshToken(RefreshToken, "refresh_token", CLIENT_ID, CLIENT_SECRET);
        call.enqueue(new retrofit2.Callback<RefreshTokenResponceModel>() {
            @Override
            public void onResponse(Call<RefreshTokenResponceModel> call, retrofit2.Response<RefreshTokenResponceModel> response) {

                AccessToken = response.body().getAccessToken();
                int expireIn = response.body().getExpiresIn();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, expireIn - 1000);
                expireTime = calendar.getTime();
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(AccessToken);
                webService = api.getService();

            }

            @Override
            public void onFailure(Call<RefreshTokenResponceModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PlayEvent event) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(event.startTimeHour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(event.startTimeMinute));
        long startTime = calendar.getTimeInMillis();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(event.stopTimeHour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(event.stopTimeMinutes));
        long stopTime = calendar.getTimeInMillis();
        Intent startIntent = new Intent(this, MyBroadcastReceiver.class);
        startIntent.putExtra("Type", "Start");
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, 123456789, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(startPendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, startPendingIntent);

        Intent stopIntent = new Intent(this, MyBroadcastReceiver.class);
        stopIntent.putExtra("Type", "Stop");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 987654321, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(stopPendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, stopTime, AlarmManager.INTERVAL_DAY, stopPendingIntent);

        bitRate = event.bitrate;
        Play(event.playListId);
    }

    private void Play(String playlistId) {
        webService.getPlaylistTracks(userId, playlistId, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                list = playlistTrackPager.items;
                Collections.shuffle(list);
                mPlayer.playUri(null, list.get(0).track.uri, 0, 0);
                mPlayer.setPlaybackBitrate(null, bitRate);
                list.remove(0);
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
                Play(playList.get(0).id);
            }
        });

    }


    @Override
    public void onLoggedIn() {
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

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }
}