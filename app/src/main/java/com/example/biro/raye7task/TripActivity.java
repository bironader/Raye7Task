package com.example.biro.raye7task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripActivity extends AppCompatActivity {

    @BindView(R.id.recyler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TripActivity.this);
        recyclerView.setLayoutManager(layoutManager);


        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(new RoutingListener() {

                    @Override
                    public void onRoutingFailure(RouteException e) {
                        Toast.makeText(TripActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRoutingStart() {

                    }


                    @Override
                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

                        TripAdapter tripAdapter = new TripAdapter(arrayList, TripActivity.this);
                        recyclerView.setAdapter(tripAdapter);


                    }

                    @Override
                    public void onRoutingCancelled() {
                        Toast.makeText(TripActivity.this, "cancled", Toast.LENGTH_LONG).show();
                    }
                })
                .waypoints(GoogleMaps.getStart(), GoogleMaps.getEnd())
                .alternativeRoutes(true)
                .build();
        routing.execute();


    }
}
