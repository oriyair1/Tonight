package com.example.or.tonight;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);




        recyclerView = (RecyclerView)findViewById(R.id.people_going_recycler_view);
        imageView = (ImageView)findViewById(R.id.place_image);


        FirebaseDatabase.getInstance().getReference().child("Places").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoUrl = dataSnapshot.child("photo").getValue().toString();
                Picasso.with(getApplicationContext()).load(photoUrl).into(imageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        Calendar date = Calendar.getInstance();
        placeDatabase = FirebaseDatabase.getInstance().getReference().child("Places").child(name).child(date.get(Calendar.DAY_OF_MONTH) + "," + (date.get(Calendar.MONTH) + 1) + "," + date.get(Calendar.YEAR));

        setUpRecyclerView();

    }


    public void setUpRecyclerView() {
        final ArrayList<PersonGoing> people = new ArrayList<>();

        placeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                peopleGoingSize = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    final PersonGoing person = new PersonGoing();
                    String n = d.getKey();
                    Log.i("hiiiiiiiiii", n);
                    person.setName(n);

                    usersDatabase.child(n).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                           person.setGender(dataSnapshot.child("gender").getValue().toString());
                           person.setAgeByBirth(Long.parseLong(dataSnapshot.child("date").getValue().toString()));
                            people.add(person);


                            if(people.size() == peopleGoingSize) {
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
