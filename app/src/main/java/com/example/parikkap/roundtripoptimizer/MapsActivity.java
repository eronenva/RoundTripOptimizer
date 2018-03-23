package com.example.parikkap.roundtripoptimizer;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    // Variables
    private GoogleMap mMap;
    private ArrayList<String> newDestination = new ArrayList<>();
    private ArrayList<Double> distanceAndDurationArray = new ArrayList<>();
    private ArrayList<String> responseOfWaypoints = new ArrayList<>();
    private EditText startLoc;
    private Button addDestinationButton;
    private int i = 0;
    private EditText input;
    private ViewGroup placeholder;
    private BottomSheetBehavior mBottomSheetBehavior;
    private EditText newInput;
    private JsonParse jsonParse;
    private int placeholderId = R.id.placeholder;
    private ArrayList<Integer> newDestinationId = new ArrayList<>();
    private PolylineOptions options = new PolylineOptions().color(Color.RED);
    private LatLng[] latLngs;
    private ArrayList<String> polylinePoints = new ArrayList<>();
    private String wayPointsOrder = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = mBottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(200);
        startLoc = (EditText) findViewById(R.id.StartLoc);
        addDestinationButton = (Button) findViewById(R.id.AddDestinationButton);
        addDestinationButton.setOnClickListener(onClick());
        placeholder = (ViewGroup) this.findViewById(placeholderId);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    //executes button click
    private View.OnClickListener onClick() {
        return new View.OnClickListener() {

            @Override
            //Adds a new Layout with and calls on createNewEditText.
            public void onClick(View v) {
                createNewEditText();
            }
        };
    }
    /*
    ** Creates a new Editext box and saves the id in newDestinationId
     */
    private EditText createNewEditText() {
        final ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        newInput = new EditText(this.getApplicationContext());
        newInput.setLayoutParams(lparams);
        newInput.setHint("Enter desired location");
        newInput.setId(i);
        placeholder.addView(newInput);
        newDestinationId.add(i);
        i++;
        return newInput;
    }

    public void calcRouteButton(View v) {
        double totDistance = 0;
        double totDuration = 0;
        newDestination.add(startLoc.getText().toString().trim());
        createNewEditText();
        for (int i = 0; i < newDestinationId.size(); i++) {
            EditText nextDestinations = (EditText) placeholder.findViewById(newDestinationId.get(i));
            newDestination.add(nextDestinations.getText().toString().trim());
        }

        try {
            JSONObject response;
            //Executes asyncTask to get JSONOBJ
            response = new getWayPoints().execute(newDestination).get();
            jsonParse = new JsonParse();
            //Runs method in JsonParse.java and gets latAndLongs of user input.
            latLngs = jsonParse.ParseLatAndLang(response,newDestination);
            //Gets polylinePoints that ables us to map the route.
            polylinePoints = jsonParse.parsePolylinePoints(response);
            totDistance = jsonParse.CalculateDistance(response);
            totDuration = jsonParse.CalculateDuration(response);

            setCityLatAndLong(latLngs);
            addPolylinePoints(polylinePoints);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String distance = "";
        String duration = "";

        totDistance = Math.round(totDistance);
        distance = Double.toString(totDistance);
        duration = Double.toString(totDuration);

        String[] durations = duration.split("\\.");
        String hours = durations[0];
        String min = durations[1];

        TextView distanceTx = (TextView) findViewById(R.id.distance);
        TextView durationTx = (TextView) findViewById(R.id.duration);

        distanceTx.setText(distance + " Km");
        durationTx.setText(hours + " h " + min + " min ");

    }

    public void setCityLatAndLong(LatLng[] latAndLong){
        for(int i = 0; i < latAndLong.length-1;i++){
            mMap.addMarker(new MarkerOptions().title("Fastest route: "+ i).position(latAndLong[i])).showInfoWindow();
        }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latAndLong[0]));

    }

    public void addPolylinePoints(ArrayList<String> points){
        for(int i = 0; i < points.size();i++){
            options.width(5);
            List<LatLng> polylineLat = PolyUtil.decode(points.get(i));
            options.addAll(polylineLat);
            mMap.addPolyline(options);
        }
    }
}
