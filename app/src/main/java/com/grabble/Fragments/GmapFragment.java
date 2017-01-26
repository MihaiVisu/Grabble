package com.grabble.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grabble.customclasses.GameState;
import com.grabble.customclasses.KMLParser;
import com.grabble.R;
import com.grabble.customclasses.ProgressHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mbanje.kurt.fabbutton.FabButton;


public class GmapFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private static Location mLastLocation;
    private static Location oldLocation;
    private static LocationRequest mLocationRequest;
    private static GoogleMap mMap;

    // circles representing line of sight and grabbing radius
    private static Circle lineOfSight, grabbingRadius;

    // distances in meters
    private static int grabbingRadiusDistance = 10,
            lineOfSightDistance = 50;

    private GameState state;

    private static ArrayList<Marker> markers = new ArrayList<>();
    private static ArrayList<Marker> visibleMarkers = new ArrayList<>();
    private static ArrayList<Marker> markersInRadius = new ArrayList<>();

    // declare the snackbar which is going to be used in the fragment
    private static Snackbar snackbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gmaps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        state = ((GameState) getActivity().getApplicationContext());
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                if (markersInRadius.isEmpty()) {
                    message = "No Letter Grabbed!";
                } else {
                    message = "New Letters Grabbed!";

                    for (Marker marker : markersInRadius) {
                        marker.setVisible(false);
                        // mark marker as grabbed
                        state.addNewMarker(marker.getId());
                        state.addNewLetter(marker.getTitle());
                    }

                    markersInRadius.clear();
                }
                snackbar.setText(message).show();
            }
        });

        state.checkMilestones(snackbar);

        final FabButton fabButton = (FabButton) getActivity().findViewById(R.id.los_fab);
        final ProgressHelper helper = new ProgressHelper(fabButton, this.getActivity());

        if (state.getLosProgress() > 0) {
            helper.startDeterminate();
        }

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if we have boosters
                if (state.getLosBoosters() > 0) {
                    // if the booster is not started already
                    if (state.getLosProgress() == 0) {
                        helper.startDeterminate();
                        multiplyLineOfSightDistance(2); // double the radius
                        setLineOfSightRadiusAndColor(lineOfSightDistance, R.color.bt_red);
                        hideAllMarkers();
                        state.useLosBooster();
                        updateMarkers(oldLocation, state);
                    } else {
                        snackbar.setText("Booster is already in progress!").show();
                    }
                } else {
                    snackbar.setText("No boosters left!").show();
                }
            }
        });

        mLocationRequest = new LocationRequest();
        setLocationRequestVariables(state.getBatterySavingMode());

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
        MapFragment fragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        fragment.getMapAsync(this);
    }

    public static void multiplyLineOfSightDistance(double val) {
        lineOfSightDistance *= val;
    }

    public static int getLineOfSightDistance() {
        return lineOfSightDistance;
    }

    public static GoogleMap getMap() {
        return mMap;
    }

    private String getUrl() {

        String[] days = {
                "monday", "tuesday", "wednesday", "thursday",
                "friday", "saturday", "sunday"
        };
        Calendar c = Calendar.getInstance();

        return "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/" +
                days[c.get(Calendar.DAY_OF_WEEK) - 1] + ".kml";
    }

    public static void setLineOfSightRadiusAndColor(int radius, int color) {
        lineOfSight.setRadius(radius);
        lineOfSight.setFillColor(color);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        state.activityStopped();
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        mMap = googleMap;
        if (state.getNightMode()) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this.getActivity(), R.raw.night_mode_map));
        }
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // if we have night mode enabled
        if (state.getNightMode()) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.night_mode_map));
        }

        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//            return;
        } else {
            mMap.setMyLocationEnabled(true);
            handleNewLocation();
        }

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(getUrl());
        } else {
            Log.i("No connection available", "No Connection Available");
        }

    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, List<KMLParser.Entry>> {

        @Override
        protected List<KMLParser.Entry> doInBackground(String... params) {
            try {
                return loadXmlFromNetwork(params[0]);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<KMLParser.Entry> result) {

            for (KMLParser.Entry entry : result) {
                String letterDescription = entry.getDescription();
                LatLng coordinates = entry.getCoordinates();

                Location loc = new Location("Provider");
                loc.setLatitude(coordinates.latitude);
                loc.setLongitude(coordinates.longitude);

                // add new created marker to markers array
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title(letterDescription));
                markers.add(marker);
                // set initial visibility of markers to none
                marker.setVisible(false);
            }
        }

        private List<KMLParser.Entry> loadXmlFromNetwork(String urlString) throws
                XmlPullParserException, IOException {
            InputStream stream;
            KMLParser kmlParser = new KMLParser();

            List<KMLParser.Entry> entries = null;

            try {
                stream = downloadUrl(urlString);
                entries = kmlParser.parse(stream);
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return entries;
        }

        private InputStream downloadUrl(String myUrl) throws IOException, XmlPullParserException {

            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            return conn.getInputStream();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        System.out.print("CALLBACK CALLBACK:  " + requestCode);
        switch (requestCode) {
            case 100: {

                System.out.print("GRANTED!!!");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted
                    try {
                        mLastLocation = LocationServices.FusedLocationApi
                                .getLastLocation(mGoogleApiClient);
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                mLocationRequest, this);
                        mMap.setMyLocationEnabled(true);
                        handleNewLocation();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                } else {
                    // permission denied
                    Toast.makeText(getActivity(), "Permission denied to load location",
                            Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void createLocationRequest() {

        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    public static void setLocationRequestVariables(boolean batterySavingMode) {

        // set variables for battery saving mode
        int locationAccuracyMode, interval;

        if (batterySavingMode) {
            locationAccuracyMode = LocationRequest.PRIORITY_LOW_POWER;
            interval = 20;
        } else {
            locationAccuracyMode = LocationRequest.PRIORITY_HIGH_ACCURACY;
            interval = 10;
        }
        mLocationRequest.setInterval(interval * 1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(locationAccuracyMode);
    }

    private void handleNewLocation() {
        if (mLastLocation != null && mMap != null && lineOfSight == null && grabbingRadius == null) {
            lineOfSight = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .radius(lineOfSightDistance)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(Color.parseColor("#70303F9F")));
            grabbingRadius = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .radius(grabbingRadiusDistance)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(Color.parseColor("#703F51B5")));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())
            ));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        createLocationRequest();
        handleNewLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection Failed!");
    }

    public static void updateMarkers(Location location, GameState state) {
        if (location == null) {
            return;
        }
        // clear efficiently visibleMarkers array after location change
        visibleMarkers.clear();
        markersInRadius.clear();

        // set new markers visibility to true
        for (Marker marker: markers) {
            float[] distance = new float[2],
                    grabbingDistance = new float[2];

            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    marker.getPosition().latitude, marker.getPosition().longitude, distance);

            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    marker.getPosition().latitude, marker.getPosition().longitude, grabbingDistance);

            if (distance[0] < lineOfSightDistance && !state.getMarkersGrabbed().contains(marker.getId())) {
                marker.setVisible(true);
                visibleMarkers.add(marker);
            }

            if (grabbingDistance[0] < grabbingRadiusDistance && !state.getMarkersGrabbed().contains(marker.getId())) {
                markersInRadius.add(marker);
            }
        }
    }

    public static void hideAllMarkers() {
        for (Marker marker: visibleMarkers) {
            marker.setVisible(false);
        }
    }

    public static Location getLocation() {
        return oldLocation;
    }


    @Override
    public void onLocationChanged(Location location) {
        if (oldLocation != null) {
            float[] dist = new float[2];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    oldLocation.getLatitude(), oldLocation.getLongitude(), dist);
            state.addDistance(dist[0]);
        }
        oldLocation = location;
        state.checkMilestones(snackbar);
        System.out.println("Location Changed " + location.toString() + " distance:" +
                state.getDistanceTraveled());
        if (grabbingRadius != null) {
            grabbingRadius.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        if (lineOfSight != null) {
            lineOfSight.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                location.getLatitude(), location.getLongitude()
        )));

        // update markers
        hideAllMarkers();
        updateMarkers(location, state);

    }

}
