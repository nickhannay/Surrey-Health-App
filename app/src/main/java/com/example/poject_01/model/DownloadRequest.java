package com.example.poject_01.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 *  Gets Data from URL and handles it
 */
public class DownloadRequest {

    private static final int RESTAURANT_URL_CHECK = 0;
    private static final int INSPECTION_URL_CHECK = 1;
    private RequestQueue mQueue;
    private String url;
    private Context rContext;
    private String fileName;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private boolean urlModified;
    private String surreyLastModified;
    private String csvDataURL;
    private int dataSetCheck;

    public DownloadRequest(String url, Context rContext, String fileName, int check ) {
        this.url = url;
        this.rContext = rContext;
        this.fileName = fileName;
        this.prefs = rContext.getSharedPreferences("startup_logic", MODE_PRIVATE);
        this.editor = prefs.edit();
        this.dataSetCheck = check;
    }

    public boolean dataModified(){
        return urlModified;
    }

    public void downloadData() {
        DownloadDataAsyncTask task = new DownloadDataAsyncTask(rContext, fileName);
        // calls DownloadDataAsyncTask.doInBackground()

        if (dataSetCheck == INSPECTION_URL_CHECK) {
            if(urlModified){
                editor.putString("inspections_last_modified" , surreyLastModified);
                task.execute(csvDataURL, "1");
            }
        }
        if(dataSetCheck == RESTAURANT_URL_CHECK){
            if(urlModified){
                editor.putString("restaurants_last_modified", surreyLastModified);
                task.execute(csvDataURL, "0");
            }
        }

        // updating preferences used to control flow of app
        editor.putBoolean("initial_update", true);
        Date currentDate = new Date(System.currentTimeMillis());
        editor.putLong("last_update", currentDate.getTime() );
        editor.commit();

    }

    public void getURL( final VolleyCallBack callBack) {
        mQueue = Volley.newRequestQueue(rContext);
        JsonObjectRequest restaurantsRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject urlResult = response.getJSONObject("result");
                    JSONArray dataType = urlResult.getJSONArray("resources");
                    JSONObject csvObject = dataType.getJSONObject(0);
                    csvDataURL  =  csvObject.getString("url");

                    surreyLastModified = csvObject.getString("last_modified");
                    if (dataSetCheck == RESTAURANT_URL_CHECK) {
                        String restaurantLastModified = prefs.getString("restaurants_last_modified", "");
                        Log.d("get url", "ours: " + restaurantLastModified + ", SURREY: " + surreyLastModified);
                        if (!Objects.equals(surreyLastModified, restaurantLastModified)){
                            urlModified = true;
                        }
                        else{
                            urlModified = false;
                        }
                    }
                    else if ( dataSetCheck == INSPECTION_URL_CHECK) {
                        String inspectionsLastModified = prefs.getString("inspections_last_modified", "");
                        Log.d("get url", "ours: " + inspectionsLastModified + ", SURREY: " + surreyLastModified);
                        if (!Objects.equals(surreyLastModified, inspectionsLastModified)){

                            urlModified = true;
                        }
                        else{
                            urlModified = false;
                        }
                    }
                    callBack.onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(restaurantsRequest);
    }

    public interface VolleyCallBack {
        void onSuccess();
    }
}
