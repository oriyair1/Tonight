package com.example.or.tonight;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class PlaceActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbar;
    Toolbar toolbar;
    ImageView imageView;
    RecyclerView recyclerView;
    PeopleGoingAdapter adapter;
    DatabaseReference placeDatabase;
    DatabaseReference usersDatabase;
    int peopleGoingSize;
    SharedPreferences preferences;
    RelativeLayout askLayout;
    Calendar date;

    String name;
    int ages;
    float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        peopleGoingSize = 0;
        name = "";
        name = getIntent().getStringExtra("place_name");
        ages = getIntent().getIntExtra("place_ages", 0);
        distance = getIntent().getFloatExtra("place_distance", 0);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(name);

        preferences = getSharedPreferences("data", MODE_PRIVATE);






        recyclerView = (RecyclerView)findViewById(R.id.people_going_recycler_view);
        imageView = (ImageView)findViewById(R.id.place_image);
        askLayout = (RelativeLayout)findViewById(R.id.ask_layout);

        Animation a = AnimationUtils.loadAnimation(this, R.anim.anim1);
        a.setInterpolator(this, android.R.interpolator.linear);
        a.setDuration(500);

        askLayout.setAnimation(a);



        FirebaseDatabase.getInstance().getReference().child("Logos").child(name).child("p").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoUrl = dataSnapshot.getValue().toString();
                Picasso.with(getApplicationContext()).load(photoUrl).into(imageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        date = Calendar.getInstance();
        checkIfDatabaseContainsPast();

        placeDatabase = FirebaseDatabase.getInstance().getReference().child("Places").child(name).child(date.get(Calendar.DAY_OF_MONTH) + "," + (date.get(Calendar.MONTH) + 1) + "," + date.get(Calendar.YEAR));

        setUpRecyclerView();

    }

    public void checkIfDatabaseContainsPast(){
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Logos").child(name);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    if(! (d.getKey().toString().equals("p") || d.getKey().toString().equals(date.get(Calendar.DAY_OF_MONTH) + "," + (date.get(Calendar.MONTH) + 1) + "," + date.get(Calendar.YEAR))) ) {
                        db.child(d.getKey().toString()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void comingClick(View v){
        date = Calendar.getInstance();
        checkIfDatabaseContainsPast();
        placeDatabase = FirebaseDatabase.getInstance().getReference().child("Logos").child(name).child(date.get(Calendar.DAY_OF_MONTH) + "," + (date.get(Calendar.MONTH) + 1) + "," + date.get(Calendar.YEAR));
        placeDatabase.child(preferences.getString("user", null)).setValue(System.currentTimeMillis()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                refreshRecyclerView();
            }
        });

    }

    public void notComingClick(View v){
        date = Calendar.getInstance();
        checkIfDatabaseContainsPast();
        placeDatabase = FirebaseDatabase.getInstance().getReference().child("Logos").child(name).child(date.get(Calendar.DAY_OF_MONTH) + "," + (date.get(Calendar.MONTH) + 1) + "," + date.get(Calendar.YEAR));
        placeDatabase.child(preferences.getString("user", null)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                refreshRecyclerView();
            }
        });
    }

    public void refreshRecyclerView(){
        final ArrayList<PersonGoing> people = new ArrayList<>();

        placeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                peopleGoingSize = (int) dataSnapshot.getChildrenCount();

                if(dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        final PersonGoing person = new PersonGoing();
                        String n = d.getKey().toString();
                        person.setName(n);
                        person.setTime(Long.parseLong(d.getValue().toString()));

                        usersDatabase.child(n).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                person.setGender(dataSnapshot.child("gender").getValue().toString());
                                person.setAgeByBirth(Long.parseLong(dataSnapshot.child("date").getValue().toString()));

                                people.add(person);


                                if (people.size() == peopleGoingSize) {
                                    for (int i = 0; i < people.size(); i++) {
                                        int minIndex = i;
                                        for (int k = i; k < people.size(); k++) {
                                            if (people.get(k).getTime() < people.get(minIndex).getTime()) {
                                                minIndex = k;
                                            }
                                        }
                                        PersonGoing p = people.get(minIndex);
                                        people.remove(minIndex);
                                        people.add(0, p);
                                    }
                                    adapter.people = people;
                                    adapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else{
                    adapter.people = people;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setUpRecyclerView() {
        final ArrayList<PersonGoing> people = new ArrayList<>();

        placeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                peopleGoingSize = (int) dataSnapshot.getChildrenCount();

                if(dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        final PersonGoing person = new PersonGoing();
                        String n = d.getKey().toString();
                        person.setName(n);
                        person.setTime(Long.parseLong(d.getValue().toString()));

                        usersDatabase.child(n).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                person.setGender(dataSnapshot.child("gender").getValue().toString());
                                person.setAgeByBirth(Long.parseLong(dataSnapshot.child("date").getValue().toString()));

                                people.add(person);


                                if (people.size() == peopleGoingSize) {
                                    for (int i = 0; i < people.size(); i++) {
                                        int minIndex = i;
                                        for (int k = i; k < people.size(); k++) {
                                            if (people.get(k).getTime() < people.get(minIndex).getTime()) {
                                                minIndex = k;
                                            }
                                        }
                                        PersonGoing p = people.get(minIndex);
                                        people.remove(minIndex);
                                        people.add(0, p);
                                    }
                                    adapter = new PeopleGoingAdapter(people, PlaceActivity.this.getLayoutInflater());
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(PlaceActivity.this));
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else{
                    adapter = new PeopleGoingAdapter(people, PlaceActivity.this.getLayoutInflater());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PlaceActivity.this));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
