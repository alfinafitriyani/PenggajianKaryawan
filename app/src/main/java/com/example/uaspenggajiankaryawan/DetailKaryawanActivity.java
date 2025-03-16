package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailKaryawanActivity extends AppCompatActivity {
    private TextView textViewId, textViewUsername, textViewNama, textViewTelepon, textViewAlamat, textViewDepartemen;
    private ImageView imageProfile;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_karyawan);

        // Inisialisasi View
        textViewId = findViewById(R.id.textViewEmployeID);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewNama = findViewById(R.id.textViewName);
        textViewTelepon = findViewById(R.id.textViewPhone);
        textViewAlamat = findViewById(R.id.textViewAddress);
        textViewDepartemen = findViewById(R.id.textViewDepartment);
        imageProfile = findViewById(R.id.imageProfile);
        buttonBack = findViewById(R.id.buttonBackDetailKaryawan);

        // Ambil data dari Intent
        Intent intent = getIntent();
        String idKaryawan = intent.getStringExtra("idKaryawan");
        String username = intent.getStringExtra("username");
        String nama = intent.getStringExtra("nama");
        String telepon = intent.getStringExtra("telepon");
        String alamat = intent.getStringExtra("alamat");
        String departemen = intent.getStringExtra("departemen");

        // Set data ke TextView
        textViewId.setText("ID: " + idKaryawan);
        textViewUsername.setText("Username: " + username);
        textViewNama.setText("Nama: " + nama);
        textViewTelepon.setText("Telepon: " + telepon);
        textViewAlamat.setText("Alamat: " + alamat);
        textViewDepartemen.setText("Departemen: " + departemen);

        // Set Gambar Profil Default
        imageProfile.setImageResource(R.drawable.ic_profil);

        // Tombol Kembali
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Menutup aktivitas dan kembali ke halaman sebelumnya
            }
        });
    }
}
