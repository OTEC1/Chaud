package com.example.chaudelivery.Retrofit_;

import com.example.chaudelivery.utils.Constant;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Base_config {


    static Retrofit retrofit;


    public  static  Retrofit getRetrofit(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .build();


        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.FIREBASE_CLOUD_FUNCTION_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    . build();

        }

        return  retrofit;
    }
}
