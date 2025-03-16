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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import android.util.Log;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.ViewHolder> {

    private List<Karyawan> karyawanList;
    private Context context;

    public KaryawanAdapter(List<Karyawan> karyawanList, Context context) {
        this.karyawanList = karyawanList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_karyawan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Karyawan karyawan = karyawanList.get(position);

        holder.textViewName.setText("Nama: " + karyawan.getName());
        holder.textViewDepartment.setText("Departemen: " + karyawan.getDepartment());
        holder.textViewPhone.setText("Nomor Telepon: " + karyawan.getPhone());
        holder.textViewAddress.setText("Alamat: " + karyawan.getAddress());

        // Tombol Hapus
        holder.buttonDelete.setOnClickListener(v -> deleteKaryawan(karyawan.getUsername(), position));

        // Tombol Edit
        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditKaryawanActivity.class);
            intent.putExtra("username", karyawan.getUsername());  // Mengirim username
            intent.putExtra("name", karyawan.getName());
            intent.putExtra("department", karyawan.getDepartment());
            intent.putExtra("phone", karyawan.getPhone());
            intent.putExtra("address", karyawan.getAddress());
            intent.putExtra("password", karyawan.getPassword());

            Log.d("KaryawanAdapter", "Mengirim username: " + karyawan.getUsername()); // Debug
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return karyawanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDepartment, textViewPhone, textViewAddress;
        Button buttonDelete, buttonEdit; // Tambahkan buttonEdit

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inisialisasi komponen tampilan
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDepartment = itemView.findViewById(R.id.textViewDepartment);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit); // Tambahkan ini
        }
    }

    private void deleteKaryawan(String username, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(username).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        karyawanList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Karyawan berhasil dihapus", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Gagal menghapus karyawan", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
