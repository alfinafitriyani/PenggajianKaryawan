package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private LinearLayout buttonGajiKaryawan, buttonDataKaryawan, buttonJadwal, buttonPengumuman;
    private ImageButton buttonBackAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Menghubungkan LinearLayout dengan ID dari GridLayout
        buttonGajiKaryawan = findViewById(R.id.buttonGajiKaryawan);
        buttonDataKaryawan = findViewById(R.id.buttonDataKaryawan);
        buttonJadwal = findViewById(R.id.buttonJadwal);
        buttonPengumuman = findViewById(R.id.buttonPengumuman);

        // OnClickListener untuk buttonGajiKaryawan
        buttonGajiKaryawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke GajiActivity
                Intent intent = new Intent(AdminActivity.this, GajiActivity.class);
                startActivity(intent);
            }
        });

        // OnClickListener untuk buttonDataKaryawan
        buttonDataKaryawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, DataKaryawanActivity.class);
                startActivity(intent);
            }
        });

        // OnClickListener untuk buttonJadwal (menggantikan Laporan)
        buttonJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke JadwalActivity
                Intent intent = new Intent(AdminActivity.this, JadwalActivity.class);
                startActivity(intent);
            }
        });

        // OnClickListener untuk buttonPengumuman (menggantikan Pengaturan)
        buttonPengumuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke PengumumanActivity
                Intent intent = new Intent(AdminActivity.this, PengumumanActivity.class);
                startActivity(intent);
            }
        });

        buttonBackAdmin = findViewById(R.id.buttonBackAdmin);

        buttonBackAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Menutup halaman admin agar tidak bisa kembali dengan tombol back
            }
        });
    }



}
