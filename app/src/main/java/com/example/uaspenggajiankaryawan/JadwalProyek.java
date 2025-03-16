package com.example.uaspenggajiankaryawan;

public class JadwalProyek {
    private String id;
    private String tanggal;
    private String namaProyek;
    private String deadline;

    public JadwalProyek() { }

    public JadwalProyek(String id, String tanggal, String namaProyek, String deadline) {
        this.id = id;
        this.tanggal = tanggal;
        this.namaProyek = namaProyek;
        this.deadline = deadline;
    }

    public String getId() { return id; }
    public String getTanggal() { return tanggal; }
    public String getNamaProyek() { return namaProyek; }
    public String getDeadline() { return deadline; }
}

