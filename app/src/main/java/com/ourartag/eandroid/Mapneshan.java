package com.ourartag.eandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Looper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.neshan.mapsdk.MapView;
import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.model.Marker;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.google.android.gms.location.FusedLocationProviderClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Mapneshan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mapneshan extends Fragment implements View.OnClickListener, LocationListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btnBack, btnGoMe;
    private View view;
    private FusedLocationProviderClient mFusedLocationClient;
    private String latitudeText, longitudeText;
    int PERMISSION_ID = 44;
    private MapView map;
    private Marker marker;
    private Config myConfigs;
    esTimer baseTimer;
    public Mapneshan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Mapneshan.
     */
    // TODO: Rename and change types and number of parameters
    public static Mapneshan newInstance(String param1, String param2) {
        Mapneshan fragment = new Mapneshan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        myConfigs = new Config(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mapneshan, container, false);
        btnBack = (Button) view.findViewById(R.id.mapBack);
        btnBack.setOnClickListener(this);
        btnGoMe = (Button) view.findViewById(R.id.mapGoOnMe);
        btnGoMe.setOnClickListener(this);
        latitudeText = "35.767234";
        longitudeText = "51.330743";
        map = view.findViewById(R.id.map);
        map.setTrafficEnabled(true);

        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
        map.getSettings().setZoomControlsEnabled(true);
        baseTimer = new esTimer(new Runnable() {public void run() {baseLoop();}}, 10, 6000, false);

        return view;
    }
    public void baseLoop(){
        getLastLocation();
        map.setZoom(14, 0);
        Float temfll = Float.valueOf(latitudeText);
        Float tempfln = Float.valueOf(longitudeText);
//                map.getSettings().setZoomControlsEnabled(true);
        addUserMarker(new LatLng(temfll, tempfln));

//                map.addMarker(createMarker(latlng));
        map.moveCamera(new LatLng(temfll, tempfln), .5f);
    }
    private void addUserMarker(LatLng loc) {
        //remove existing marker from map
        if (marker != null) {
            marker.setLatLng(loc);
        } else {
            // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
            // and then call buildStyle method on it. This method returns an object of type MarkerStyle
            MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
            markStCr.setSize(30f);
            markStCr.setAnchorPointY(0);
            markStCr.setAnchorPointX(0);
            markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.markerg)));
            MarkerStyle markSt = markStCr.buildStyle();

            // Creating user marker
            marker = new Marker(loc, markSt);

            // Adding user marker to map!
            map.addMarker(marker);

        }

    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.mapBack){
            baseTimer.stopTimer();
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(Mapneshan.this).commit();
            ((MainActivity)getActivity()).turnOffFrag();
        }else if(v.getId()==R.id.mapGoOnMe){
            getLastLocation();
            map.setZoom(14, 0);
            Float temfll = Float.valueOf(latitudeText);
            Float tempfln = Float.valueOf(longitudeText);
//                map.getSettings().setZoomControlsEnabled(true);
            addUserMarker(new LatLng(temfll, tempfln));

//                map.addMarker(createMarker(latlng));
            map.moveCamera(new LatLng(temfll, tempfln), .5f);
            baseTimer.startTimer();
        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeText = location.getLatitude() + "";
                            longitudeText = location.getLongitude() + "";
                            myConfigs.set_myLati(latitudeText);
                            myConfigs.set_myLoni(longitudeText);

                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeText = mLastLocation.getLatitude() + "";
            longitudeText = mLastLocation.getLongitude() + "";
            myConfigs.set_myLati(latitudeText);
            myConfigs.set_myLoni(longitudeText);
        }
    };


    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}