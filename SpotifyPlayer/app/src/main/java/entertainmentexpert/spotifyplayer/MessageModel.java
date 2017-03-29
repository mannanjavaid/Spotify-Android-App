package entertainmentexpert.spotifyplayer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageModel {

    @SerializedName("to")
    @Expose
    private String to = "/topics/Playlist";

    @SerializedName("time_to_live")
    @Expose
    private String time_to_live = "0";

    @SerializedName("data")
    @Expose
    private Data data;

    public String getTo() {
        return to;
    }

    public String getTime_to_live() {
        return time_to_live;
    }

    public Data getdata() {
        return data;
    }

    public void setdata(Data data) {
        this.data = data;
    }


}



