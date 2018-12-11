package be.kuleuven.gent.jpi127.support;

import be.kuleuven.gent.jpi127.model.WResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IRailApi {


    @GET("{url}")
    Call<WResponse> getStationDatils(@Path("url") String url);
}
