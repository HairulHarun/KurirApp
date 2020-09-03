package com.example.gorontalo.kurir_app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gorontalo.kurir_app.adapter.KoneksiAdapter;
import com.example.gorontalo.kurir_app.adapter.SessionAdapter;
import com.example.gorontalo.kurir_app.adapter.URLAdapter;
import com.example.gorontalo.kurir_app.adapter.VolleyAdapter;
import com.example.gorontalo.kurir_app.service.TrackingService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_ID = "id";
    public final static String TAG_NAME = "nama";
    public final static String TAG_KTP = "ktp";
    public final static String TAG_HP = "hp";
    public final static String TAG_SEX = "sex";
    public final static String TAG_EMAIL = "email";
    public final static String TAG_ALAMAT = "alamat";
    public final static String TAG_SALDO = "saldo";
    public final static String TAG_USERNAME = "username";
    public final static String TAG_PHOTO = "photo";
    public final static String TAG_STATUS_AKTF = "status_aktif";
    public final static String TAG_BIAYA = "biaya";
    public final static String TAG_BIAYA2 = "biaya2";

    String tag_json_obj = "json_obj_req";
    int success;

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private ProgressDialog pDialog;
    private Button btnLogin, btnRegister;
    private EditText txtUsername, txtPassword;

    private Boolean isInternetPresent = false;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        koneksiAdapter= new KoneksiAdapter(getApplicationContext());
        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionAdapter.checkLogin();

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        txtUsername = (EditText) findViewById(R.id.txt_username);
        txtPassword = (EditText) findViewById(R.id.txt_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(LoginActivity.this)
                        .withPermissions(
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {

                                    stopService(new Intent(LoginActivity.this, TrackingService.class));

                                    String username = txtUsername.getText().toString();
                                    String password = txtPassword.getText().toString();

                                    if (username.trim().length() > 0 && password.trim().length() > 0) {
                                        if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                            checkLogin(username, password, FirebaseInstanceId.getInstance().getToken());
                                        }else{
                                            SnackbarManager.show(
                                                    Snackbar.with(LoginActivity.this)
                                                            .text("No Connection !")
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                            .actionLabel("Refresh")
                                                            .actionListener(new ActionClickListener() {
                                                                @Override
                                                                public void onActionClicked(Snackbar snackbar) {
                                                                    refresh();
                                                                }
                                                            })
                                                    , LoginActivity.this);
                                        }
                                    } else {
                                        // Prompt user to enter credentials
                                        Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                                    }
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {
                                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void checkLogin(final String username, final String password, final String token) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().loginKurir(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String id = jObj.getString(TAG_ID);
                        String nama = jObj.getString(TAG_NAME);
                        String ktp = jObj.getString(TAG_KTP);
                        String hp = jObj.getString(TAG_HP);
                        String sex = jObj.getString(TAG_SEX);
                        String email = jObj.getString(TAG_EMAIL);
                        String alamat = jObj.getString(TAG_ALAMAT);
                        String saldo = jObj.getString(TAG_SALDO);
                        String username = jObj.getString(TAG_USERNAME);
                        String photo = jObj.getString(TAG_PHOTO);
                        String status_aktif = jObj.getString(TAG_STATUS_AKTF);
                        int biaya = jObj.getInt(TAG_BIAYA);
                        int biaya2 = jObj.getInt(TAG_BIAYA2);

                        Log.e("Successfully Login!", jObj.toString());

                        // menyimpan login ke session
                        sessionAdapter.createLoginSession(id, nama, ktp, hp, sex, email, alamat, saldo, username, photo, status_aktif, biaya, biaya2);
                        sessionAdapter.simpanToken(FirebaseInstanceId.getInstance().getToken());

                        // Memanggil main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("token", token);

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik lagi untuk keluar !", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
