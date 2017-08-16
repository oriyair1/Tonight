package com.example.or.tonight;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Or on 25/07/2017.
 */

public class PeopleGoingAdapter extends RecyclerView.Adapter<PeopleGoingAdapter.PersonViewHolder> {

    ArrayList<PersonGoing> people;
    LayoutInflater inflater;

    public PeopleGoingAdapter(ArrayList<PersonGoing> people, LayoutInflater inflater) {
        this.people = people;
        this.inflater = inflater;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.person_goes_view_holder, parent, false);
        PersonViewHolder holder = new PersonViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.setUpTexts(position);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView gender;
        TextView age;
        TextView time;

        public PersonViewHolder(View v) {
            super(v);

            name = (TextView)v.findViewById(R.id.person_going_name);
            gender = (TextView)v.findViewById(R.id.person_going_gender);
            age = (TextView)v.findViewById(R.id.person_going_age);
            time = (TextView)v.findViewById(R.id.person_going_time);
        }

        public void setUpTexts(int position) {
            name.setText(people.get(position).getName());
            gender.setText("Gender: " + people.get(position).getGender());
            age.setText("Age: " + people.get(position).getAge());
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(people.get(position).getTime());
            String hour;
            String minute;
            if(c.get(Calendar.HOUR_OF_DAY) < 10) hour = "0" + c.get(Calendar.HOUR_OF_DAY);
            else hour = c.get(Calendar.HOUR_OF_DAY) + "";

            if(c.get(Calendar.MINUTE) < 10) minute = "0" + c.get(Calendar.MINUTE);
            else minute = c.get(Calendar.MINUTE) + "";
            time.setText(hour + ":" + minute);

        }
    }
}
