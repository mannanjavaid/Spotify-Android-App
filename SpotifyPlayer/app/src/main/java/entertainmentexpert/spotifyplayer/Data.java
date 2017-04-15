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

    @SerializedName("StartTime")
    @Expose
    private String startTime;

    @SerializedName("StopTime")
    @Expose
    private String stopTime;

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

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