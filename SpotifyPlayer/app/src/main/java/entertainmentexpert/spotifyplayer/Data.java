package entertainmentexpert.spotifyplayer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by furba on 3/30/2017.
 */

public class Data {

    @SerializedName("playList")
    @Expose
    private String playList;
    @SerializedName("quality")
    @Expose
    private String quality;

    public String getPlayList() {
        return playList;
    }

    public void setPlayList(String playList) {
        this.playList = playList;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

}