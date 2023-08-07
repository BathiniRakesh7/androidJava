package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void Add(View view) {
        try {
            Intent intent = new Intent(getApplicationContext(), AddServiceRequest.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Edit(View view) {
        String serviceId = "1250"; // Replace with the actual record ID you want to pass

        Intent intent = new Intent(getApplicationContext(), AddServiceRequest.class);
        intent.putExtra("serviceId", serviceId);
        startActivity(intent);
    }
}