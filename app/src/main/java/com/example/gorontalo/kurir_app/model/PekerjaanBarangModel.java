package com.example.gorontalo.kurir_app.model;

public class PekerjaanBarangModel {
    private String idPekerjaanBarang, idOutlet, namaOutlet, idOutletBarang, namaOutletBarang, harga, qty, jumlah, status, photo;

    public PekerjaanBarangModel() {
    }

    public PekerjaanBarangModel(String idPekerjaanBarang, String idOutlet, String namaOutlet, String idOutletBarang, String namaOutletBarang, String harga, String qty, String jumlah, String status, String photo) {
        this.idPekerjaanBarang = idPekerjaanBarang;
        this.idOutlet = idOutlet;
        this.namaOutlet = namaOutlet;
        this.idOutletBarang = idOutletBarang;
        this.namaOutletBarang = namaOutletBarang;
        this.harga = harga;
        this.qty = qty;
        this.jumlah = jumlah;
        this.status = status;
        this.photo = photo;
    }

    public String getIdPekerjaanBarang() {
        return idPekerjaanBarang;
    }

    public void setIdPekerjaanBarang(String idPekerjaanBarang) {
        this.idPekerjaanBarang = idPekerjaanBarang;
    }

    public String getIdOutlet() {
        return idOutlet;
    }

    public void setIdOutlet(String idOutlet) {
        this.idOutlet = idOutlet;
    }

    public String getNamaOutlet() {
        return namaOutlet;
    }

    public void setNamaOutlet(String namaOutlet) {
        this.namaOutlet = namaOutlet;
    }

    public String getIdOutletBarang() {
        return idOutletBarang;
    }

    public void setIdOutletBarang(String idOutletBarang) {
        this.idOutletBarang = idOutletBarang;
    }

    public String getNamaOutletBarang() {
        return namaOutletBarang;
    }

    public void setNamaOutletBarang(String namaOutletBarang) {
        this.namaOutletBarang = namaOutletBarang;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getStatusPekerjaan() {
        return status;
    }

    public void setStatusPekerjaan(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
