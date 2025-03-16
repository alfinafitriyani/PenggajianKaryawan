package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class PengumumanActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PengumumanAdapter adapter;
    private List<Pengumuman> pengumumanList;
    private List<Pengumuman> filteredList; // List untuk hasil filter
    private DatabaseReference databaseReference;
    private EditText searchPengumuman;
    private Button buttonTambahPengumuman;
    private ImageButton buttonBackPengumuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengumuman);

        recyclerView = findViewById(R.id.recyclerViewPengumuman);
        searchPengumuman = findViewById(R.id.searchPengumuman);
        buttonTambahPengumuman = findViewById(R.id.buttonTambahPengumuman);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pengumumanList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Hanya fitur hapus tanpa edit
        adapter = new PengumumanAdapter(this, filteredList, true);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Pengumuman");

        loadPengumuman();

        buttonTambahPengumuman.setOnClickListener(v -> {
            Intent intent = new Intent(PengumumanActivity.this, TambahPengumumanActivity.class);
            startActivity(intent);
        });
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

        // Menambahkan listener pada searchPengumuman untuk menangani pencarian
        searchPengumuman.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterPengumuman(charSequence.toString()); // Panggil fungsi filter saat teks berubah
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void loadPengumuman() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pengumumanList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pengumuman pengumuman = dataSnapshot.getValue(Pengumuman.class);
                    if (pengumuman != null) {
                        pengumumanList.add(pengumuman);
                    }
                }
                filterPengumuman(searchPengumuman.getText().toString()); // Filter setelah memuat data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PengumumanActivity.this, "Gagal memuat data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterPengumuman(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(pengumumanList); // Jika query kosong, tampilkan semua pengumuman
        } else {
            for (Pengumuman pengumuman : pengumumanList) {
                if (pengumuman.getJudul().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(pengumuman); // Tambahkan jika sesuai dengan pencarian
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
