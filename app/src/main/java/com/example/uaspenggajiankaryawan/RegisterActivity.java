package com.example.uaspenggajiankaryawan;

import android.content.Intent;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextConfirmPassword, editTextName, editTextPhone, editTextAddress;
    private Spinner spinnerDepartment;
    private Button buttonRegister;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi View
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        // Referensi Firebase Database
        database = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://db-uaspenggajiankaryawan-default-rtdb.firebaseio.com/");

        // Data untuk Spinner (Departemen)
        String[] departments = {"HR", "Finance", "IT", "Marketing", "Operations"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Set Listener untuk Tombol Register
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String department = spinnerDepartment.getSelectedItem().toString();

            // Validasi Input
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    name.isEmpty() || phone.isEmpty() || address.isEmpty() || department.equals("Pilih Departemen")) {
                Toast.makeText(getApplicationContext(), "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(getApplicationContext(), "Password tidak cocok!", Toast.LENGTH_SHORT).show();
            } else {
                // Cek apakah username sudah terdaftar
                database.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Username sudah terdaftar!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Tentukan role pengguna (admin jika pertama kali)
                            database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    String role = snapshot.exists() ? "user" : "admin";

                                    // Simpan Data Pengguna di Database
                                    DatabaseReference userRef = database.child("users").child(username);
                                    userRef.child("username").setValue(username);
                                    userRef.child("password").setValue(password);
                                    userRef.child("name").setValue(name);
                                    userRef.child("phone").setValue(phone);
                                    userRef.child("address").setValue(address);
                                    userRef.child("department").setValue(department);
                                    userRef.child("role").setValue(role);

                                    Toast.makeText(getApplicationContext(), "Registrasi Berhasil sebagai " + role, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Tombol Batal
        buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}