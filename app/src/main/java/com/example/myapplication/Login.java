package com.example.myapplication;

import static com.example.myapplication.MainActivity2.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText Email,Password;
    Button LoginBtn;
    TextView CreateBtn;
    ProgressBar progressBar2;
    FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);
        LoginBtn = findViewById(R.id.loginbtn);
        CreateBtn = findViewById(R.id.textCreateHere);
        Email = findViewById(R.id.editTextEmail);
        Auth = FirebaseAuth.getInstance();
        progressBar2 = findViewById(R.id.progressBar2);



        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Email.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Password.setError("Password is Required");
                    return;
                }
                if (password.length()<6){
                    Password.setError("Password must be above 6 Characters");
                    return;
                }
                progressBar2.setVisibility(View.VISIBLE);
                Auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.e(TAG, "Authentication failed", task.getException());
                                    Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    progressBar2.setVisibility(View.GONE);
                                }
                            }
                        });


            }

        });


        CreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity2.class));

            }
        });

    }
}