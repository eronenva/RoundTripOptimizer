package com.example.parikkap.roundtripoptimizer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JsonParse {
    private ArrayList<String> polylinePoints = new ArrayList<>();
    private String points = "";
    private JSONObject joBjLegs = null;

    public LatLng[] ParseLatAndLang(JSONObject waypointsJson, ArrayList<String> destinations) {
        JSONObject jObjRoutes = null;
        JSONArray routesArray = null;
        JSONArray legsArray = null;
        JSONObject startLocation = null;
        LatLng[] latAndLangsOfGivenPoints = new LatLng[destinations.size()];

        try {
            routesArray = waypointsJson.getJSONArray("routes");
            jObjRoutes = routesArray.getJSONObject(0);
            legsArray = jObjRoutes.getJSONArray("legs");

            for (int k = 0; k < legsArray.length(); k++) {
                joBjLegs = legsArray.getJSONObject(k);
                startLocation = joBjLegs.getJSONObject("start_location");
                double lat = startLocation.getDouble("lat");
                double lng = startLocation.getDouble("lng");
                latAndLangsOfGivenPoints[k] = new LatLng(lat, lng);
                JSONArray arraySteps = joBjLegs.getJSONArray("steps");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latAndLangsOfGivenPoints;
    }
    public Double CalculateDistance(JSONObject waypointsJson){
        JSONObject distance = null;
        double distanceInMeters = 0;
        double totalInKm = 0;
        JSONObject jOSteps = null;
        JSONObject jObjRoutes = null;
        JSONArray routesArray = null;
        JSONArray legsArray = null;
        JSONArray stepsArray = null;
        try {
            routesArray = waypointsJson.getJSONArray("routes");
            System.out.println(waypointsJson);
            jObjRoutes = routesArray.getJSONObject(0);
            legsArray = jObjRoutes.getJSONArray("legs");
            for (int k = 0; k < legsArray.length(); k++) {
                joBjLegs = legsArray.getJSONObject(k);
                stepsArray = joBjLegs.getJSONArray("steps");
                for (int i = 0; i < stepsArray.length(); i++) {
                    jOSteps = stepsArray.getJSONObject(i);
                    distance = jOSteps.getJSONObject("distance");
                    distanceInMeters = distanceInMeters + distance.getDouble("value");
                }
            }
            totalInKm = distanceInMeters / 1000;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return totalInKm;
    }

    public Double CalculateDuration(JSONObject waypointsJson){
        JSONObject duration = null;
        double durationInSeconds = 0;
        double totalDurationInHours = 0;
        JSONObject jOSteps = null;
        JSONObject jObjRoutes = null;
        JSONArray routesArray = null;
        JSONArray legsArray = null;
        JSONArray stepsArray = null;
        try {
            routesArray = waypointsJson.getJSONArray("routes");
            jObjRoutes = routesArray.getJSONObject(0);
            legsArray = jObjRoutes.getJSONArray("legs");
            for (int k = 0; k < legsArray.length(); k++) {
                joBjLegs = legsArray.getJSONObject(k);
                stepsArray = joBjLegs.getJSONArray("steps");
                for (int i = 0; i < stepsArray.length(); i++) {
                    jOSteps = stepsArray.getJSONObject(i);
                    duration = jOSteps.getJSONObject("duration");
                    durationInSeconds = durationInSeconds + duration.getDouble("value");
                }
            }
            double totalDurationInMinutes = durationInSeconds / 60;
            totalDurationInHours = totalDurationInMinutes / 60;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return totalDurationInHours;
    }

    public ArrayList<String> parsePolylinePoints(JSONObject waypointsJson) {
        JSONObject polyline = null;
        JSONObject jObjRoutes = null;
        JSONArray routesArray = null;
        JSONArray legsArray = null;
        JSONArray stepsArray = null;
        JSONObject jobSteps = null;
        try {
            routesArray = waypointsJson.getJSONArray("routes");
            jObjRoutes = routesArray.getJSONObject(0);
            legsArray = jObjRoutes.getJSONArray("legs");

        for (int k = 0; k < legsArray.length(); k++) {
            joBjLegs = legsArray.getJSONObject(k);
            stepsArray = joBjLegs.getJSONArray("steps");
                for (int i = 0; i < stepsArray.length(); i++) {
                    try {
                        jobSteps = stepsArray.getJSONObject(i);
                        polyline = jobSteps.getJSONObject("polyline");
                        points = polyline.getString("points");
                        polylinePoints.add(points);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        } catch (JSONException e) {
                e.printStackTrace();
            }
        return polylinePoints;
    }
}
