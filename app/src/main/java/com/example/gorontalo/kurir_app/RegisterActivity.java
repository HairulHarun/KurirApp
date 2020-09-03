package com.example.gorontalo.kurir_app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.example.gorontalo.kurir_app.adapter.HttpsTrustManagerAdapter;
import com.example.gorontalo.kurir_app.adapter.KoneksiAdapter;
import com.example.gorontalo.kurir_app.adapter.SessionAdapter;
import com.example.gorontalo.kurir_app.adapter.URLAdapter;
import com.example.gorontalo.kurir_app.adapter.VolleyAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    int success;

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private ProgressDialog pDialog;
    private Button btnLogin, btnRegister;
    private EditText txtNama, txtKtp, txtHp, txtEmail, txtUsername, txtPassword, txtConfirmPassword;

    private Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        koneksiAdapter= new KoneksiAdapter(getApplicationContext());
        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionAdapter.checkLogin();

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        txtNama = (EditText) findViewById(R.id.txt_nama);
        txtKtp = (EditText) findViewById(R.id.txt_ktp);
        txtHp = (EditText) findViewById(R.id.txt_hp);
        txtEmail = (EditText) findViewById(R.id.txt_email);
        txtUsername = (EditText) findViewById(R.id.txt_username);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        txtConfirmPassword = (EditText) findViewById(R.id.txt_confirm_password);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(RegisterActivity.this).withPermissions(
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    String nama = txtNama.getText().toString();
                                    String ktp = txtKtp.getText().toString();
                                    String hp = txtHp.getText().toString();
                                    String email = txtEmail.getText().toString();
                                    String username = txtUsername.getText().toString();
                                    String password = txtPassword.getText().toString();
                                    String confirmpassword = txtConfirmPassword.getText().toString();
                                    String token = FirebaseInstanceId.getInstance().getToken();

                                    if (username.trim().length() > 0 && password.trim().length() > 0) {
                                        if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                            checkRegister(nama, ktp, hp, email, username, password, confirmpassword, token);
                                        }else{
                                            SnackbarManager.show(
                                                    Snackbar.with(RegisterActivity.this)
                                                            .text("No Connection !")
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                            .actionLabel("Refresh")
                                                            .actionListener(new ActionClickListener() {
                                                                @Override
                                                                public void onActionClicked(Snackbar snackbar) {
                                                                    refresh();
                                                                }
                                                            })
                                                    , RegisterActivity.this);
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void checkRegister(final String nama, final String ktp, final String hp, final String email, final String username, final String password, final String confirm_password, final String token) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().registerKurir(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Register!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txtNama.setText("");
                        txtKtp.setText("");
                        txtHp.setText("");
                        txtEmail.setText("");
                        txtUsername.setText("");
                        txtPassword.setText("");
                        txtConfirmPassword.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama", nama);
                params.put("ktp", ktp);
                params.put("hp", hp);
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                params.put("confirm_password", confirm_password);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
}
