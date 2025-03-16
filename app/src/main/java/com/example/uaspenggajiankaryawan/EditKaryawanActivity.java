package com.example.uaspenggajiankaryawan;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.List;

public class EditKaryawanActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword, editTextConfirmPassword, editTextName, editTextPhone, editTextAddress;
    private Spinner spinnerDepartment;
    private Button buttonUpdateKaryawan, buttonCancel;
    private DatabaseReference databaseReference;
    private String oldUsername; // Pastikan digunakan dengan benar

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_karyawan);

        // Inisialisasi view
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        buttonUpdateKaryawan = findViewById(R.id.buttonUpdateKaryawan);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Data untuk Spinner (Departemen)
        List<String> departments = Arrays.asList("HR", "Finance", "IT", "Marketing", "Operations");
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Mendapatkan data dari intent
        oldUsername = getIntent().getStringExtra("username"); // Perbaikan: Gunakan variabel global
        String name = getIntent().getStringExtra("name");
        String department = getIntent().getStringExtra("department");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");
        String password = getIntent().getStringExtra("password");

        // Set data ke dalam form
        editTextUsername.setText(oldUsername);
        editTextPassword.setText(password);
        editTextConfirmPassword.setText(password); // Tampilkan password lama juga di konfirmasi
        editTextName.setText(name);
        editTextPhone.setText(phone);
        editTextAddress.setText(address);

        // Set pilihan departemen sesuai data lama
        if (department != null) {
            int spinnerPosition = departments.indexOf(department);
            if (spinnerPosition >= 0) {
                spinnerDepartment.setSelection(spinnerPosition);
            }
        }

        // Username tidak boleh diedit
        editTextUsername.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Event untuk tombol Update
        buttonUpdateKaryawan.setOnClickListener(v -> updateKaryawan());

        // Event untuk tombol Cancel
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void updateKaryawan() {
        String newPassword = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String newName = editTextName.getText().toString();
        String newPhone = editTextPhone.getText().toString();
        String newAddress = editTextAddress.getText().toString();
        String newDepartment = spinnerDepartment.getSelectedItem().toString();

        // Validasi password harus sama
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Password dan Konfirmasi Password tidak cocok!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Periksa apakah oldUsername valid
        if (oldUsername == null || oldUsername.isEmpty()) {
            Toast.makeText(this, "Terjadi kesalahan: Username tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update data di Firebase berdasarkan username lama
        DatabaseReference userRef = databaseReference.child(oldUsername);
        userRef.child("name").setValue(newName);
        userRef.child("phone").setValue(newPhone);
        userRef.child("address").setValue(newAddress);
        userRef.child("department").setValue(newDepartment);
        userRef.child("password").setValue(newPassword) // Perbaikan: Tambah titik koma di sini!
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditKaryawanActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditKaryawanActivity.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show());
    }
}
