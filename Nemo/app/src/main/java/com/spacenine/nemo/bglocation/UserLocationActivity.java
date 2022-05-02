package com.spacenine.nemo.bglocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.gson.Gson;
import com.spacenine.nemo.R;
import com.spacenine.nemo.util.StringUtilKt;

import java.util.Objects;

public class UserLocationActivity extends AppCompatActivity {

    public static final String contactNameKey = "contactNameKey";
    public static final String phoneKey = "phoneKey";

    private TextView contactLocationTextView;
    private WebView contactLocationWebView;

    private String contactName;
    private String phoneNumber;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        Objects.requireNonNull(getSupportActionBar()).hide();

        contactLocationTextView = findViewById(R.id.contactLocationTextView);

        contactLocationWebView = findViewById(R.id.contactLocationWebView);
        contactLocationWebView.getSettings().setJavaScriptEnabled(true);

        Bundle extras = getIntent().getExtras();
        contactName = extras.getString(contactNameKey);
        phoneNumber = extras.getString(phoneKey);

    }

    @Override
    protected void onResume() {
        super.onResume();

        contactLocationTextView.setText(contactName + "'s location");

        try {
            FirebaseDatabase.getInstance().getReference().child("nemo").child("phone-locations").child(phoneNumber).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Object latitude = null;
                    Object longitude = null;

                    for (DataSnapshot child: snapshot.getChildren())
                        if (Objects.equals(child.getKey(), "latitude"))
                            latitude = child.getValue();
                        else if (Objects.equals(child.getKey(), "longitude"))
                            longitude = child.getValue();

                    if (latitude != null && longitude != null)
                        contactLocationWebView.loadUrl("https://www.google.com/maps/place/" + latitude + "," + longitude);
                    else
                        Toast.makeText(UserLocationActivity.this, "location == null", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}