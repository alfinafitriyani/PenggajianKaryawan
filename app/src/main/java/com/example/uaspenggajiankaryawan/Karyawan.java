package com.example.uaspenggajiankaryawan;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Karyawan {
    private String username;
    private String name;
    private Object phone; // Bisa berupa String atau Long
    private String address;
    private String department;
    private String role;
    private String password;

    public Karyawan() {}

    public Karyawan(String username, String name, Object phone, String address, String department, String role, String password) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.department = department;
        this.role = role;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() {
        return phone != null ? phone.toString() : "Tidak tersedia";
    }
    public void setPhone(Object phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
