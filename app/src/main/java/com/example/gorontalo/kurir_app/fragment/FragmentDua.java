package com.example.gorontalo.kurir_app.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gorontalo.kurir_app.MainActivity;
import com.example.gorontalo.kurir_app.R;
import com.example.gorontalo.kurir_app.adapter.HttpsTrustManagerAdapter;
import com.example.gorontalo.kurir_app.adapter.KoneksiAdapter;
import com.example.gorontalo.kurir_app.adapter.RVPekerjaanAdapter;
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
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

/**
 * Created by Gorontalo on 11/26/2018.
 */

public class FragmentDua extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_PEKERJAAN = "pekerjaan";
    private static final String TAG_PEKERJAAN_BARANG = "pekerjaan_barang";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<PekerjaanModel> pekerjaanList;
    private TextView txtPendapatan, txtEmpty;

    int success;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_dua, container, false);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getActivity().getApplicationContext());
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvPekerjaan);

        txtPendapatan = (TextView)view.findViewById(R.id.txtPendapatan);
        txtEmpty = (TextView)view.findViewById(R.id.empty_view);

        pekerjaanList = new ArrayList<>();
        adapter = new RVPekerjaanAdapter(getActivity().getApplicationContext(), pekerjaanList);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getData(sessionAdapter.getID(), getCurrentDate());
                            }else{
                                SnackbarManager.show(
                                        Snackbar.with(getActivity())
                                                .text("No Connection !")
                                                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , getActivity());
                            }

                        }

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
                        Toast.makeText(getActivity().getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(date.getTime());

                getData(sessionAdapter.getID(), formattedDate);
            }
        });

        return view;
    }

    private void getData(final String id, final String tanggal) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().getPekerjaanKurir(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        pekerjaanList.clear();

                        int pendapatan = 0;

                        JSONArray pekerjaan = jObj.getJSONArray(TAG_PEKERJAAN);
                        for (int i = 0; i < pekerjaan.length(); i++) {
                            try {
                                JSONObject jsonObject = pekerjaan.getJSONObject(i);

                                PekerjaanModel pekerjaanModel = new PekerjaanModel();
                                pekerjaanModel.setId(jsonObject.getString("id"));
                                pekerjaanModel.setIdKurir(jsonObject.getString("id_kurir"));
                                pekerjaanModel.setNamaKurir(jsonObject.getString("nama_kurir"));
                                pekerjaanModel.setIdPelanggan(jsonObject.getString("id_pelanggan"));
                                pekerjaanModel.setNamaPelanggan(jsonObject.getString("nama_pelanggan"));
                                pekerjaanModel.setTanggal(jsonObject.getString("tanggal"));
                                pekerjaanModel.setWaktu(jsonObject.getString("waktu"));
                                pekerjaanModel.setJarak(Double.parseDouble(jsonObject.getString("jarak")));
                                pekerjaanModel.setBiaya(Integer.parseInt(jsonObject.getString("biaya")));
                                pekerjaanModel.setTotal(Integer.parseInt(jsonObject.getString("total")));
                                pekerjaanModel.setStatus(jsonObject.getString("status_pekerjaan"));

                                pekerjaanList.add(pekerjaanModel);

                                if (jsonObject.getString("status_pekerjaan").equals("3")){
                                    pendapatan = jsonObject.getInt("biaya");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        txtPendapatan.setText(konversiRupiah(pendapatan));
                        mRecyclerView.setVisibility(View.VISIBLE);
                        txtEmpty.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), jObj.getString(TAG_PEKERJAAN), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        mRecyclerView.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtPendapatan.setText("0.000");
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
                params.put("id_kurir", id);
                params.put("tanggal", tanggal);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(stringRequest, "json_pekerjaan");
    }

    private String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void refresh(){
        Intent intent = getActivity().getIntent();
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
