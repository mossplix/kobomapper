package org.openstreetmap.josm.plugins.kobomapper.utils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.GeoJsonObject;
import org.geojson.GeometryCollection;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.openstreetmap.josm.gui.MapView;

import org.openstreetmap.josm.plugins.geojson.DataSetBuilder;
import org.openstreetmap.josm.plugins.geojson.DataSetBuilder.BoundedDataSet;
import org.openstreetmap.josm.plugins.geojson.GeoJsonLayer;
import org.openstreetmap.josm.gui.layer.Layer;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;

import static org.openstreetmap.josm.gui.mappaint.mapcss.ExpressionFactory.Functions.tr;
import java.io.IOException; 
/**
 * Created by Moses on 7/22/16.
 */

public class GeoserverApi  {

    private GeoserverApiService service;
    private static String GEOSERVER_BASE="http://127.0.0.1:8080/geoserver/";

    MapView mv = null;
   
    public GeoserverApi(){
        this.service=getStringClient();
    }
    public GeoserverApiService getStringClient(){
        Retrofit retrofit = new Retrofit.Builder()  
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(GEOSERVER_BASE)
        .build();
        service = retrofit.create(GeoserverApiService.class);  
        return service;
    }


    public Observable<String> getGeoJson()
    {
        return service.getStringResponse()
        .subscribeOn(Schedulers.io());

       
    }

    public void getJson()
    {
        Call<String> stringCall = service.getStringResponse("http://127.0.0.1:8080/geoserver/hot/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=hot:boundaries&maxFeatures=50&outputFormat=application%2Fjson");  
    stringCall.enqueue(new Callback<String>() {  
    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
            try{
                String responseString = response.body();
                final ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                final GeoJsonObject object = mapper.readValue(responseString, GeoJsonObject.class);
                GeoJsonObject geoJsonObject = new GeometryCollection();
                final BoundedDataSet data = new DataSetBuilder().build(object);

                 final Layer layer = new GeoJsonLayer(tr("Boundaries"), data);
                    layer.setBackgroundLayer(true);
                    MainApplication.getLayerManager().addLayer(layer);
                }
                catch(IOException e)
                {

                }
        }

    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
});
    }


}
