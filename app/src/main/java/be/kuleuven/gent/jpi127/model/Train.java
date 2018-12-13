package be.kuleuven.gent.jpi127.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Train {
    private String destination;
    private String code;
    private String platform;
    private String delay;


    public Train() {
    }

    public Train(String destination, String code, String delay, String platform) {
        this.destination = destination;
        this.code = code;
        this.delay = delay;
        this.platform = platform;
    }

    public Train(Train train) {
        this.destination=train.destination;
        this.code=train.code;
        this.platform=train.platform;
        this.delay=train.delay;
    }



    public Train(JSONObject jsonObject){
        try {
            destination=jsonObject.getString("headsign");
            code=jsonObject.getString("routeLabel");
            platform=jsonObject.getString("platform");
            delay=jsonObject.getString("delay");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getPlatform() {
        return platform;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }
}
