package entertainmentexpert.spotifyplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by furba on 4/13/2017.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String type = bundle.getString("Type");
                if (type.equals("Start")) {
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("Alarm", true);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } else {

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("Alarm", false);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        }
    }
}