package com.example.gorontalo.kurir_app.adapter;

/**
 * Created by Gorontalo on 11/27/2018.
 */

public class URLAdapter {
//    private String URL = "http://192.168.43.163/kurir-app/webservices/";
    private String URL = "http://luwukbike.com/webservices/";

//    private String URL_PHOTO = "http://192.168.43.163/kurir-app/admin-control/assets/images/photo/";
    private String URL_PHOTO = "http://luwukbike.com/admin-control/assets/images/photo/";

    public String loginKurir(){
        return URL = URL+"ws-login-kurir.php";
    }

    public String registerKurir(){
        return URL = URL+"ws-register-kurir.php";
    }

    public String getPekerjaanKurir(){
        return URL = URL+"ws-pekerjaan-kurir.php";
    }

    public String updateStatusPekerjaanKurir(){
        return URL = URL+"ws-update-status-pekerjaan-kurir.php";
    }

    public String updateStatusAktifKurir(){
        return URL = URL+"ws-update-status-aktif-kurir.php";
    }

    public String updateLokasiKurir(){
        return URL = URL+"ws-update-lokasi-kurir.php";
    }

    public String cekStatusKurir(){
        return URL = URL+"ws-cek-status-kurir.php";
    }

    public String cekStatusPekerjaanKurir(){
        return URL = URL+"ws-cek-status-pekerjaan-kurir.php";
    }

    public String cekNotifKurir(){
        return URL = URL+"ws-notif-kurir.php";
    }

    public String getPekerjaanKurirDetail(){
        return URL = URL+"ws-pekerjaan-kurir-detail.php";
    }

    public String uploadPhotoProfile(){
        return URL = URL+"ws-upload-photo.php";
    }

    public String getPhotoBarang(){
        return URL = URL_PHOTO+"barang/";
    }

    public String getPhotoProfile(){
        return URL = URL_PHOTO+"profile-kurir/";
    }

    public String getPhotoProfilePelanggan(){
        return URL = URL_PHOTO+"profile-pelanggan/";
    }

    public String getBiayaKurir(){
        return URL = URL+"ws-get-biaya.php";
    }

    public String getSaldoKurir(){
        return URL = URL+"ws-get-saldo-kurir.php";
    }

    public String getStatusPekerjaan(){
        return URL = URL+"ws-get-status-pekerjaan.php";
    }
}
