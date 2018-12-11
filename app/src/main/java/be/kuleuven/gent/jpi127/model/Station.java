package be.kuleuven.gent.jpi127.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Station implements Parcelable {
    String uri;
    String name;

    public Station(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public Station(Station station) {
        this.name=station.name;
        this.uri=station.uri;
    }

    public Station(JSONObject obj){
        try {
            name=obj.getString("name");
            uri=obj.getString("uri");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Station> CREATOR
            = new Parcelable.Creator<Station>() {
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        public Station[] newArray(int size) {
            return new Station[size];
        }


    };

    private Station(Parcel in) {
        uri = in.readString();
        name =in.readString();
    }
}
