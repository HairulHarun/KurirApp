package com.example.gorontalo.kurir_app.model;

public class PekerjaanModel {
    private String id, idKurir, namaKurir, idPelanggan, namaPelanggan, tanggal, waktu, status;
    private double jarak;
    private int biaya, total;

    public PekerjaanModel() {
    }

    public PekerjaanModel(String id, String idKurir, String namaKurir, String idPelanggan, String namaPelanggan, String tanggal, String waktu, double jarak, int biaya, int total,  String status) {
        this.id = id;
        this.idKurir = idKurir;
        this.namaKurir = namaKurir;
        this.idPelanggan = idPelanggan;
        this.namaPelanggan = namaPelanggan;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.jarak = jarak;
        this.biaya = biaya;
        this.total = total;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdKurir() {
        return idKurir;
    }

    public void setIdKurir(String idKurir) {
        this.idKurir = idKurir;
    }

    public String getNamaKurir() {
        return namaKurir;
    }

    public void setNamaKurir(String namaKurir) {
        this.namaKurir = namaKurir;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public double getJarak() {
        return jarak;
    }

    public void setJarak(double jarak) {
        this.jarak = jarak;
    }

    public int getBiaya() {
        return biaya;
    }

    public void setBiaya(int biaya) {
        this.biaya = biaya;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
