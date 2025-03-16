package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GajiActivity extends AppCompatActivity {

    private Spinner spinnerBulan;
    private RecyclerView recyclerViewGaji;
    private GajiAdapter gajiAdapter;
    private List<Karyawan> karyawanList;
    private DatabaseReference databaseReferenceUsers;
    public static String selectedBulan = "Januari";
    private ImageButton buttonBackAdmin;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaji);

        // Inisialisasi UI
        spinnerBulan = findViewById(R.id.spinnerBulan);
        recyclerViewGaji = findViewById(R.id.recyclerViewGaji);
        recyclerViewGaji.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi Firebase
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users");
        karyawanList = new ArrayList<>();
        gajiAdapter = new GajiAdapter(karyawanList, this);
        recyclerViewGaji.setAdapter(gajiAdapter);

        // Set pilihan bulan pada spinner
        String[] bulanArray = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        ArrayAdapter<String> bulanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bulanArray);
        spinnerBulan.setAdapter(bulanAdapter);

        // Event saat bulan dipilih
        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBulan = bulanArray[position];
                fetchKaryawanData(); // Ambil ulang data saat bulan berubah
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Ambil data karyawan dari Firebase
        fetchKaryawanData();
    }

    private void fetchKaryawanData() {
        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                karyawanList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Karyawan karyawan = dataSnapshot.getValue(Karyawan.class);

                    // Pastikan data tidak null dan memiliki username
                    if (karyawan != null && karyawan.getUsername() != null && !karyawan.getUsername().isEmpty()) {

                        // Filter agar hanya karyawan (bukan admin) yang muncul
                        if (karyawan.getRole() == null || !karyawan.getRole().equalsIgnoreCase("admin")) {
                            karyawanList.add(karyawan);
                        }
                    }
                }

                // Perbarui RecyclerView setelah mendapatkan data
                gajiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GajiActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton buttonBackAdmin = findViewById(R.id.buttonBackAdmin);
        buttonBackAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(GajiActivity.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Menutup halaman saat ini
        });
    }
}
