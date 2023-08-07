package com.example.myapplication;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addservice_request);
        title = findViewById(R.id.Title);
        initiatedBy = findViewById(R.id.InitiatedBy);
        description = findViewById(R.id.Description);
        hintLocation = findViewById(R.id.hintLocation);
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


        populateSpinnerFromFirestore(location, "Dropdown", "LocationDropdown", "Location");
        populateSpinnerFromFirestore(center, "Dropdown", "CenterDropdown", "Center");
        populateSpinnerFromFirestore(category, "Dropdown", "CategoryDropdown", "Category");
        populateSpinnerFromFirestore(subCategory, "Dropdown", "SubCategoryDropdown", "SubCategory");
        populateSpinnerFromFirestore(severity, "Dropdown", "SeverityDropdown", "Severity");

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
            public void onClick(View view1) {
                clearSpinnerSelection(location);
                clearSpinnerSelection(center);
                clearSpinnerSelection(category);
                clearSpinnerSelection(subCategory);
                clearSpinnerSelection(severity);
            }
        });

    }




    private void clearSpinnerSelection(Spinner spinner) {
        spinner.setSelection(0); // Set the selection to the first item
    }




    private void populateFieldsForEditing() {
        String serviceId = getIntent().getStringExtra("serviceId");
        store.collection("ServiceRequest").document(serviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        initiatedBy.setText(documentSnapshot.getString("InitiatedBy"));
                        description.setText(documentSnapshot.getString("Description"));
                        setSpinnerSelection(location, documentSnapshot.getString("Location"));
                        setSpinnerSelection(center, documentSnapshot.getString("center"));
                        setSpinnerSelection(category, documentSnapshot.getString("Category"));
                        setSpinnerSelection(subCategory, documentSnapshot.getString("SubCategory"));
                        setSpinnerSelection(severity, documentSnapshot.getString("Severity"));
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }


    private void setSpinnerSelection(Spinner spinner, String selectedItem) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(selectedItem);
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
                    serviceData.put("serviceId",  nextId.toString());
                    serviceData.put("Location", location.getSelectedItem());
                    serviceData.put("center", center.getSelectedItem());
                    serviceData.put("Category", category.getSelectedItem());
                    serviceData.put("SubCategory", subCategory.getSelectedItem());
                    serviceData.put("Severity", severity.getSelectedItem());
                        store.collection("ServiceRequest")
                                .document(String.valueOf(nextId))
                                .set(serviceData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + nextId);
                                        Toast.makeText(AddServiceRequest.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));


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


    private void populateSpinnerFromFirestore(Spinner spinner, String collectionName, String fieldName, String defaultItem) {
        store.collection(collectionName).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> items = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot) {
                        String item = document.getString(fieldName);
                        items.add(item);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    int position = adapter.getPosition(defaultItem);

                    if (position != -1) {
                        spinner.setSelection(position);
                    } else {
                        Log.d(TAG, "Default Item for " + spinner.getTag() + " not found in adapter.");
                        // If the default item is not found in the adapter, you can handle it as needed.
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    }




