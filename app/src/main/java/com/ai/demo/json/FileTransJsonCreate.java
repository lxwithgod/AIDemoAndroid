package com.ai.demo.json;

import android.app.Activity;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FileTransJsonCreate {
    private Activity activity;
    //private JSONObject transObject;
    private String transObject;
    private String ipAddress;


    public FileTransJsonCreate(Activity activity, String transObject, String ipAddress) {
        super();
        this.activity = activity;
        this.transObject = transObject;
        this.ipAddress = ipAddress;
    }


    public void fileTransJsonCreate(final VolleyCallback callback) {
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(activity);//volley框架实现json互传
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, this.ipAddress, transObject, new Response.Listener<JSONObject>() {
                //对方成功接收json的操作
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("返回的json", response.toString());
                    callback.onSuccess(response);


                }
            }, new Response.ErrorListener() {
                //对方接收json失败的操作
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailure();

                }
            }) {
                //设置头
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            //设置延时 40秒
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //加入到请求队列
            requestQueue.add(jsonRequest);

        } catch (Exception e) {
            Log.e("异常", "fileTransJsonCreate异常!");
            e.printStackTrace();
        }
    }


    public interface VolleyCallback {
        void onSuccess(JSONObject response);

        void onFailure();
    }


}
