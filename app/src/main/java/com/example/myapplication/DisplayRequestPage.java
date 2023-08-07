package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class DisplayRequestPage extends AppCompatActivity {
    FirebaseFirestore store;
    TextView initiatedByTextView, descriptionTextView, locationTextView, centerTextView,
            categoryTextView, subCategoryTextView, severityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_page);

        initiatedByTextView = findViewById(R.id.initiatedByTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        locationTextView = findViewById(R.id.locationTextView);
        centerTextView = findViewById(R.id.centerTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        subCategoryTextView = findViewById(R.id.subCategoryTextView);
        severityTextView = findViewById(R.id.severityTextView);

        store = FirebaseFirestore.getInstance();

        String serviceId = getIntent().getStringExtra("serviceId");

        store.collection("ServiceRequest").document(serviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String initiatedBy = documentSnapshot.getString("InitiatedBy");
                        String description = documentSnapshot.getString("Description");
                        String location = documentSnapshot.getString("Location");
                        String center = documentSnapshot.getString("Center");
                        String category = documentSnapshot.getString("Category");
                        String subCategory = documentSnapshot.getString("SubCategory");
                        String severity = documentSnapshot.getString("Severity");

                        String initiatedByText = "Initiated By: " + initiatedBy;
                        String centerText = "Center: " + center;

                        initiatedByTextView.setText(initiatedByText);

                        descriptionTextView.setText("Description: " + description);
                        locationTextView.setText("Location: " + location);
                        centerTextView.setText(centerText);
                        categoryTextView.setText("Category: " + category);
                        subCategoryTextView.setText("SubCategory: " + subCategory);
                        severityTextView.setText("Severity: " + severity);
                    }
                })
                .addOnFailureListener(e -> {
                });

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToEditPage();
            }
        });
    }

    private void navigateToEditPage() {
        String serviceId = getIntent().getStringExtra("serviceId");
        Intent intent = new Intent(this, AddServiceRequest.class);
        intent.putExtra("serviceId", serviceId);
        startActivity(intent);
    }

}
