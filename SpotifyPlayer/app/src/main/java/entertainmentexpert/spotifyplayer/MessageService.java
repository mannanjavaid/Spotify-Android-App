package entertainmentexpert.spotifyplayer;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by furba on 3/29/2017.
 */

public interface MessageService {

    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAA1zxUmmg:APA91bHwYxZDOuOOBZyR03NwAKG3s5yW9AwDJXp0TluXclmsOdB5ZvQ45m5YBZnjJVbKm3i33asggFpEeEuubyE4KRKnQKlUUr1Y-KPIu8Zh__6Y11ROgieNj0jQcBdU3NJ_jh1j-OCm"
    })
    @POST("fcm/send")
    Call<ResponseBody> sendMessageToTopic(@Body RequestBody messageModel);
}
