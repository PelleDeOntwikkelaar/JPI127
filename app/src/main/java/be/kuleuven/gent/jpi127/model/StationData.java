package be.kuleuven.gent.jpi127.model;

import org.json.JSONException;
import org.json.JSONObject;

public class StationData {
    private String stationCode;
    private int commDelay;
    private int piek;
    private int dal;

    public StationData(String stationCode, int commDelay, int piek, int dal) {
        this.stationCode = stationCode;
        this.commDelay = commDelay;
        this.piek = piek;
        this.dal = dal;
    }

    public StationData(JSONObject jsonObject){
        try {
            stationCode=jsonObject.getString("stationuri");
            commDelay=jsonObject.getInt("avg_delay");
            piek=jsonObject.getInt("max_delay");
            dal=jsonObject.getInt("min_delay");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public int getCommDelay() {
        return commDelay;
    }

    public void setCommDelay(int commDelay) {
        this.commDelay = commDelay;
    }

    public int getPiek() {
        return piek;
    }

    public void setPiek(int piek) {
        this.piek = piek;
    }

    public int getDal() {
        return dal;
    }

    public void setDal(int dal) {
        this.dal = dal;
    }
}
