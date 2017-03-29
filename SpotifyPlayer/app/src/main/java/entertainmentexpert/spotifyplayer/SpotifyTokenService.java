package entertainmentexpert.spotifyplayer;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by furba on 3/29/2017.
 */

public interface SpotifyTokenService {

    @FormUrlEncoded
    @POST("api/token")
    Call<TokenResponceModel> getRefreshToken(@Field("code") String code,
                                             @Field("grant_type") String grant_type, @Field("redirect_uri") String redirect_uri, @Field("client_id") String client_id, @Field("client_secret") String client_secret);

    @FormUrlEncoded
    @POST("api/token")
    Call<RefreshTokenResponceModel> getAccessTokenFromRefreshToken(@Field("refresh_token") String refresh_token,
                                                                   @Field("grant_type") String grant_type, @Field("client_id") String client_id, @Field("client_secret") String client_secret);


}
