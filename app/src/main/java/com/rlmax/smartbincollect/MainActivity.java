package com.rlmax.smartbincollect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleMap mMap;
    private String binName;
    private double lat;
    private double log;
    private int percentage;
    private boolean status;
    Marker currentMarker = null;
    SupportMapFragment mapFragment;
    private TextView tvNoBin;
    private String driverName;
    private String driverId;
    private RelativeLayout rlRequestView;
    private DatabaseReference rootRef;
    SharedPreferences sharedPreferences;
    private Long timeStamp;
    private FrameLayout flPickupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNoBin = findViewById(R.id.tvNoBin);
        rlRequestView = findViewById(R.id.rlRequestView);
        flPickupBtn = findViewById(R.id.flPickupBtn);
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        mapFragment  = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        rlRequestView.setVisibility(View.GONE);
        flPickupBtn.setVisibility(View.GONE);
        mapFragment.getView().setVisibility(View.INVISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                binName =  dataSnapshot.child("name").getValue().toString();
                lat =(Double) dataSnapshot.child("latitude").getValue();
                log =(Double) dataSnapshot.child("longitude").getValue();
                percentage = ((Long) dataSnapshot.child("precent").getValue()).intValue();
                status = (boolean) dataSnapshot.child("status").getValue();
                driverName = dataSnapshot.child("d_name").getValue().toString();
                driverId = dataSnapshot.child("d_uid").getValue().toString();
                timeStamp = (Long) dataSnapshot.child("Time").getValue();
                if(percentage >= 80 && !status) {
                    if(sharedPreferences.getLong("timestamp",0) != timeStamp) {
                        tvNoBin.setVisibility(View.GONE);
                        flPickupBtn.setVisibility(View.GONE);
                        mapFragment.getView().setVisibility(View.INVISIBLE);
                        rlRequestView.setVisibility(View.VISIBLE);
                    }
                }else if(percentage >= 80 && status && user.getUid().equals(driverId)){
                    if(sharedPreferences.getLong("timestamp",0) != timeStamp) {
                        tvNoBin.setVisibility(View.GONE);
                        rlRequestView.setVisibility(View.GONE);
                        flPickupBtn.setVisibility(View.VISIBLE);
                        mapFragment.getView().setVisibility(View.VISIBLE);
                        if (currentMarker != null) {
                            currentMarker.remove();
                            currentMarker = null;
                        }
                        LatLng latLng = new LatLng(lat, log);
                        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(binName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    }
                }else{
                    tvNoBin.setVisibility(View.VISIBLE);
                    rlRequestView.setVisibility(View.GONE);
                    flPickupBtn.setVisibility(View.GONE);
                    mapFragment.getView().setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvNoBin.setVisibility(View.VISIBLE);
                rlRequestView.setVisibility(View.GONE);
                flPickupBtn.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.INVISIBLE);
            }

        };
        rootRef.addValueEventListener(valueEventListener);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_two);
        View view = MenuItemCompat.getActionView(menuItem);

        CircleImageView profileImage = view.findViewById(R.id.toolbar_profile_image);
        Glide
                .with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.logo)
                .into(profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
               // Toast.makeText(MainActivity.this, "Profile Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miLogout:
                final FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

            //When we use layout for menu then this case will not work
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void onClick(View view) {
        if(view.getId() == R.id.btnAccept){
            HashMap<String, Object> newData = new HashMap<>();
            newData.put("status", true);
            newData.put("d_uid",user.getUid());
            newData.put("d_name",user.getDisplayName());
            rootRef.updateChildren(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    tvNoBin.setVisibility(View.GONE);
                    rlRequestView.setVisibility(View.GONE);
                    flPickupBtn.setVisibility(View.VISIBLE);
                    mapFragment.getView().setVisibility(View.VISIBLE);
                }
            });
        }
        if(view.getId() == R.id.btnReject){
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putLong("timestamp", timeStamp);
            myEdit.apply();
            tvNoBin.setVisibility(View.VISIBLE);
            rlRequestView.setVisibility(View.GONE);
            flPickupBtn.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }
        if(view.getId() == R.id.fbBtn){
            Long tsLong = System.currentTimeMillis()/1000;
            HashMap<String, Object> newData = new HashMap<>();
            newData.put("status", false);
            newData.put("d_uid",user.getUid());
            newData.put("precent",0);
            newData.put("Time",tsLong);
            rootRef.updateChildren(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putLong("timestamp", timeStamp);
                    myEdit.apply();
                    tvNoBin.setVisibility(View.VISIBLE);
                    rlRequestView.setVisibility(View.GONE);
                    flPickupBtn.setVisibility(View.GONE);
                    mapFragment.getView().setVisibility(View.INVISIBLE);
                }
            });

        }
    }
}