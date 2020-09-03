package com.example.gorontalo.kurir_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.gorontalo.kurir_app.adapter.GPSTrackingAdapter;
import com.example.gorontalo.kurir_app.adapter.HttpsTrustManagerAdapter;
import com.example.gorontalo.kurir_app.adapter.SessionAdapter;
import com.example.gorontalo.kurir_app.adapter.SessionPekerjaanAdapter;
import com.example.gorontalo.kurir_app.adapter.URLAdapter;
import com.example.gorontalo.kurir_app.adapter.VolleyAdapter;
import com.example.gorontalo.kurir_app.fragment.FragmentDetailPekerjaan;
import com.example.gorontalo.kurir_app.fragment.FragmentProfilePelanggan;
import com.example.gorontalo.kurir_app.model.LocationsModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PekerjaanActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = PekerjaanActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_STATUS= "status";

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;
    private SessionAdapter sessionAdapter;
    private SessionPekerjaanAdapter sessionPekerjaanAdapter;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private static LatLng PELANGGAN = null;
    private static LatLng OUTLET = null;

    private Marker mPelanggan;
    private Marker mOutlet;

    private Button btnPekerjaan;
    private TextView txtOutlet, txtPelanggan, txtJumlah, txtJarak, txtCatatan;
    private ImageView btnOutlet, btnPelanggan;

    private ProgressDialog pDialog;

    String tag_json_obj = "json_obj_req";
    int success;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pekerjaan);

        btnPekerjaan = findViewById(R.id.btnPekerjaan);
        txtOutlet = findViewById(R.id.txtPekerjaanOutlet);
        txtPelanggan = findViewById(R.id.txtPekerjaanPelanggan);
        txtJumlah = findViewById(R.id.txtPekerjaanJumlah);
        txtJarak = findViewById(R.id.txtPekerjaanJarak);
        txtCatatan= findViewById(R.id.txtPekerjaanCatatan);
        btnOutlet = findViewById(R.id.imgOutletMaps);
        btnPelanggan= findViewById(R.id.imgPelangganMaps);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionPekerjaanAdapter = new SessionPekerjaanAdapter(getApplicationContext());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPekerjaan);
        mapFragment.getMapAsync(this);

        PELANGGAN = new LatLng(Double.parseDouble(sessionPekerjaanAdapter.getLat()),
                Double.parseDouble(sessionPekerjaanAdapter.getLong()));
        OUTLET = new LatLng(Double.parseDouble(sessionPekerjaanAdapter.getLatOutlet()),
                Double.parseDouble(sessionPekerjaanAdapter.getLongOutlet()));

        txtPelanggan.setText(sessionPekerjaanAdapter.getPelanggan());
        txtOutlet.setText(sessionPekerjaanAdapter.getOutlet());
        txtJumlah.setText(sessionPekerjaanAdapter.getTotal());
        txtJarak.setText(sessionPekerjaanAdapter.getJarak() +" Km");
        txtCatatan.setText(sessionPekerjaanAdapter.getCatatan());

        showDialogPekerjaan(sessionPekerjaanAdapter.getID(), sessionPekerjaanAdapter.getPelanggan(), sessionPekerjaanAdapter.getOutlet(), sessionPekerjaanAdapter.getJarak());

        if (sessionPekerjaanAdapter.getStatus().equals("0")){
            btnPekerjaan.setText("Menuju Outlet");
        }else if (sessionPekerjaanAdapter.getStatus().equals("1")){
            btnPekerjaan.setText("Mulai Belanja");
        }else if (sessionPekerjaanAdapter.getStatus().equals("2")){
            btnPekerjaan.setText("Menuju Pelanggan");
        }else{
            btnPekerjaan.setText("Selesai");
        }

        btnPekerjaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionPekerjaanAdapter.getStatus().equals("0")){
                    btnPekerjaan.setBackgroundResource(R.color.colorAccent);
                    updateStatus(sessionPekerjaanAdapter.getID(), "1", "Mulai Belanja");
                }else if (sessionPekerjaanAdapter.getStatus().equals("1")){
                    btnPekerjaan.setBackgroundResource(R.color.colorYellow);
                    updateStatus(sessionPekerjaanAdapter.getID(), "2", "Menuju Pelanggan");
                }else if (sessionPekerjaanAdapter.getStatus().equals("2")){
                    btnPekerjaan.setBackgroundResource(R.color.colorPrimaryDark);
                    updateStatus(sessionPekerjaanAdapter.getID(), "3", "Selesai");
                }else{
                    sessionPekerjaanAdapter.deleteSession();
                }

            }
        });

        txtOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDetailPekerjaan bottomSheetFragment = new FragmentDetailPekerjaan();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        txtPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentProfilePelanggan bottomSheetFragment = new FragmentProfilePelanggan();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        btnOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", Double.parseDouble(sessionPekerjaanAdapter.getLatOutlet()), Double.parseDouble(sessionPekerjaanAdapter.getLongOutlet()), "Lokasi Outlet");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try{
                    startActivity(intent);
                }
                catch(ActivityNotFoundException ex){
                    try{
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx){
                        Toast.makeText(PekerjaanActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", Double.parseDouble(sessionPekerjaanAdapter.getLat()), Double.parseDouble(sessionPekerjaanAdapter.getLong()), "Lokasi Pelanggan");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getStatus(sessionPekerjaanAdapter.getID());
            }
        }, 0, 10000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (!sessionAdapter.getID().equals("")){
                gMap = googleMap;
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(OUTLET, 13));

                loginToFirebase();
                // Add some markers to the map, and add a data object to each marker.
                mPelanggan= gMap.addMarker(new MarkerOptions().position(PELANGGAN).title(sessionPekerjaanAdapter.getPelanggan()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_pelanggan)));
                mPelanggan.setTag(0);
                mOutlet= gMap.addMarker(new MarkerOptions().position(OUTLET).title(sessionPekerjaanAdapter.getOutlet()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_outlet)));
                mOutlet.setTag(0);
            }
        }catch (NullPointerException e){

        }
    }

    public void onBackPressed(){
        moveTaskToBack(true);
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
                        mMarkers.put(key, gMap.addMarker(new MarkerOptions().title(key).position(location).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_kurir))));
                    } else {
                        mMarkers.get(key).setPosition(location);
                    }
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : mMarkers.values()) {
                        builder.include(marker.getPosition());
                    }
