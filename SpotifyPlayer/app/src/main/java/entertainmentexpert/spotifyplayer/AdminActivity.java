package entertainmentexpert.spotifyplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by furba on 3/28/2017.
 */

public class AdminActivity extends AppCompatActivity {

    private String USER_NAME = "admin";
    private String PASSWORD = "partyex450";

    /*    private String USER_NAME = "1";
        private String PASSWORD = "1";*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button loginButton = (Button) findViewById(R.id.login);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText login = (EditText) findViewById(R.id.userName);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(PASSWORD) && login.getText().toString().equals(USER_NAME)) {

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), "User name or Password is wrong", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


}
