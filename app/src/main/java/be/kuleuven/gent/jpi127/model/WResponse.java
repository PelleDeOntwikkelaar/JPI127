package be.kuleuven.gent.jpi127.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WResponse {
    @SerializedName("@context")
    @Expose
    private String context;
    @SerializedName("@graph")
    @Expose
    private String trainString;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTrainString() {
        return trainString;
    }

    public void setTrainString(String trainString) {
        this.trainString = trainString;
    }
}
