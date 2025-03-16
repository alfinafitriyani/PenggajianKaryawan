package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KaryawanActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private ImageButton buttonBackAdmin;
    private GridLayout gridMenu;
    private LinearLayout menuGaji, menuProfil, menuJadwal, menuPengumuman;
    private DatabaseReference database; // Referensi ke Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        gridMenu = findViewById(R.id.gridMenu);
        menuGaji = findViewById(R.id.menuGaji);
        menuProfil = findViewById(R.id.menuProfil);
        menuJadwal = findViewById(R.id.menuJadwal);
        menuPengumuman = findViewById(R.id.menuPengumuman);

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (!username.isEmpty()) {
            // Ambil data user dari Firebase berdasarkan username
            database = FirebaseDatabase.getInstance().getReference("users").child(username);
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nama = snapshot.child("name").getValue(String.class);
                        textViewWelcome.setText("Hi, " + nama); // Tampilkan nama user
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(KaryawanActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Event klik menu
        menuGaji.setOnClickListener(v -> startActivity(new Intent(KaryawanActivity.this, GajiKaryawanActivity.class)));
        menuProfil.setOnClickListener(v -> startActivity(new Intent(KaryawanActivity.this, ProfileKaryawanActivity.class)));
        menuJadwal.setOnClickListener(v -> startActivity(new Intent(KaryawanActivity.this, JadwalKaryawanActivity.class)));
        menuPengumuman.setOnClickListener(v -> startActivity(new Intent(KaryawanActivity.this, PengumumanKaryawanActivity.class)));

        buttonBackAdmin = findViewById(R.id.buttonBackAdmin);

        buttonBackAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KaryawanActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Menutup halaman admin agar tidak bisa kembali dengan tombol back
            }
        });
    }
}
