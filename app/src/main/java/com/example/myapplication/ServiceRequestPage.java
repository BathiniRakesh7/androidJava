package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ServiceRequestPage extends AppCompatActivity {
    FirebaseFirestore store;
    LinearLayout serviceRequestsLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_request_page);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Service Request Page");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        serviceRequestsLayout = findViewById(R.id.serviceRequestsLayout);

        store = FirebaseFirestore.getInstance();


        store.collection("ServiceRequest")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String initiatedBy = documentSnapshot.getString("InitiatedBy");
                        String location = documentSnapshot.getString("Location");
                        String center = documentSnapshot.getString("Center");
                        String category = documentSnapshot.getString("Category");
                        String subCategory = documentSnapshot.getString("SubCategory");
                        String severity = documentSnapshot.getString("Severity");

                        CardView serviceRequestCardView = (CardView) getLayoutInflater().inflate(
                                R.layout.service_request_card, serviceRequestsLayout, false);


                        TextView initiatedByTextView = serviceRequestCardView.findViewById(R.id.initiatedByTextView);
                        TextView locationTextView = serviceRequestCardView.findViewById(R.id.locationTextView);
                        TextView centerTextView = serviceRequestCardView.findViewById(R.id.centerTextView);
                        TextView categoryTextView = serviceRequestCardView.findViewById(R.id.categoryTextView);
                        TextView subCategoryTextView = serviceRequestCardView.findViewById(R.id.subCategoryTextView);
                        TextView severityTextView = serviceRequestCardView.findViewById(R.id.severityTextView);
                        int violetColor = Color.parseColor("#61428a");

                        locationTextView.setText(makeTextBoldAndColored("Location: ", location, violetColor));
                        centerTextView.setText(makeTextBoldAndColored("Center: ", center, violetColor));
                        categoryTextView.setText(makeTextBoldAndColored("Category: ", category, violetColor));
                        subCategoryTextView.setText(makeTextBoldAndColored("SubCategory: ", subCategory, violetColor));
                        severityTextView.setText(makeTextBoldAndColored("Severity: ", severity, violetColor));
                        initiatedByTextView.setText(makeTextBoldAndColored("Initiated By: ", initiatedBy, violetColor));


                        Button editButton = serviceRequestCardView.findViewById(R.id.editButton);
                        editButton.setOnClickListener(view -> {
                            String serviceId = documentSnapshot.getId();
                            Intent intent = new Intent(getApplicationContext(), AddServiceRequest.class);
                            intent.putExtra("serviceId", serviceId);
                            startActivity(intent);
                        });

                        serviceRequestsLayout.addView(serviceRequestCardView);

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ServiceRequestPage.this, "No Data Found", Toast.LENGTH_SHORT).show();

                });


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private SpannableString makeTextBoldAndColored(String heading, String value, int color) {
        String fullText = heading + value;
        SpannableString spannableString = new SpannableString(fullText);

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, heading.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, heading.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}



