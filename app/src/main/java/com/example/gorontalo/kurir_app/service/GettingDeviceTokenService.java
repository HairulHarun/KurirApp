package com.example.gorontalo.kurir_app.service;

import android.util.Log;

import com.example.gorontalo.kurir_app.adapter.SessionAdapter;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GettingDeviceTokenService extends FirebaseInstanceIdService {
    private SessionAdapter sessionAdapter;

    @Override
    public void onTokenRefresh() {
        String DeviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("DeviceToken ==> ",  DeviceToken);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionAdapter.simpanToken(DeviceToken);
    }
}
