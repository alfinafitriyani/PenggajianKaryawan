package com.example.uaspenggajiankaryawan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileKaryawanActivity extends AppCompatActivity {

    private TextView textViewName, textViewDepartment, textViewAddress, textViewPhone;
    private ImageView profileImage;
    private DatabaseReference databaseReference;
    private static final String TAG = "ProfileKaryawanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_karyawan);

        textViewName = findViewById(R.id.textViewName);
        textViewDepartment = findViewById(R.id.textViewDepartment);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewPhone = findViewById(R.id.textViewPhone);
        profileImage = findViewById(R.id.imageViewProfile);

        // Tombol Kembali
        ImageButton buttonBack = findViewById(R.id.buttonBackProfileKaryawan);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigasi kembali ke KaryawanActivity
                Intent intent = new Intent(ProfileKaryawanActivity.this, KaryawanActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Periksa koneksi internet
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (!username.isEmpty()) {
            Log.d(TAG, "Mengambil data untuk username: " + username);
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "Data ditemukan: " + dataSnapshot.getValue());

                        String name = dataSnapshot.child("name").getValue(String.class);
                        String department = dataSnapshot.child("department").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        textViewName.setText("Nama:" +name);
                        textViewDepartment.setText("Departemen:" + department);
                        textViewAddress.setText("Alamat: " + address);
                        textViewPhone.setText("Telepon: " + phone);
                    } else {
                        Log.e(TAG, "Data tidak ditemukan untuk username: " + username);
                        Toast.makeText(ProfileKaryawanActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Gagal mengambil data: " + databaseError.getMessage());
                    Toast.makeText(ProfileKaryawanActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Username tidak ditemukan di SharedPreferences");
            Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
