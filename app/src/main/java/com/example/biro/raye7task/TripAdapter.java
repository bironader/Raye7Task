package com.example.biro.raye7task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Biro on 6/10/2017.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private ArrayList<Route> routes;
    Activity context;


    public TripAdapter(ArrayList<Route> routes, Activity context) {
        this.routes = routes;
        this.context = context;
    }


    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("route", (ArrayList<? extends Parcelable>) routes.get(vh.getAdapterPosition()).getPoints());

                context.setResult(RESULT_OK, intent); // send data to on activity result
                context.finish();


            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(TripAdapter.ViewHolder holder, int position) {


        holder.tripDistance.setText(routes.get(position).getDistanceText());
        holder.tripHours.setText(routes.get(position).getDurationText());
    }


    @Override
    public int getItemCount() {
        return routes.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trip_hours)
        TextView tripHours;

        @BindView(R.id.trip_distance)
        TextView tripDistance;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


    }

}





