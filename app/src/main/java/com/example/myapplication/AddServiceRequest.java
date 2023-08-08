package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddServiceRequest extends AppCompatActivity {
    public static final String TAG = "Tag";

    EditText initiatedBy, description;
    TextView title, hintLocation;
    Spinner location, center, category, subCategory, severity;
    Button save, cancel;
    FirebaseFirestore store;
    ProgressBar progressBar3;
    String serviceId;
    Toolbar addServiceRequestToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addservice_request);
        addServiceRequestToolbar = findViewById(R.id.AddServiceToolbar);
        setSupportActionBar(addServiceRequestToolbar);

        getSupportActionBar().setTitle("Add Service Request");


        initiatedBy = findViewById(R.id.InitiatedBy);
        description = findViewById(R.id.Description);
        location = findViewById(R.id.Location);
        center = findViewById(R.id.Center);
        category = findViewById(R.id.Category);
        subCategory = findViewById(R.id.SubCategory);
        severity = findViewById(R.id.Severity);
        save = findViewById(R.id.Savebutton);
        cancel = findViewById(R.id.Cancelbutton);
        progressBar3 = findViewById(R.id.addserviceprogress);
        store = FirebaseFirestore.getInstance();
        serviceId = getIntent().getStringExtra("serviceId");


        populateSpinnerFromFirebase(location, "Dropdown", "LocationDropdown", "Location");
        populateSpinnerFromFirebase(center, "Dropdown", "CenterDropdown", "Center");
        populateSpinnerFromFirebase(category, "Dropdown", "CategoryDropdown", "Category");
        populateSpinnerFromFirebase(subCategory, "Dropdown", "SubCategoryDropdown", "SubCategory");
        populateSpinnerFromFirebase(severity, "Dropdown", "SeverityDropdown", "Severity");

        if (serviceId != null) {
            populateFieldsForEditing();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar3.setVisibility(View.VISIBLE);

                if (serviceId == null) {
                    addServiceRequest();
                } else {
                    updateServiceRequest();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }


    private void populateFieldsForEditing() {
        store.collection("ServiceRequest").document(serviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        initiatedBy.setText(documentSnapshot.getString("InitiatedBy"));
                        description.setText(documentSnapshot.getString("Description"));
                        setSpinnerSelection(location, documentSnapshot.getString("Location"));
                        setSpinnerSelection(center, documentSnapshot.getString("Center"));
                        setSpinnerSelection(category, documentSnapshot.getString("Category"));
                        setSpinnerSelection(subCategory, documentSnapshot.getString("SubCategory"));
                        setSpinnerSelection(severity, documentSnapshot.getString("Severity"));
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(AddServiceRequest.this, "Data not populated", Toast.LENGTH_SHORT).show();

                });
    }


    private void setSpinnerSelection(Spinner spinner, String selectedItem) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(selectedItem.trim());
        if (position != -1) {
            spinner.setSelection(position);
        }
    }

    private void addServiceRequest() {
        progressBar3.setVisibility(View.VISIBLE);
        DocumentReference sequenceRef = store.collection("Sequences").document("AddServiceSequence");
        sequenceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long nextId = documentSnapshot.getLong("NextId");
                if (nextId != null) {
                    Map<String, Object> serviceData = new HashMap<>();
                    serviceData.put("InitiatedBy", initiatedBy.getText().toString().trim());
                    serviceData.put("Description", description.getText().toString().trim());
                    serviceData.put("serviceId", nextId.toString());
                    serviceData.put("Location", location.getSelectedItem());
                    serviceData.put("Center", center.getSelectedItem());
                    serviceData.put("Category", category.getSelectedItem());
                    serviceData.put("SubCategory", subCategory.getSelectedItem());
                    serviceData.put("Severity", severity.getSelectedItem());
                    store.collection("ServiceRequest")
                            .document(String.valueOf(nextId))
                            .set(serviceData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    try {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + nextId);
                                        Toast.makeText(AddServiceRequest.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddServiceRequest.this, ServiceRequestPage.class);
                                        intent.putExtra("serviceId", String.valueOf(nextId));
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error while saving service request: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(AddServiceRequest.this, "An error occurred while saving data", Toast.LENGTH_SHORT).show();
                                    }


                                    sequenceRef.update("NextId", nextId + 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Next ID updated successfully");
                                            } else {
                                                Log.d(TAG, "Next ID update failed");
                                            }

                                            progressBar3.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    Toast.makeText(AddServiceRequest.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                                    progressBar3.setVisibility(View.GONE);
                                }
                            });
                } else {
                    Log.d(TAG, "Next ID is null or not found");
                    Toast.makeText(AddServiceRequest.this, "Next ID is null or not found", Toast.LENGTH_SHORT).show();
                    progressBar3.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "OnFailure: " + e.toString());
                Toast.makeText(AddServiceRequest.this, "Error fetching NextId", Toast.LENGTH_SHORT).show();
                progressBar3.setVisibility(View.GONE);
            }
        });


    }

    private void updateServiceRequest() {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("InitiatedBy", initiatedBy.getText().toString().trim());
        updatedData.put("Description", description.getText().toString().trim());
        updatedData.put("Location", location.getSelectedItem().toString());
        updatedData.put("Center", center.getSelectedItem().toString());
        updatedData.put("Category", category.getSelectedItem().toString());
        updatedData.put("SubCategory", subCategory.getSelectedItem().toString());
        updatedData.put("Severity", severity.getSelectedItem().toString());
        String serviceId = getIntent().getStringExtra("serviceId");

        store.collection("ServiceRequest").document(serviceId)
                .set(updatedData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    progressBar3.setVisibility(View.GONE);
                    Toast.makeText(AddServiceRequest.this, "Service request updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar3.setVisibility(View.GONE);
                    Toast.makeText(AddServiceRequest.this, "Failed to update service request", Toast.LENGTH_SHORT).show();
                });
    }


    private void populateSpinnerFromFirebase(Spinner spinner, String collectionPath, String documentId, String fieldName) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        store.collection(collectionPath).document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> options = (List<String>) documentSnapshot.get(fieldName);
                        if (options != null) {
                            adapter.addAll(options);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddServiceRequest.this, "No Data Found", Toast.LENGTH_SHORT).show();

                });


    }

}


