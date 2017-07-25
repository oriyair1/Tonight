package com.example.or.tonight;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    TextView registerButton;
    TextView dateBox;
    EditText usernameBox;
    EditText passwordBox;
    DatabaseReference database;
    RadioGroup genderPick;
    boolean dateIsSet;
    Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dateIsSet = false;
        registerButton = (TextView)findViewById(R.id.register_button);
        usernameBox = (EditText)findViewById(R.id.register_username);
        passwordBox = (EditText)findViewById(R.id.register_password);
        dateBox = (TextView)findViewById(R.id.register_date);
        genderPick = (RadioGroup) findViewById(R.id.register_gender);



        database = FirebaseDatabase.getInstance().getReference();
        database = database.child("Users");

    }

    public void registerDateClicked(View v) {
        date = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateBox.setText(datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear());
                dateIsSet = true;
                date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                date.set(Calendar.MONTH, datePicker.getMonth());
                date.set(Calendar.YEAR, datePicker.getYear());
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this, listener, date.get(Calendar.YEAR) - 16, date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        Calendar d = Calendar.getInstance();
        d.set(Calendar.YEAR, d.get(Calendar.YEAR) - 16);
        dialog.getDatePicker().setMaxDate(d.getTimeInMillis());
        dialog.show();

    }

    public void registerClicked(View v) {


        if (usernameBox.getText().toString().length() >= 3 && passwordBox.getText().toString().length() >=5) {

            database.child(usernameBox.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        usernameBox.setError("Username already exists!");
                    }
                    else {
                        database.child(usernameBox.getText().toString()).child("password").setValue(passwordBox.getText().toString());
                        String gender;
                        if(genderPick.getCheckedRadioButtonId() == R.id.male_pick)
                            gender = "male";
                        else
                            gender = "female";
                        database.child(usernameBox.getText().toString()).child("gender").setValue(gender);
                        database.child(usernameBox.getText().toString()).child("date").setValue(date.getTimeInMillis());
                        Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
