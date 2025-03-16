package com.example.uaspenggajiankaryawan;

import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;
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

public class PengumumanKaryawanActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PengumumanAdapter adapter;
    private List<Pengumuman> pengumumanList;
    private DatabaseReference databaseReference;
    private ImageButton buttonBackPengumuman; // Inisialisasi tombol kembali

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengumuman_karyawan);

        recyclerView = findViewById(R.id.recyclerViewPengumuman);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pengumumanList = new ArrayList<>();

        // Here, pass 'false' for isAdmin as this is for employees (karyawan)
        adapter = new PengumumanAdapter(this, pengumumanList, false);  // false means karyawan (employee)
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Pengumuman");

        // Mengambil referensi ke buttonBackPengumuman
        buttonBackPengumuman = findViewById(R.id.buttonBackPengumuman);

        // Menambahkan listener pada tombol kembali
        buttonBackPengumuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fungsi tombol kembali untuk menyelesaikan aktivitas ini dan kembali ke activity sebelumnya
                finish();
            }
        });

        // Memuat pengumuman dari database
        loadPengumuman();
    }

    private void loadPengumuman() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pengumumanList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pengumuman pengumuman = dataSnapshot.getValue(Pengumuman.class);
                    pengumumanList.add(pengumuman);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PengumumanKaryawanActivity.this, "Gagal memuat data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}