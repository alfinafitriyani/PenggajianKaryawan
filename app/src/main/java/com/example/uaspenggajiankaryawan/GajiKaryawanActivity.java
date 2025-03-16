package com.example.uaspenggajiankaryawan;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class GajiKaryawanActivity extends AppCompatActivity {

    private TextView textViewNama, textViewBulan, textViewGajiPokok, textViewTunjangan, textViewPotongan, textViewTotalGaji;
    private Spinner spinnerBulan;
    private Button buttonDownloadPDF;
    private DatabaseReference databaseReference;
    private String username, nama, bulan;
    private ImageButton buttonBackGaji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaji_karyawan);

        textViewNama = findViewById(R.id.textViewName);
        textViewBulan = findViewById(R.id.textViewBulan);
        textViewGajiPokok = findViewById(R.id.textViewGajiPokok);
        textViewTunjangan = findViewById(R.id.textViewTunjangan);
        textViewPotongan = findViewById(R.id.textViewPotongan);
        textViewTotalGaji = findViewById(R.id.textViewTotalGaji);
        spinnerBulan = findViewById(R.id.spinnerBulan);
        buttonDownloadPDF = findViewById(R.id.buttonDownloadPDF);
        buttonBackGaji = findViewById(R.id.buttonBackGaji);

        // Tombol Kembali
        buttonBackGaji.setOnClickListener(v -> {
            Intent intent = new Intent(GajiKaryawanActivity.this, KaryawanActivity.class);
            startActivity(intent);
            finish();
        });

        // Dapatkan username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        if (username == null) {
            Toast.makeText(this, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ambil Nama dari Database Firebase
        ambilNamaDariFirebase();

        // Atur Spinner Bulan
        setUpSpinnerBulan();

        // Periksa izin penyimpanan untuk PDF
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        buttonDownloadPDF.setOnClickListener(view -> generatePDF());
    }

    private void ambilNamaDariFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("name")) {
                    nama = snapshot.child("name").getValue(String.class);
                    textViewNama.setText("Nama: " + nama);
                } else {
                    textViewNama.setText("Nama: Tidak ditemukan");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewNama.setText("Nama: Error memuat data");
            }
        });
    }

    private void setUpSpinnerBulan() {
        List<String> bulanList = Arrays.asList("Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bulanList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBulan.setAdapter(adapter);

        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bulan = parent.getItemAtPosition(position).toString();
                ambilDataGaji();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void ambilDataGaji() {
        if (username == null || bulan == null) {
            Toast.makeText(this, "Username atau bulan tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("gaji").child(username).child(bulan);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    textViewBulan.setText("Bulan: " + bulan);
                    textViewGajiPokok.setText("Gaji Pokok: Rp " + snapshot.child("gajiPokok").getValue());
                    textViewTunjangan.setText("Tunjangan: Rp " + snapshot.child("tunjangan").getValue());
                    textViewPotongan.setText("Potongan: Rp " + snapshot.child("potongan").getValue());
                    textViewTotalGaji.setText("Total Gaji: Rp " + snapshot.child("totalGaji").getValue());
                } else {
                    Toast.makeText(GajiKaryawanActivity.this, "Data gaji tidak ditemukan untuk bulan " + bulan, Toast.LENGTH_SHORT).show();
                    textViewBulan.setText("Bulan: " + bulan);
                    textViewGajiPokok.setText("Gaji Pokok: Rp -");
                    textViewTunjangan.setText("Tunjangan: Rp -");
                    textViewPotongan.setText("Potongan: Rp -");
                    textViewTotalGaji.setText("Total Gaji: Rp -");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GajiKaryawanActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePDF() {
        if (textViewTotalGaji.getText().toString().contains("-")) {
            Toast.makeText(this, "Data gaji belum lengkap!", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint boldPaint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(500, 750, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int startX = 50, startY = 50, lineSpacing = 30;
        int pageWidth = pageInfo.getPageWidth();
        int centerX = pageWidth / 2;
        int rightAlignX = 350; // Posisi tanda tangan di kanan
        int leftAlignX = startX + 20; // Posisi teks ke kiri lebih rapi

        // Header Perusahaan (dibuat lebih simetris dan center)
        boldPaint.setTextSize(20);
        boldPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("PT. Arta Jaya", centerX, startY, boldPaint);

        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Jl. H. Sulhan No.126 B, Jepara, Jateng", centerX, startY + 20, paint);
        canvas.drawText("Telp: 085727141432", centerX, startY + 40, paint);

        // Garis Pembatas
        startY += 60;
        canvas.drawLine(startX, startY, startX + 400, startY, linePaint);
        startY += 30;

        // Judul Slip Gaji
        boldPaint.setTextSize(16);
        boldPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Slip Gaji " + textViewBulan.getText().toString().replace("Bulan: ", ""), centerX, startY, boldPaint);
        startY += 40;

        // Nama Karyawan dipindah ke kiri
        boldPaint.setTextSize(14);
        boldPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(textViewNama.getText().toString(), leftAlignX, startY, boldPaint);
        startY += lineSpacing;

        // Rincian Gaji (rata kiri)
        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(textViewGajiPokok.getText().toString(), leftAlignX, startY, paint);
        startY += lineSpacing;

        canvas.drawText(textViewTunjangan.getText().toString(), leftAlignX, startY, paint);
        startY += lineSpacing;

        canvas.drawText(textViewPotongan.getText().toString(), leftAlignX, startY, paint);

        // Garis Pembatas
        startY += 20;
        canvas.drawLine(startX, startY, startX + 400, startY, linePaint);
        startY += 30;

        // Total Gaji (rata kiri)
        boldPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(textViewTotalGaji.getText().toString(), leftAlignX, startY, boldPaint);

        // Tentukan posisi paling kanan agar lebih rapat ke tepi
        int rightMostX = pageWidth - 50;

        // Tanggal lebih ke kanan
                startY += 80;
                boldPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Buana Arta", rightMostX, startY, boldPaint);

        // Beri jarak lebih besar sebelum tanda tangan
                startY += 50;
                paint.setTextSize(14);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("(Manajer Operasional)", rightMostX, startY, paint);


        pdfDocument.finishPage(page);

        // Simpan PDF
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            savePdfToDownloadsQ(pdfDocument);
        } else {
            savePdfToDownloadsLegacy(pdfDocument);
        }

        pdfDocument.close();
    }





    // Metode untuk Android 10 ke atas (tanpa izin tambahan)
    private void savePdfToDownloadsQ(PdfDocument pdfDocument) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, "SlipGaji_" + nama +"_"+ bulan + ".pdf");
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream);
                    Toast.makeText(this, "PDF tersimpan di folder Download", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Gagal menyimpan PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Metode untuk Android 9 ke bawah (membutuhkan izin WRITE_EXTERNAL_STORAGE)
    private void savePdfToDownloadsLegacy(PdfDocument pdfDocument) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadsDir, "Slip_Gaji_" + bulan + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            pdfDocument.writeTo(fos);
            Toast.makeText(this, "PDF tersimpan di: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error menyimpan PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with downloading the PDF
            } else {
                Toast.makeText(this, "Permission to write to storage is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
