package com.example.biro.raye7task;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Biro on 6/8/2017.
 */

public class GoogleMaps {


    private static GoogleMaps instance = null;
    private static ArrayList<Marker> currentMarkerList;
    // keep track with markers and only keep 1 for destinantion and source on map
    public static ArrayList<Marker> desMarkerList;
    private static LatLng start;
    private static LatLng end;

//    private static final int[] COLORS = new int[]{R.color.colorBLue,R.color.colorPrimaryDark,R.color.colorAccent,R.color.colorYellow,R.color.colorDarkGray};

    public static void setPolyline(Polyline polyline) {
        GoogleMaps.polyline = polyline;
    }

    public static Polyline getPolyline() {
        return polyline;
    }

    private static Polyline polyline;

    public static LatLng getStart() {
        return start;
    }

    public static LatLng getEnd() {
        return end;
    }

    private Context context;


    private GoogleMaps(Context context) {
        this.context = context;
        currentMarkerList = new ArrayList<Marker>();
        desMarkerList = new ArrayList<Marker>();

    }

    public static GoogleMaps getInstance(Context context) {
        if (instance == null) {
            instance = new GoogleMaps(context);
        }
        return instance;
    }

    // return address of specific location
    public String getAddress(LatLng loc) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
        Address obj = addresses.get(0);
        String add = "  " + obj.getAddressLine(0);
        return add;
    }

    public void setupCurrentLocation(LatLng currentLoc, GoogleMap mMap) throws IOException {
//        if (Utils.isNetworkConnected(context)) {
        start = currentLoc;
        if (polyline != null)
            polyline.remove();
        if (currentMarkerList.size() != 0) // validate only one marker for source location
        {
            currentMarkerList.get(0).remove();
            currentMarkerList.remove(0);

        }

        Marker m1 = mMap.addMarker(new MarkerOptions()
                .position(currentLoc)
                .title(getAddress(currentLoc)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17), 2000, null);
        mMap.setTrafficEnabled(true);
        m1.showInfoWindow();
        currentMarkerList.add(m1);


    }

    // get destination location
    public void setupDestLocation(LatLng desLoc, GoogleMap mMap) throws IOException {

        end = desLoc;
        if (polyline != null)
            polyline.remove();
        if (desMarkerList.size() != 0) // validate only one marker for destination location

        {
            desMarkerList.get(0).remove();
            desMarkerList.remove(0);

        }
        Marker m2 = mMap.addMarker(new MarkerOptions()
                .position(desLoc)
                .title(getAddress(desLoc))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(desLoc, 17), 1000, null);
        mMap.setTrafficEnabled(true);
        m2.showInfoWindow();
        desMarkerList.add(m2);


    }
//
//    public void showRoute( GoogleMap mMap) {
//        Routing routing = new Routing.Builder()
//                .travelMode(AbstractRouting.TravelMode.DRIVING)
//                .withListener(new RoutingListener() {
//
//                    @Override
//                    public void onRoutingFailure(RouteException e) {
//                        Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onRoutingStart() {
//
//                    }
//
//
//                    @Override
//                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
////                        for (int j = 0; j < arrayList.size(); j++) {
////
////                            int colorIndex = j % COLORS.length;
////                            PolylineOptions polyOptions = new PolylineOptions();
////                            polyOptions.color(context.getResources().getColor(COLORS[colorIndex]));
////                            polyOptions.width(10 + j);
////                            polyOptions.addAll(arrayList.get(j).getPoints());
////                            polyline = mMap.addPolyline(polyOptions);
////
////
////                        }
//                        TripAdapter tripAdapter = new TripAdapter(arrayList);
//                        recyclerView.setAdapter(tripAdapter);
//
//                    }
//
//                    @Override
//                    public void onRoutingCancelled() {
//                        Toast.makeText(context, "cancled", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .waypoints(start, end)
//                .alternativeRoutes(true)
//                .build();
//        routing.execute();
//    }


}
