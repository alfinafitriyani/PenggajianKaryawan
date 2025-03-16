package com.example.uaspenggajiankaryawan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GajiAdapter extends RecyclerView.Adapter<GajiAdapter.ViewHolder> {
    private List<Karyawan> karyawanList;
    private Context context;

    public GajiAdapter(List<Karyawan> karyawanList, Context context) {
        this.karyawanList = karyawanList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gaji, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Karyawan karyawan = karyawanList.get(position);

        holder.textViewName.setText(karyawan.getName());
        holder.textViewDepartment.setText("Departemen: " + karyawan.getDepartment());

        // Cek apakah username null sebelum mengambil data
        if (karyawan.getUsername() == null || karyawan.getUsername().isEmpty()) {
            holder.textViewGajiPokok.setText("Username tidak tersedia");
            return; // Stop proses jika username tidak ada
        }

        String username = karyawan.getUsername();

        // Ambil data gaji berdasarkan username
        loadGaji(username, holder);


        // Tombol tambah gaji
        holder.buttonTambahGaji.setOnClickListener(v -> {
            if (GajiActivity.selectedBulan == null || GajiActivity.selectedBulan.isEmpty()) {
                Toast.makeText(context, "Pilih bulan terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, TambahGajiActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("nama", karyawan.getName());
            intent.putExtra("departemen", karyawan.getDepartment());
            intent.putExtra("bulan", GajiActivity.selectedBulan); // Kirim data bulan
            context.startActivity(intent);
        });


    }

    // Fungsi untuk mengambil data gaji
    private void loadGaji(String username, ViewHolder holder) {
        if (GajiActivity.selectedBulan == null || GajiActivity.selectedBulan.isEmpty()) {
            Toast.makeText(context, "Pilih bulan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference gajiRef = FirebaseDatabase.getInstance().getReference("gaji")
                .child(username)
                .child(GajiActivity.selectedBulan);

        gajiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Gaji gaji = snapshot.getValue(Gaji.class);
                    if (gaji != null) {
                        holder.textViewGajiPokok.setText("Gaji Pokok: Rp " + gaji.getGajiPokok());
                        holder.textViewTunjangan.setText("Tunjangan: Rp " + gaji.getTunjangan());
                        holder.textViewPotongan.setText("Potongan: Rp " + gaji.getPotongan());
                        holder.textViewTotalGaji.setText("Total Gaji: Rp " + gaji.getTotalGaji());
                    }
                } else {
                    holder.textViewGajiPokok.setText("Gaji Pokok: Rp -");
                    holder.textViewTunjangan.setText("Tunjangan: Rp -");
                    holder.textViewPotongan.setText("Potongan: Rp -");
                    holder.textViewTotalGaji.setText("Total Gaji: Rp -");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Gagal mengambil data gaji", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return karyawanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDepartment, textViewGajiPokok, textViewTunjangan, textViewPotongan, textViewTotalGaji;
        Button buttonTambahGaji, buttonEditGaji;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDepartment = itemView.findViewById(R.id.textViewDepartment);
            textViewGajiPokok = itemView.findViewById(R.id.textViewGajiPokok);
            textViewTunjangan = itemView.findViewById(R.id.textViewTunjangan);
            textViewPotongan = itemView.findViewById(R.id.textViewPotongan);
            textViewTotalGaji = itemView.findViewById(R.id.textViewTotalGaji);
            buttonTambahGaji = itemView.findViewById(R.id.buttonTambahGaji);
        }
    }
}
