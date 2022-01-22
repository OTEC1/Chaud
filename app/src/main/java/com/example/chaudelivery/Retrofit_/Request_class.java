package com.example.chaudelivery.Retrofit_;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Request_class {


    @POST("appCat/SendPasswordRestLink")
    Call<Object> sendRestLink(@Body Map<String,Object> data);

}
