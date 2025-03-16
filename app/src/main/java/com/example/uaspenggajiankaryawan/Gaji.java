package com.example.uaspenggajiankaryawan;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Gaji {
    private String username;
    private String bulan;
    private int gajiPokok;
    private int tunjangan;
    private int potongan;
    private int totalGaji;

    // **Constructor kosong (dibutuhkan oleh Firebase)**
    public Gaji() {
        // Diperlukan oleh Firebase
    }

    // Constructor dengan parameter
    public Gaji(String username, String bulan, int gajiPokok, int tunjangan, int potongan, int totalGaji) {
        this.username = username;
        this.bulan = bulan;
        this.gajiPokok = gajiPokok;
        this.tunjangan = tunjangan;
        this.potongan = potongan;
        this.totalGaji = totalGaji;
    }

    // Getter dan Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public int getGajiPokok() {
        return gajiPokok;
    }

    public void setGajiPokok(int gajiPokok) {
        this.gajiPokok = gajiPokok;
    }

    public int getTunjangan() {
        return tunjangan;
    }

    public void setTunjangan(int tunjangan) {
        this.tunjangan = tunjangan;
    }

    public int getPotongan() {
        return potongan;
    }

    public void setPotongan(int potongan) {
        this.potongan = potongan;
    }

    public int getTotalGaji() {
        return totalGaji;
    }

    public void setTotalGaji(int totalGaji) {
        this.totalGaji = totalGaji;
    }
}
