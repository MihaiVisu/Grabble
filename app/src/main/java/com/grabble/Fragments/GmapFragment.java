package com.grabble.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grabble.R;


public class GmapFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private LatLng myPosition;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private Circle grabbingRadius, lineOfSight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gmaps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        MapFragment fragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        fragment.getMapAsync(this);
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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        mMap.setMyLocationEnabled(true);

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void createLocationRequest() {

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
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
        if (mLastLocation!= null && mMap != null) {
            lineOfSight = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .radius(100)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(Color.parseColor("#70303F9F")));
            grabbingRadius = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .radius(50)
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
        if (grabbingRadius != null) {
            grabbingRadius.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        if (lineOfSight != null) {
            lineOfSight.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

}
