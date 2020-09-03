package com.example.gorontalo.kurir_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.gorontalo.kurir_app.MainActivity;
import com.example.gorontalo.kurir_app.PekerjaanActivity;

public class SessionPekerjaanAdapter {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "pekerjaan_session";
    private static final String IS_READY = "IsPekerjaan";
    public static final String KEY_ID = "id";
    public static final String KEY_DIALOG = "dialog";
    public static final String KEY_NAMA_PELANGGAN= "nama_pelanggan";
    public static final String KEY_HP_PELANGGAN= "hp_pelanggan";
    public static final String KEY_ALAMAT_PELANGGAN= "alamat_pelanggan";
    public static final String KEY_PHOTO_PELANGGAN= "photo_pelanggan";
    public static final String KEY_NAMA_OUTLET= "nama_outlet";
    public static final String KEY_JARAK= "jarak";
    public static final String KEY_BIAYA= "biaya";
    public static final String KEY_TOTAL = "total";
    public static final String KEY_LAT= "lat";
    public static final String KEY_LONG= "long";
    public static final String KEY_LAT_OUTLET= "lat_outlet";
    public static final String KEY_LONG_OUTLET= "long_outlet";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CATATAN= "catatan";

    public SessionPekerjaanAdapter(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createPekerjaanSession(String dialog, String id, String nama_pelanggan, String hp_pelanggan, String alamat_pelanggan, String photo_pelanggan, String nama_outlet, String jarak, String biaya, String total, String latitude, String longitude, String lat_outlet, String long_outlet, String status, String catatan){
        editor.putBoolean(IS_READY, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_DIALOG, dialog);
        editor.putString(KEY_NAMA_PELANGGAN, nama_pelanggan);
        editor.putString(KEY_HP_PELANGGAN, hp_pelanggan);
        editor.putString(KEY_ALAMAT_PELANGGAN, alamat_pelanggan);
        editor.putString(KEY_PHOTO_PELANGGAN, photo_pelanggan);
        editor.putString(KEY_NAMA_OUTLET, nama_outlet);
        editor.putString(KEY_JARAK, jarak);
        editor.putString(KEY_BIAYA, biaya);
        editor.putString(KEY_TOTAL, total);
        editor.putString(KEY_LAT, latitude);
        editor.putString(KEY_LONG, longitude);
        editor.putString(KEY_LAT_OUTLET, lat_outlet);
        editor.putString(KEY_LONG_OUTLET, long_outlet);
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_CATATAN, catatan);
        editor.commit();
    }

    public void deleteSession(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public void checkPekerjaan(){
        if(this.isPekerjaan()){
            Intent i = new Intent(_context, PekerjaanActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public boolean isPekerjaan(){
        return pref.getBoolean(IS_READY, false);
    }

    public String getID(){
        String user = pref.getString(KEY_ID, null);
        return user;
    }

    public String getDialog(){
        String user = pref.getString(KEY_DIALOG, null);
        return user;
    }

    public String getPelanggan(){
        String user = pref.getString(KEY_NAMA_PELANGGAN, null);
        return user;
    }

    public String getHpPelanggan(){
        String user = pref.getString(KEY_HP_PELANGGAN, null);
        return user;
    }

    public String getAlamatPelanggan(){
        String user = pref.getString(KEY_ALAMAT_PELANGGAN, null);
        return user;
    }

    public String getPhotoPelanggan(){
        String user = pref.getString(KEY_PHOTO_PELANGGAN, null);
        return user;
    }

    public String getOutlet(){
        String user = pref.getString(KEY_NAMA_OUTLET, null);
        return user;
    }

    public String getJarak(){
        String user = pref.getString(KEY_JARAK, null);
        return user;
    }

    public String getBiaya(){
        String user = pref.getString(KEY_BIAYA, null);
        return user;
    }

    public String getTotal(){
        String user = pref.getString(KEY_TOTAL, null);
        return user;
    }

    public String getLat(){
        String user = pref.getString(KEY_LAT, null);
        return user;
    }

    public String getLong(){
        String user = pref.getString(KEY_LONG, null);
        return user;
    }

    public String getLatOutlet(){
        String user = pref.getString(KEY_LAT_OUTLET, null);
        return user;
    }

    public String getLongOutlet(){
        String user = pref.getString(KEY_LONG_OUTLET, null);
        return user;
    }

    public String getStatus(){
        String user = pref.getString(KEY_STATUS, null);
        return user;
    }

    public void setStatus(String status){
        editor.putString(KEY_STATUS, status);
        editor.commit();
    }

    public String getCatatan(){
        String user = pref.getString(KEY_CATATAN, null);
        return user;
    }

    public void setCatatan(String catatan){
        editor.putString(KEY_CATATAN, catatan);
        editor.commit();
    }


}
