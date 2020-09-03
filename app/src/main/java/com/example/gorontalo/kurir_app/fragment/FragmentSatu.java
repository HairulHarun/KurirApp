package com.example.gorontalo.kurir_app.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.gorontalo.kurir_app.MainActivity;
import com.example.gorontalo.kurir_app.R;
import com.example.gorontalo.kurir_app.adapter.SessionAdapter;
import com.example.gorontalo.kurir_app.model.LocationsModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Gorontalo on 11/26/2018.
 */

public class FragmentSatu extends Fragment implements OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;
    private SessionAdapter sessionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_satu, container, false);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (!sessionAdapter.getID().equals("")){
                gMap = googleMap;
                gMap.setMaxZoomPreference(16);
                loginToFirebase();
            }
        }catch (NullPointerException e){

        }
    }

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);

        // Authenticate with Firebase and subscribe to updates
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    subscribeToUpdates();
                    try {
                        subscribeToUpdates2();
                    }catch (IllegalStateException | NullPointerException e){
                        Log.d("Main Activity", "Error Fragment");
                    }
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path)+"/"+sessionAdapter.getID());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void subscribeToUpdates2() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.child(sessionAdapter.getID().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    LocationsModel locations = dataSnapshot.getValue(LocationsModel.class);

                    String key = dataSnapshot.getKey();
                    double lat = locations.getLatitude();
                    double lng = locations.getLongitude();

                    LatLng location = new LatLng(lat, lng);
                    if (!mMarkers.containsKey(key)) {
                        mMarkers.put(key, gMap.addMarker(new MarkerOptions().title(key).position(location)));
                    } else {
                        mMarkers.get(key).setPosition(location);
                    }
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : mMarkers.values()) {
                        builder.include(marker.getPosition());
                    }
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));

                }catch (NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tmz", "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, gMap.addMarker(new MarkerOptions().title(key).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }
}
