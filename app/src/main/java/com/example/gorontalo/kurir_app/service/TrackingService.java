package com.example.gorontalo.kurir_app.service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gorontalo.kurir_app.AboutActivity;
import com.example.gorontalo.kurir_app.MainActivity;
import com.example.gorontalo.kurir_app.R;
import com.example.gorontalo.kurir_app.adapter.GPSTrackingAdapter;
import com.example.gorontalo.kurir_app.adapter.SessionAdapter;
import com.example.gorontalo.kurir_app.adapter.URLAdapter;
import com.example.gorontalo.kurir_app.adapter.VolleyAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gorontalo on 11/28/2018.
 */

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();
    private SessionAdapter sessionAdapter;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    int success;

    private Timer timer = new Timer();
    private GPSTrackingAdapter gpsTrackingAdapter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sessionAdapter = new SessionAdapter(getApplicationContext());
        buildNotification();
        loginToFirebase();

        gpsTrackingAdapter = new GPSTrackingAdapter(getApplicationContext());

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (gpsTrackingAdapter.canGetLocation()){
                    updateLokasi(sessionAdapter.getID(), String.valueOf(gpsTrackingAdapter.getLatitude()), String.valueOf(gpsTrackingAdapter.getLongitude()));
                }else{
                    Log.d("Lokasi", "Lokasi error !");
                }
            }
        }, 0, 20000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(1, builder.build());
        }
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (!sessionAdapter.getID().equals("")){
                        requestLocationUpdates();
                    }
                } else {
                    Log.d(TAG, "Firebase authentication failed");
                }
            }
        });
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path)+"/"+sessionAdapter.getID().toString();
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        ref.setValue(location);
                    }
                }
            }, null);
        }
    }

    private void updateLokasi(final String id, final String latitude, final String longitude) {
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().updateLokasiKurir(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        Log.e(TAG, jObj.getString(TAG_MESSAGE));
                    } else {
                        Log.e(TAG, jObj.    getString(TAG_MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Data Errorrrr: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("latitude", latitude);
                params.put("longitude", longitude);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}
