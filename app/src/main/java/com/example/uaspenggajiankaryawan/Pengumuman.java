package com.example.uaspenggajiankaryawan;

public class Pengumuman {
    private String id;
    private String judul;
    private String deskripsi;
    private String tanggal;
    private String gambar;

    public Pengumuman() {
    }

    public Pengumuman(String id, String judul, String deskripsi, String tanggal, String gambar) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.gambar = gambar;
    }

    public String getId() { return id; }
    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
    public String getTanggal() { return tanggal; }
    public String getGambar() { return gambar; }
}

