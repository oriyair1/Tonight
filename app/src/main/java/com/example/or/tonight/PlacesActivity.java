package com.example.or.tonight;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener {

    RecyclerView recyclerView;
    boolean loggedIn;
    SharedPreferences preferences;
    final int REQUEST_LOCATION = 101;
    LocationRequest locationRequest;
    GoogleApiClient googleClient;
    Location currentLocation;
    PlacesAdapter adapter;
    LocationManager locationManager;
    ConstraintLayout enableGpsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        preferences = getSharedPreferences("data", MODE_PRIVATE);

        recyclerView = (RecyclerView) findViewById(R.id.places_recycler);

        enableGpsLayout = (ConstraintLayout) findViewById(R.id.enable_gps_layout);

        setUpRecyclerView();



    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleClient != null && googleClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleClient, this);
            googleClient.disconnect();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!preferences.getString("user", "").equals(""))
            loggedIn = true;
        else {
            loggedIn = false;
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        setupGPS();


    }

    public void setupGPS() {
        if (ActivityCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            locationRequest = new LocationRequest();
            locationRequest.setInterval(40000);
            locationRequest.setFastestInterval(20000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            googleClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            googleClient.connect();

            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleClient);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            turnOnGPS();

            locationManager.addGpsStatusListener(new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int i) {
                    switch(i) {
                        case GpsStatus.GPS_EVENT_STARTED:
                            enableGpsLayout.setVisibility(View.GONE);
                            break;
                        case GpsStatus.GPS_EVENT_STOPPED:
                            enableGpsLayout.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }



    public void setUpRecyclerView(){
        DatabaseReference firebasePlaces = FirebaseDatabase.getInstance().getReference().child("Places");
        firebasePlaces.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Place> places = new ArrayList<>();

                for(DataSnapshot d : dataSnapshot.getChildren()) {


                    Location dest = new Location("");
                    dest.setLatitude(Double.valueOf(d.child("Latitude").getValue().toString()));
                    dest.setLongitude(Double.valueOf(d.child("Longitude").getValue().toString()));
                    float distance;
                    if(currentLocation == null) distance = 0;
                    else distance = currentLocation.distanceTo(dest) / 1000;
                    places.add(new Place(d.getKey().toString()  , d.child("photo").getValue().toString(), distance, 19));
                }

                adapter = new PlacesAdapter(places, PlacesActivity.this, LayoutInflater.from(PlacesActivity.this));
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(PlacesActivity.this, 2));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setAdapterPlacesList() {
        DatabaseReference firebasePlaces = FirebaseDatabase.getInstance().getReference().child("Places");
        firebasePlaces.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.places.clear();
                for(DataSnapshot d : dataSnapshot.getChildren()) {

                    Location dest = new Location("");
                    dest.setLatitude(Double.valueOf(d.child("Latitude").getValue().toString()));
                    dest.setLongitude(Double.valueOf(d.child("Longitude").getValue().toString()));
                    float distance;
                    if(currentLocation == null) distance = 0;
                    else distance = currentLocation.distanceTo(dest) / 1000;
                    adapter.places.add(new Place(d.getKey().toString()  , d.child("photo").getValue().toString(), distance, 19));
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void turnOnGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(PlacesActivity.this, 1);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();

                            }
                            break;

                    }
                }
            });
        }

    }

    public void turnOnGPS(View v) {
        turnOnGPS();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if(adapter != null){
            setAdapterPlacesList();
        }
        Toast.makeText(this, "Longitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Can't get your current location", Toast.LENGTH_LONG).show();
    }
}
