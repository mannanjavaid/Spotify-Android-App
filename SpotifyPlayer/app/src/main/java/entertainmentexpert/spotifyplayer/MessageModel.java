package entertainmentexpert.spotifyplayer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageModel {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("to")
    @Expose
    private String to = "/topics/Playlist";

    @SerializedName("priority")
    @Expose
    private String priority = "high";

    @SerializedName("time_to_live")
    @Expose
    private int time_to_live = 3;


    public int getTime_to_live() {
        return time_to_live;
    }

    public void setTime_to_live(int time_to_live) {
        this.time_to_live = time_to_live;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}
