package entertainmentexpert.spotifyplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String accessToken = (String) bundle.get("accessToken");
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

            Button sendButton = (Button) findViewById(R.id.send);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessage();
                }
            });
        }

    }


    private SpotifyService GetSpotifyApi(String token) {
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        return api.getService();
    }

    private void SendMessage() {
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://fcm.googleapis.com/")
                .build();

        MessageService service = retrofit.create(MessageService.class);
        MessageModel message = new MessageModel();
        Data data = new Data();
        data.setPlayList(playList);
        data.setQuality(bitRate);
        message.setdata(data);

        Call<String> call = service.sendMessageToTopic(message);
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }
}
