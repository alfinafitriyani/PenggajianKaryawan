package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

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

public class DataKaryawanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KaryawanAdapter adapter;
    private List<Karyawan> karyawanList;
    private DatabaseReference databaseReference;
    private Button buttonTambahKaryawan;
    private TextView textViewJumlahKaryawan;
    private ImageButton buttonBackAdmin;// Perbaikan: Tambahkan deklarasi TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_karyawan);


        // Inisialisasi UI
        recyclerView = findViewById(R.id.recyclerViewKaryawan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textViewJumlahKaryawan = findViewById(R.id.textViewJumlahKaryawan); // Pastikan tidak NULL
        buttonTambahKaryawan = findViewById(R.id.buttonTambahKaryawan);

        // Tombol untuk menambah karyawan
        buttonTambahKaryawan.setOnClickListener(v ->
                startActivity(new Intent(DataKaryawanActivity.this, TambahKaryawanActivity.class))
        );

        ImageButton buttonBackAdmin = findViewById(R.id.buttonBackAdmin);
        buttonBackAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(DataKaryawanActivity.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Menutup halaman saat ini
        });

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        karyawanList = new ArrayList<>();
        adapter = new KaryawanAdapter(karyawanList, this);
        recyclerView.setAdapter(adapter);

        // Ambil data dari Firebase
        fetchKaryawanData();
    }

    private void fetchKaryawanData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                karyawanList.clear();
                int jumlahKaryawan = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    Karyawan karyawan = dataSnapshot.getValue(Karyawan.class);
                    if (karyawan != null && "user".equals(karyawan.getRole())) {
                        karyawan.setUsername(key);
                        karyawanList.add(karyawan);
                        jumlahKaryawan++; // Tambahkan jumlah karyawan
                    }
                }

                // Perbaikan: Update jumlah karyawan
                textViewJumlahKaryawan.setText(String.format("Total Karyawan: %d", jumlahKaryawan));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DataKaryawanActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
