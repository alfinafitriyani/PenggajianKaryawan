package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TambahKaryawanActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextConfirmPassword, editTextName, editTextPhone, editTextAddress;
    private Spinner spinnerDepartment;
    private Button buttonCreateKaryawan;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_karyawan);

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Inisialisasi UI
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        buttonCreateKaryawan = findViewById(R.id.buttonCreateKaryawan);

        // Data untuk Spinner (Departemen)
        String[] departments = {"HR", "Finance", "IT", "Marketing", "Operations"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Event Listener untuk tombol tambah karyawan
        buttonCreateKaryawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahKaryawan();
            }
        });
    }

    private void tambahKaryawan() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String department = spinnerDepartment.getSelectedItem().toString();
        String role = "user"; // Default role sebagai "user"

        // Validasi input
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username tidak boleh kosong!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password tidak boleh kosong!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Password tidak cocok!");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Nama tidak boleh kosong!");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Nomor telepon tidak boleh kosong!");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError("Alamat tidak boleh kosong!");
            return;
        }

        // Cek apakah username sudah terdaftar
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(TambahKaryawanActivity.this, "Username sudah digunakan!", Toast.LENGTH_SHORT).show();
                } else {
                    // Simpan Data ke Firebase
                    DatabaseReference userRef = databaseReference.child(username);
                    userRef.child("username").setValue(username);
                    userRef.child("password").setValue(password);
                    userRef.child("name").setValue(name);
                    userRef.child("phone").setValue(phone);
                    userRef.child("address").setValue(address);
                    userRef.child("department").setValue(department);
                    userRef.child("role").setValue(role);

                    Toast.makeText(TambahKaryawanActivity.this, "Karyawan berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke halaman DataKaryawanActivity setelah sukses
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(TambahKaryawanActivity.this, "Terjadi kesalahan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
