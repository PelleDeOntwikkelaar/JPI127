package be.kuleuven.gent.jpi127.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Train {
    private String destination;
    private String code;
    private int platform;
    private int delay;


    public Train() {
    }

    public Train(String destination, String code, int delay, int platform) {
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
            platform=jsonObject.getInt("platform");
            delay=jsonObject.getInt("delay");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
