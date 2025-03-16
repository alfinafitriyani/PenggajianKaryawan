package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JadwalKaryawanActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView txtProyekDetail;
    private DatabaseReference databaseReference;
    private String selectedDate;
    private ImageButton buttonBack;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_karyawan);

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("jadwal_proyek");

        // Hubungkan dengan ID di XML
        calendarView = findViewById(R.id.calendarView);
        txtProyekDetail = findViewById(R.id.txtProyekDetail);

        // Saat tanggal di kalender diklik
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            ambilProyek(selectedDate);
        });
        // Tombol Kembali
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigasi kembali ke KaryawanActivity
                Intent intent = new Intent(JadwalKaryawanActivity.this, KaryawanActivity.class);
                startActivity(intent);
                finish();
            }
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
                        Toast.makeText(JadwalKaryawanActivity.this, "Gagal mengambil data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
