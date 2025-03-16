package com.example.uaspenggajiankaryawan;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailPengumumanActivity extends AppCompatActivity {

    private TextView judul, deskripsi, tanggal;
    private ImageView gambar;
    private ImageButton buttonBack; // Tombol kembali

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengumuman);

        // Inisialisasi Views
        judul = findViewById(R.id.textJudulDetail);
        deskripsi = findViewById(R.id.textDeskripsiDetail);
        tanggal = findViewById(R.id.textTanggalDetail);
        gambar = findViewById(R.id.imagePengumumanDetail);
        buttonBack = findViewById(R.id.buttonBackDetailKaryawan); // Inisialisasi tombol kembali

        // Menambahkan listener untuk tombol kembali
        buttonBack.setOnClickListener(v -> {
            // Menyelesaikan activity ini dan kembali ke aktivitas sebelumnya
            finish();
        });

        // Mendapatkan data dari Intent
        String id = getIntent().getStringExtra("id");
        String judulText = getIntent().getStringExtra("judul");
        String deskripsiText = getIntent().getStringExtra("deskripsi");
        String tanggalText = getIntent().getStringExtra("tanggal");
        String gambarUrl = getIntent().getStringExtra("gambar");

        // Set data ke tampilan
        judul.setText(judulText);
        deskripsi.setText(deskripsiText);
        tanggal.setText(tanggalText);

        // Menampilkan gambar dengan Glide
        Glide.with(this)
                .load(gambarUrl)
                .placeholder(R.drawable.placeholder) // pastikan ada placeholder yang sesuai
                .into(gambar);
    }
}
