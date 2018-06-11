package org.openstreetmap.josm.plugins.kobomapper.utils;



import java.util.List;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;
import retrofit2.Call;



public interface GeoserverApiService {


   @GET("tiger/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=tiger:tiger_roads&maxFeatures=50&outputFormat=application%2Fjson")
   Observable<String> getStringResponse();  

   @GET()
   Call<String> getStringResponse(@Url String url);  

}
