package com.example.chaudelivery.Running_Service;

import android.util.Log;

import java.util.Map;
import java.util.Objects;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;
import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

import static com.example.chaudelivery.utils.Constant.REQUEST_KEY;

public class Pusher {

    static String TAG = "Pusher";

    public static ObjectMapper mapper = new ObjectMapper();

    public static String sendPush(pushrequest pushrequest) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("https://api.pushy.me/push?api_key=" + REQUEST_KEY);
        request.addHeader("Content-Type", "application/json");
        byte[] json = mapper.writeValueAsBytes(pushrequest);
        request.setEntity(new ByteArrayEntity(json));
        HttpResponse response = client.execute(request, new BasicHttpContext());
        String respond_from_post_request = EntityUtils.toString(response.getEntity());
        Map<String, Object> map = mapper.readValue(respond_from_post_request, Map.class);
        if (map.containsKey("error"))
            throw new Exception(Objects.requireNonNull(map.get("error")).toString());


        return null;

    }

    public static class pushrequest {
        public Object to;
        public Object data;

        public pushrequest(Object data, Object to) {
            this.to = to;
            this.data = data;

        }
    }
}
