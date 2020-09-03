package com.example.gorontalo.kurir_app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gorontalo.kurir_app.adapter.HttpsTrustManagerAdapter;
import com.example.gorontalo.kurir_app.adapter.KoneksiAdapter;
import com.example.gorontalo.kurir_app.adapter.RVPekerjaanAdapter;
import com.example.gorontalo.kurir_app.adapter.RVPekerjaanBarangAdapter;
import com.example.gorontalo.kurir_app.adapter.SessionAdapter;
import com.example.gorontalo.kurir_app.adapter.URLAdapter;
import com.example.gorontalo.kurir_app.adapter.VolleyAdapter;
import com.example.gorontalo.kurir_app.model.PekerjaanBarangModel;
import com.example.gorontalo.kurir_app.model.PekerjaanModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailPekerjaanActivity extends AppCompatActivity {
    private static final String TAG = DetailPekerjaanActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_PEKERJAAN_BARANG = "pekerjaan_barang";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<PekerjaanBarangModel> pekerjaanBarangModelList;

    int success, jumlah;
    String nama_outlet;

    private Intent intent;
    private String id_pekerjaan, nama_pelanggan;
    private double jarak;
    private TextView txtNamaPelanggan, txtNamaOutlet, txtJumlah, txtJarak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pekerjaan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtNamaPelanggan = (TextView)findViewById(R.id.txtDetailNamaPelanggan);
        txtNamaOutlet = (TextView)findViewById(R.id.txtDetailNamaOutlet);
        txtJumlah = (TextView)findViewById(R.id.txtDetailJumlah);
        txtJarak = (TextView)findViewById(R.id.txtDetailJarak);

        intent = getIntent();
        id_pekerjaan = intent.getStringExtra("id_pekerjaan");
        nama_pelanggan = intent.getStringExtra("nama_pelanggan");
        jarak = intent.getDoubleExtra("jarak", 0);

        txtNamaPelanggan.setText(nama_pelanggan);
        txtJarak.setText(jarak+" Km");

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());
        mRecyclerView = (RecyclerView)findViewById(R.id.rvDetailPekerjaan);

        pekerjaanBarangModelList = new ArrayList<>();
        adapter = new RVPekerjaanBarangAdapter(getApplicationContext(), pekerjaanBarangModelList);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getData(id_pekerjaan);
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(DetailPekerjaanActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , DetailPekerjaanActivity.this);
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

    private void getData(final String id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().getPekerjaanKurirDetail(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        pekerjaanBarangModelList.clear();
                        jumlah = 0;

                        JSONArray pekerjaan = jObj.getJSONArray(TAG_PEKERJAAN_BARANG);

                        for (int i = 0; i < pekerjaan.length(); i++) {
                            try {
                                JSONObject jsonObject = pekerjaan.getJSONObject(i);

                                PekerjaanBarangModel pekerjaanBarangModel = new PekerjaanBarangModel();
                                pekerjaanBarangModel.setIdPekerjaanBarang(jsonObject.getString("id_pekerjaan_barang"));
                                pekerjaanBarangModel.setIdOutlet(jsonObject.getString("id_outlet"));
                                pekerjaanBarangModel.setNamaOutlet(jsonObject.getString("nama_outlet"));
                                pekerjaanBarangModel.setIdOutletBarang(jsonObject.getString("id_outlet_barang"));
                                pekerjaanBarangModel.setNamaOutletBarang(jsonObject.getString("nama_outlet_barang"));
                                pekerjaanBarangModel.setHarga(jsonObject.getString("harga"));
                                pekerjaanBarangModel.setQty(jsonObject.getString("qty"));
                                pekerjaanBarangModel.setJumlah(jsonObject.getString("jumlah"));
                                pekerjaanBarangModel.setPhoto(jsonObject.getString("photo"));

                                pekerjaanBarangModelList.add(pekerjaanBarangModel);

                                jumlah += Integer.parseInt(jsonObject.getString("jumlah"));
                                nama_outlet = jsonObject.getString("nama_outlet");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        txtNamaOutlet.setText(nama_outlet);
                        txtJumlah.setText(konversiRupiah(jumlah));

                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_PEKERJAAN_BARANG), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
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

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil.substring(2);
    }

}
