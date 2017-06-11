package com.example.biro.raye7task;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


import static com.example.biro.raye7task.R.id.map;

public class MainScreen extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE1 = 1;  // keep track which editbox user clicked on
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;
    public static final int REQUEST_LOCATION = 99;
    public static final int REQUEST_ACTIVITY_CODE = 3; // send data back to map activity to show route

    @BindView(R.id.edit_from)
    EditText fromEditText;
    @BindView(R.id.edit_to)
    EditText toEditText;
    @BindView(R.id.current_location)
    ImageButton currentLocation;


    @BindView(R.id.find_route)
    Button findRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ButterKnife.bind(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this) // init google api client
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();


        currentLocation.setOnClickListener(new View.OnClickListener() { // when user click on location icon
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(MainScreen.this)) {

                    try {

                        LatLng x = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()); // get last location which updated in on location changed
                        GoogleMaps.getInstance(MainScreen.this)
                                .setupCurrentLocation(x, mMap);
                        updateUi(mLastLocation);


                    } catch (NullPointerException e) {
                        Toast.makeText(MainScreen.this, "Error try again ", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else
                    Toast.makeText(MainScreen.this, "No internet connection", Toast.LENGTH_LONG).show();


            }
        });

        fromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MainScreen.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE1);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
        toEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // google api auto complete
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MainScreen.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });


        findRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (GoogleMaps.getPolyline() != null)
                    GoogleMaps.getPolyline().remove(); //remove any route in the map
                if (GoogleMaps.getEnd() != null) {
                    Intent intent = new Intent(MainScreen.this, TripActivity.class);
                    MainScreen.this.startActivityForResult(intent, REQUEST_ACTIVITY_CODE);
                    overridePendingTransition(R.anim.slideup, R.anim.stay);

                }
                else Toast.makeText(MainScreen.this,"Select Destintaion",Toast.LENGTH_LONG).show();


            }
        });

        fromEditText.setKeyListener(null);
        toEditText.setKeyListener(null);


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (Utils.isNetworkConnected(this)) { // check for the internet

            if (!Utils.isGpsEnabled(this)) { // if the location service not enabled
                Utils.showDialog(this); // show alert and force user to enable it
            } else {
                int permissionCheck = ContextCompat.checkSelfPermission(this, // if user change permission of app in run time sdk marshmallow above
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, // request permission
                            REQUEST_LOCATION);

                }
                else // premission granted
                {
                    LocationRequest mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(1000);
                    mLocationRequest.setFastestInterval(1000);
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }


                SupportMapFragment mF = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(map);

                mF.getMapAsync(this); // show the map


            }
        } else
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();


    }


    // when map is showen get current location of the user and make it trip source as default
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;


        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (Utils.isNetworkConnected(MainScreen.this)) {
                    try {
                        GoogleMaps.getInstance(MainScreen.this).setupDestLocation(latLng, mMap);
                        updateUi(GoogleMaps.getInstance(MainScreen.this).getAddress(latLng), 2);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(MainScreen.this, "No internet connection ", Toast.LENGTH_LONG).show();
            }
        });


    }


    protected void updateUi(Location l) throws IOException {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
        Address obj = addresses.get(0);
        String add = "  " + obj.getAddressLine(0);
        add = add + "\n" + obj.getCountryName();
        add = add + "\n" + obj.getCountryCode();
        fromEditText.setText(add);


    }

    protected void updateUi(String addreess, int code) throws IOException {
        if (code == 1)
            fromEditText.setText(addreess);
        else if (code == 2)
            toEditText.setText(addreess);

    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1) { //check on request code
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                try {
                    GoogleMaps.getInstance(this).setupCurrentLocation(place.getLatLng(), mMap);
                    updateUi(place.getAddress().toString(), requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Toast.makeText(this, "tryagain later", Toast.LENGTH_LONG).show();

            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                try {
                    GoogleMaps.getInstance(this).setupDestLocation(place.getLatLng(), mMap);
                    updateUi(place.getAddress().toString(), requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Toast.makeText(this, "tryagain later", Toast.LENGTH_LONG).show();

            }
        } else if (requestCode == REQUEST_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {

                List<LatLng> points = data.getParcelableArrayListExtra("route"); // get list of points from trip activity
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(Color.CYAN);
                polyOptions.width(30);
                polyOptions.addAll(points);
                Polyline polyline = mMap.addPolyline(polyOptions); // show route on map
                GoogleMaps.setPolyline(polyline);

            }
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location; // update location


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // permission granted
                    LocationRequest mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(1000);
                    mLocationRequest.setFastestInterval(1000);
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this); // on location changed get call

                } else {// premission denied close the app
                    finish();
                    System.exit(0);
                }
            }
        }
    }
}
