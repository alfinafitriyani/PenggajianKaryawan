package com.example.uaspenggajiankaryawan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Pastikan Glide sudah diimpor
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PengumumanAdapter extends RecyclerView.Adapter<PengumumanAdapter.ViewHolder> {
    private Context context;
    private List<Pengumuman> pengumumanList;
    private boolean isAdmin; // Variabel untuk menentukan mode admin/karyawan

    public PengumumanAdapter(Context context, List<Pengumuman> pengumumanList, boolean isAdmin) {
        this.context = context;
        this.pengumumanList = pengumumanList;
        this.isAdmin = isAdmin;  // This will determine if the user is an admin or not
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pengumuman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pengumuman pengumuman = pengumumanList.get(position);
        holder.judul.setText(pengumuman.getJudul());
        holder.tanggal.setText(pengumuman.getTanggal());

        Glide.with(context)
                .load(pengumuman.getGambar())
                .placeholder(R.drawable.placeholder)
                .into(holder.gambar);

        // Klik untuk melihat detail pengumuman
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailPengumumanActivity.class);
            intent.putExtra("id", pengumuman.getId());
            intent.putExtra("judul", pengumuman.getJudul());
            intent.putExtra("deskripsi", pengumuman.getDeskripsi());
            intent.putExtra("tanggal", pengumuman.getTanggal());
            intent.putExtra("gambar", pengumuman.getGambar());
            context.startActivity(intent);
        });

        // Jika mode admin, tampilkan tombol Hapus
        if (isAdmin) {
            holder.buttonHapus.setVisibility(View.VISIBLE);

            holder.buttonHapus.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus Pengumuman")
                        .setMessage("Apakah Anda yakin ingin menghapus pengumuman ini?")
                        .setPositiveButton("Ya", (dialog, which) -> {
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Pengumuman").child(pengumuman.getId());
                            dbRef.removeValue().addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Pengumuman dihapus", Toast.LENGTH_SHORT).show();
                                pengumumanList.remove(position);
                                notifyDataSetChanged();
                            }).addOnFailureListener(e -> Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        } else {
            // Jika bukan admin (karyawan), sembunyikan tombol Hapus
            holder.buttonHapus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return pengumumanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView judul, tanggal;
        ImageView gambar;
        Button buttonHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.textJudul);
            tanggal = itemView.findViewById(R.id.textTanggal);
            gambar = itemView.findViewById(R.id.imagePengumuman);
            buttonHapus = itemView.findViewById(R.id.buttonHapus);
        }
    }
}
