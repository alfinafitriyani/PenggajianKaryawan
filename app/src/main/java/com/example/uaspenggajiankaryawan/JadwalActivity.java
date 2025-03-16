package com.example.uaspenggajiankaryawan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.UUID;

public class JadwalActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView txtTanggalDipilih, txtProyekDetail;
    private EditText edtNamaProyek, edtDeadline;
    private Button btnTambahProyek;
    private String selectedDate;
    private DatabaseReference databaseReference;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        // Inisialisasi Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("jadwal_proyek");

        // Hubungkan komponen dengan ID di XML
        calendarView = findViewById(R.id.calendarView);
        txtTanggalDipilih = findViewById(R.id.txtTanggalDipilih);
        txtProyekDetail = findViewById(R.id.txtProyekDetail);
        edtNamaProyek = findViewById(R.id.edtNamaProyek);
        edtDeadline = findViewById(R.id.edtDeadline);
        btnTambahProyek = findViewById(R.id.btnTambahProyek);
        // Mengambil referensi ke buttonBackPengumuman
        buttonBack = findViewById(R.id.buttonBack);


        // Menambahkan listener pada tombol kembali
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fungsi tombol kembali untuk menyelesaikan aktivitas ini dan kembali ke activity sebelumnya
                finish();
            }
        });

        // Saat tanggal di kalender diklik
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            txtTanggalDipilih.setText("Tanggal Dipilih: " + selectedDate);

            // Ambil proyek berdasarkan tanggal yang dipilih
            ambilProyek(selectedDate);
        });

        // Saat tombol tambah proyek diklik
        btnTambahProyek.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Silakan pilih tanggal terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            String proyek = edtNamaProyek.getText().toString().trim();
            String deadline = edtDeadline.getText().toString().trim();

            if (proyek.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Harap isi proyek dan deadline!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Buat ID unik untuk proyek
            String id = UUID.randomUUID().toString();
            JadwalProyek jadwal = new JadwalProyek(id, selectedDate, proyek, deadline);

            // Simpan ke Firebase
            databaseReference.child(id).setValue(jadwal)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Proyek berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        edtNamaProyek.setText("");
                        edtDeadline.setText("");

                        // Refresh daftar proyek
                        ambilProyek(selectedDate);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Gagal menambahkan proyek!", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void ambilProyek(String tanggal) {
        databaseReference.orderByChild("tanggal").equalTo(tanggal)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            StringBuilder proyekList = new StringBuilder();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                JadwalProyek proyek = data.getValue(JadwalProyek.class);
                                proyekList.append("Proyek: ").append(proyek.getNamaProyek())
                                        .append("\nDeadline: ").append(proyek.getDeadline())
                                        .append("\n\n");
                            }
                            txtProyekDetail.setText(proyekList.toString());
                        } else {
                            txtProyekDetail.setText("Tidak ada proyek pada tanggal ini.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(JadwalActivity.this, "Gagal mengambil data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