//                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
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

    private void showDialogPekerjaan(String id, String pelanggan, String outlet, String jarak){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pekerjaan, viewGroup, false);

        Button dialogButton = (Button) dialogView.findViewById(R.id.btnDialogOk);
        TextView txtPelanggan = (TextView) dialogView.findViewById(R.id.txtDialogPelanggan);
        TextView txtOutlet = (TextView) dialogView.findViewById(R.id.txtDialogOutlet);
        txtPelanggan.setText(pelanggan);
        txtOutlet.setText(outlet);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        playAudio();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                stopAudio();
            }
        });
    }

    private void playAudio(){
        try {
            mediaPlayer = MediaPlayer.create(PekerjaanActivity.this, R.raw.dering);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    long pattern[] = { 0, 100, 200, 300, 400 };
                    vibrator.vibrate(pattern, 0);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(getApplicationContext(), "Audio is Playing !" , Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "a"+e.getMessage() , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "b"+e.getMessage() , Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "c"+e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        vibrator.cancel();
    }

    private void updateStatus(final String id, final String status, final String teks) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading ...");
        showDialog();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().updateStatusPekerjaanKurir(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        btnPekerjaan.setText(teks);
                        sessionPekerjaanAdapter.setStatus(status);

                        if (status == "3"){
                            String saldo = jObj.getString("saldo_kurir");
                            sessionAdapter.updateSaldo(saldo);
                        }

                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                        sessionPekerjaanAdapter.deleteSession();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Data Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("status", status);
                params.put("id_kurir", sessionAdapter.getID());

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void getStatus(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().getStatusPekerjaan(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt("sukses");
                    if (success == 1) {
                        String status = jObj.getString(TAG_STATUS);
                        if (status.equals("4")){
                            Toast.makeText(getApplicationContext(), "Pesanan Telah Di Batalkan !", Toast.LENGTH_LONG).show();
                            timer.cancel();
                            sessionPekerjaanAdapter.deleteSession();
                            stopAudio();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_STATUS), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_pekerjaan", id);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(stringRequest, "json_pekerjaan");
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
