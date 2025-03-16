package com.example.uaspenggajiankaryawan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TambahPengumumanActivity extends AppCompatActivity {

    private EditText editJudul, editTanggal, editDeskripsi;
    private TextView textViewNamaGambar;
    private ImageView imageViewPilihGambar;
    private Button buttonSimpanPengumuman, buttonCancel;
    private Uri gambarUri; // Menyimpan URI gambar yang dipilih
    private Calendar calendar; // Objek untuk menangani tanggal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pengumuman);

        editJudul = findViewById(R.id.editJudul);
        editTanggal = findViewById(R.id.editTanggal);
        editDeskripsi = findViewById(R.id.editDeskripsi);
        textViewNamaGambar = findViewById(R.id.textViewNamaGambar);
        imageViewPilihGambar = findViewById(R.id.imageViewPilihGambar);
        buttonSimpanPengumuman = findViewById(R.id.buttonSimpanPengumuman);
        buttonCancel = findViewById(R.id.buttonCancel);

        calendar = Calendar.getInstance(); // Inisialisasi kalender

        // Ketika editTanggal diklik, munculkan DatePickerDialog
        editTanggal.setOnClickListener(v -> showDatePicker());

        imageViewPilihGambar.setOnClickListener(v -> pilihGambar());
        buttonSimpanPengumuman.setOnClickListener(v -> simpanPengumuman());
    }


    // Fungsi untuk menampilkan DatePickerDialog
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    editTanggal.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Fungsi untuk memilih gambar dari galeri
    private void pilihGambar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    // Fungsi untuk menangani gambar yang dipilih
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            gambarUri = data.getData();

            // Menampilkan nama file gambar yang dipilih
            if (gambarUri != null) {
                String fileName = gambarUri.getLastPathSegment(); // Ambil nama file
                textViewNamaGambar.setText(fileName);
            }
        }
    }

    // Fungsi untuk menyimpan pengumuman ke Firebase
    private void simpanPengumuman() {
        String judul = editJudul.getText().toString().trim();
        String tanggal = editTanggal.getText().toString().trim();
        String deskripsi = editDeskripsi.getText().toString().trim();

        if (judul.isEmpty() || tanggal.isEmpty() || deskripsi.isEmpty() || gambarUri == null) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Menyimpan pengumuman ke Firebase Database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Pengumuman");
        String id = dbRef.push().getKey(); // Generate ID otomatis
        Map<String, Object> pengumumanMap = new HashMap<>();
        pengumumanMap.put("id", id);
        pengumumanMap.put("judul", judul);
        pengumumanMap.put("tanggal", tanggal);
        pengumumanMap.put("deskripsi", deskripsi);
        pengumumanMap.put("gambar", gambarUri.toString()); // Simpan URI gambar sebagai string

        if (id != null) {
            dbRef.child(id).setValue(pengumumanMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(TambahPengumumanActivity.this, "Pengumuman berhasil disimpan", Toast.LENGTH_SHORT).show();
                        finish(); // Kembali ke halaman sebelumnya
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TambahPengumumanActivity.this, "Gagal menyimpan pengumuman", Toast.LENGTH_SHORT).show();
                    });
        }
        buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(TambahPengumumanActivity.this, PengumumanActivity.class);
            startActivity(intent);
            finish(); // Menutup halaman ini agar tidak kembali saat tombol "Back" ditekan
        });
    }
}
