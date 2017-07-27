package com.example.or.tonight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView logInButton;
    EditText usernameBox;
    EditText passwordBox;
    DatabaseReference usersDatabase;
    SharedPreferences preferences;
    SharedPreferences.Editor se;
    final int REQUEST_EXIT = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("data", MODE_PRIVATE);
        se = preferences.edit();

        if(!preferences.getString("user", "").equals("")) {
            startActivity(new Intent(getApplicationContext(), PlacesActivity.class));
            finish();
        }

        logInButton = (TextView)findViewById(R.id.log_in_button);
        usernameBox = (EditText)findViewById(R.id.login_username);
        passwordBox = (EditText)findViewById(R.id.login_password);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EXIT && resultCode == RESULT_OK) {
            startActivity(new Intent(getApplicationContext(), PlacesActivity.class));
            finish();
        }
    }

    public void startRegisterActivity(View v) {
        startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_EXIT);
    }

    public void logInClicked(View v) {

        if (usernameBox.getText().toString().length() >= 3 && passwordBox.getText().toString().length() >=5) {

            usersDatabase.child(usernameBox.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("password").getValue().toString().equals(passwordBox.getText().toString())) {
                            se.putString("user", usernameBox.getText().toString()).commit();
                            startActivity(new Intent(getApplicationContext(), PlacesActivity.class));
                            finish();
                        }
                        else
                            passwordBox.setError("Password is incorrect!");
                    }
                    else
                        usernameBox.setError("Username does not exists");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
