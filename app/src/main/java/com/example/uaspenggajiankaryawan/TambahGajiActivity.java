package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TambahGajiActivity extends AppCompatActivity {
    private TextView textNamaKaryawan, textDepartemen, textBulan;
    private EditText editGajiPokok, editTunjangan, editPotongan;
    private Button buttonSimpanGaji, buttonCancel; // Tambah buttonCancel
    private DatabaseReference databaseReference;
    private String username, nama, departemen, bulan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_gaji);

        // Ambil data dari Intent
        username = getIntent().getStringExtra("username");
        nama = getIntent().getStringExtra("nama");
        departemen = getIntent().getStringExtra("departemen");
        bulan = GajiActivity.selectedBulan; // Ambil bulan dari Spinner di GajiActivity

        // Inisialisasi Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("gaji");

        // Inisialisasi UI
        textNamaKaryawan = findViewById(R.id.textViewName);
        textDepartemen = findViewById(R.id.textViewDepartment);
        textBulan = findViewById(R.id.textViewBulan);
        editGajiPokok = findViewById(R.id.editTextGajiPokok);
        editTunjangan = findViewById(R.id.editTextTunjangan);
        editPotongan = findViewById(R.id.editTextPotongan);
        buttonSimpanGaji = findViewById(R.id.buttonSimpanGaji);
        buttonCancel = findViewById(R.id.buttonCancel); // Inisialisasi buttonCancel

        // Set teks ke TextView
        textNamaKaryawan.setText("Nama: " + nama);
        textDepartemen.setText("Departemen: " + departemen);
        textBulan.setText("Bulan: " + bulan);

        // Set gaji pokok otomatis berdasarkan departemen
        setGajiPokok();

        // Tambahkan listener untuk menghitung total gaji saat input berubah
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hitungTotalGaji();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editGajiPokok.addTextChangedListener(textWatcher);
        editTunjangan.addTextChangedListener(textWatcher);
        editPotongan.addTextChangedListener(textWatcher);

        // Event listener untuk tombol Simpan Gaji
        buttonSimpanGaji.setOnClickListener(v -> simpanGaji());

        // Event listener untuk tombol Cancel
        buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(TambahGajiActivity.this, GajiActivity.class);
            startActivity(intent);
            finish(); // Menutup halaman ini agar tidak kembali saat tombol "Back" ditekan
        });
    }

    private void setGajiPokok() {
        if (departemen != null) {
            switch (departemen) {
                case "IT":
                    editGajiPokok.setText("3000000");
                    break;
                case "HR":
                    editGajiPokok.setText("2500000");
                    break;
                case "Finance":
                    editGajiPokok.setText("2800000");
                    break;
                default:
                    editGajiPokok.setText("2000000");
                    break;
            }
        }
    }

    private void hitungTotalGaji() {
        // Pastikan input tidak kosong
        String gajiPokokStr = editGajiPokok.getText().toString();
        String tunjanganStr = editTunjangan.getText().toString();
        String potonganStr = editPotongan.getText().toString();

        int gajiPokok = TextUtils.isEmpty(gajiPokokStr) ? 0 : Integer.parseInt(gajiPokokStr);
        int tunjangan = TextUtils.isEmpty(tunjanganStr) ? 0 : Integer.parseInt(tunjanganStr);
        int potongan = TextUtils.isEmpty(potonganStr) ? 0 : Integer.parseInt(potonganStr);

        int totalGaji = gajiPokok + tunjangan - potongan;

        // Menampilkan total gaji ke UI (Tambahkan TextView `textTotalGaji` di XML)
        TextView textTotalGaji = findViewById(R.id.textViewTotalGaji);
        textTotalGaji.setText("Total Gaji: " + totalGaji);
    }


    private void simpanGaji() {
        if (TextUtils.isEmpty(bulan)) {
            Toast.makeText(this, "Pilih bulan terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        int gajiPokok = Integer.parseInt(editGajiPokok.getText().toString());
        int tunjangan = Integer.parseInt(editTunjangan.getText().toString());
        int potongan = Integer.parseInt(editPotongan.getText().toString());

        int totalGaji = gajiPokok + tunjangan - potongan;

        Gaji gaji = new Gaji(username, bulan, gajiPokok, tunjangan, potongan, totalGaji);

        databaseReference.child(username).child(bulan).setValue(gaji)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TambahGajiActivity.this, "Gaji berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(TambahGajiActivity.this, "Gagal menyimpan gaji!", Toast.LENGTH_SHORT).show());
    }
}
