package com.example.disnakeragenda.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.disnakeragenda.R

class DetailProfileActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_profile)
        supportActionBar?.hide()

        // Set status bar putih dengan ikon gelap
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Tombol kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener { finish() }

        // Ambil data dari SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val nama = sharedPreferences.getString("nama", "Nama tidak tersedia")
        val level = sharedPreferences.getString("level", "Level tidak tersedia")
        val email = sharedPreferences.getString("email", "Email tidak tersedia")
        val telp = sharedPreferences.getString("telp", "Telepon tidak tersedia")
        val alamat = sharedPreferences.getString("alamat", "Alamat tidak tersedia")

        // Tampilkan data di TextView
        findViewById<TextView>(R.id.tvNama).text = nama
        findViewById<TextView>(R.id.tvLevel).text = level
        findViewById<TextView>(R.id.tvEmail).text = email
        findViewById<TextView>(R.id.tvPhone).text = telp
        findViewById<TextView>(R.id.tvAddress).text = alamat
    }
}
