package com.example.parikkap.roundtripoptimizer;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by parikkap on 8.5.2017.
 */

public class getWayPoints extends AsyncTask<ArrayList<String>, Void, JSONObject>  {
    private JSONObject waypointsJsonObj;

    @Override
    protected JSONObject doInBackground(ArrayList<String>... newDestination) {
        String apiKey = "AIzaSyD83CHF4wXNOHrfL1zrTzAZcPcUsn7pP1U";
        JSONObject response;
        String wayToGo = "";
        try {
            for(int i = 1; i<newDestination[0].size()-1; i++){
                wayToGo =  wayToGo+ "|" + newDestination[0].get(i);
            }
            int i = 0;
            while (i < newDestination[0].size()-1) {
                URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + newDestination[0].get(0) + "&destination="+ newDestination[0].get(0)+ "&waypoints=optimize:true"+wayToGo+ "&key=" + apiKey);
                HttpURLConnection con = null;
                con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                }
                    waypointsJsonObj = new JSONObject(sb.toString());
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return waypointsJsonObj;
    }
}
