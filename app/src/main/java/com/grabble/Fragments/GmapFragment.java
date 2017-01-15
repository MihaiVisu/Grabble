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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grabble.CustomClasses.KMLParser;
import com.grabble.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GmapFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private Circle grabbingRadius, lineOfSight;

    // distances in meters
    private int lineOfSightDistance = 50,
                grabbingRadiusDistance = 10;

    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Marker> visibleMarkers = new ArrayList<>();
    private ArrayList<Marker> markersInRadius = new ArrayList<>();
    private Map<Marker, Boolean> grabbedMarkers = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gmaps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                if (markersInRadius.isEmpty()) {
                    message = "No Letter Grabbed!";
                }
                else message = "New Letters Grabbed!";
                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                for (Marker marker : markersInRadius) {
                    marker.setVisible(false);
                    // mark marker as grabbed
                    grabbedMarkers.put(marker, true);
                }

                markersInRadius.clear();
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        MapFragment fragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        fragment.getMapAsync(this);
    }

    private String getUrl() {

        String[] days = {
                "monday", "tuesday", "wednesday", "thursday",
                "friday", "saturday", "sunday"
        };
        Calendar c = Calendar.getInstance();

        return "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/" +
                days[c.get(Calendar.DAY_OF_WEEK)-1] + ".kml";
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop location updates when activity is no longer in focus
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        mMap.setMyLocationEnabled(true);

        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(getUrl());
        }
        else {
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
                locations = new ArrayList<Location>();

                for (KMLParser.Entry entry : result) {
                    String letterDescription = entry.getDescription();
                    LatLng coordinates = entry.getCoordinates();

                    Location loc = new Location("Provider");
                    loc.setLatitude(coordinates.latitude);
                    loc.setLongitude(coordinates.longitude);
                    // add new created location to locations array
                    locations.add(loc);
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
            InputStream stream = null;
            KMLParser kmlParser = new KMLParser();

            List<KMLParser.Entry> entries = null;

            StringBuilder htmlString = new StringBuilder();

            try {
                stream = downloadUrl(urlString);
                entries = kmlParser.parse(stream);
            }
            finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return entries;
        }

        private InputStream downloadUrl(String myUrl) throws IOException, XmlPullParserException {

            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            return conn.getInputStream();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void createLocationRequest() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation!= null && mMap != null && lineOfSight == null && grabbingRadius == null) {
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
        }
        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location Changed " + location.toString());
        if (grabbingRadius != null) {
            grabbingRadius.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        if (lineOfSight != null) {
            lineOfSight.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        // set all visibleMarkers to false
        for (Marker marker: visibleMarkers) {
            marker.setVisible(false);
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

            if (distance[0] < lineOfSightDistance && !grabbedMarkers.containsKey(marker)) {
                marker.setVisible(true);
                visibleMarkers.add(marker);
            }

            if (grabbingDistance[0] < grabbingRadiusDistance && !grabbedMarkers.containsKey(marker)) {
                markersInRadius.add(marker);
            }
        }

    }

}
