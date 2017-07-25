package com.example.or.tonight;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Or on 20/07/2017.
 */


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

    ArrayList<Place> places;
    LayoutInflater inflater;
    Context activityContext;

    public PlacesAdapter(ArrayList<Place> places, Context activityContext, LayoutInflater inflater){

        this.places = new ArrayList<>();
        this.places.addAll(places);
        this.activityContext = activityContext;
        this.inflater = inflater;
    }





    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.place_view_holder, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.setTexts(position);
        holder.setUpClickListener(position);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView nameText;
        TextView agesText;
        TextView distanceText;
        View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            img = (ImageView)view.findViewById(R.id.recycler_place_img);
            nameText = (TextView)view.findViewById(R.id.recycler_place_name);
            agesText = (TextView)view.findViewById(R.id.recycler_place_age);
            distanceText = (TextView)view.findViewById(R.id.recycler_place_distance);



        }

        public void setTexts(int position) {
            Log.i("hiiiiiiiiii", "download: " + places.get(position).getDownloadURL());
            Picasso.with(activityContext.getApplicationContext()).load(places.get(position).getDownloadURL()).transform(new RoundedCornersTransformation(10, 0)).into(img);
            nameText.setText(places.get(position).getName());
            agesText.setText("Ages: " + places.get(position).getAges());
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            distanceText.setText("Distance: " + df.format(places.get(position).getDistance()) + " km");
        }

        public void setUpClickListener(final int position) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activityContext, PlaceActivity.class);
                    i.putExtra("place_name", places.get(position).getName());
                    i.putExtra("place_ages", places.get(position).getAges());
                    i.putExtra("place_distance", places.get(position).getDistance());
                    activityContext.startActivity(i);
                }
            });
        }


    }


}
