package entertainmentexpert.spotifyplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by furba on 3/29/2017.
 */

public class RequestActivity extends AppCompatActivity {

    private String bitRate;
    private String playList;
    private List<PlaylistSimple> playLists;
    private EditText imageUrl;
    private static final String CLIENT_ID = "617b892859ab4b148161cf0e5335b74e";
    private static final String REDIRECT_URI = "entertainmentexpert.spotifyplayer://callback";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private TimePicker startTime;
    private TimePicker stopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageUrl = (EditText) findViewById(R.id.urlBox);
        startTime = (TimePicker) findViewById(R.id.StartTime);
        startTime.setIs24HourView(true);

        stopTime = (TimePicker) findViewById(R.id.StopTime);
        stopTime.setIs24HourView(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.TOKEN,
                    REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            builder.setShowDialog(true);
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);


        }

    }

    private void doProcess(String accessToken) {
        SpotifyService api = GetSpotifyApi(accessToken);
        final Spinner bitRateSpinner = (Spinner) findViewById(R.id.bitRateSpinner);
        bitRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bitRate = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner playlistSpinner = (Spinner) findViewById(R.id.playListSpinner);
        playlistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // playList = parent.getItemAtPosition(position).toString();
                playList = playLists.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List<String> bitRateList = new ArrayList<>();
        bitRateList.add("BITRATE_LOW");
        bitRateList.add("BITRATE_NORMAL");
        bitRateList.add("BITRATE_HIGH");

        ArrayAdapter<String> bitRateDataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, bitRateList);
        bitRateDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bitRateSpinner.setAdapter(bitRateDataAdapter);

        final ArrayAdapter<String> playListdataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        playListdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playlistSpinner.setAdapter(playListdataAdapter);
        api.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                playLists = playlistSimplePager.items;
                for (PlaylistSimple item : playlistSimplePager.items) {
                    playListdataAdapter.add(item.name);
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(-11, returnIntent);
                finish();
            }
        });


        Button imageButton = (Button) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Image", imageUrl.getText().toString());
                editor.commit();
                Intent returnIntent = new Intent();
                setResult(-111, returnIntent);
                finish();
            }
        });
        Button sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SPOTIFY_REQUEST_CODE:
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
                if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                    doProcess(response.getAccessToken());

                }
                break;
        }
    }

    private SpotifyService GetSpotifyApi(String token) {
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        return api.getService();
    }

    private void SendMessage() {
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://fcm.googleapis.com/")
                .build();

        MessageService service = retrofit.create(MessageService.class);
        MessageModel message = new MessageModel();
        Data data = new Data();
        data.setPlayList(playList);
        data.setQuality(bitRate);

        String startTimeString = startTime.getCurrentHour() + "_" + startTime.getCurrentMinute();
        String stopTimeString = stopTime.getCurrentHour() + "_" + stopTime.getCurrentMinute();
        data.setStartTime(startTimeString);
        data.setStopTime(stopTimeString);
        message.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(message);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);
        Call<ResponseBody> call = service.sendMessageToTopic(requestBody);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }
}
