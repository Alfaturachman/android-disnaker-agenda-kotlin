package com.example.disnakeragenda.ui.pelapor.tambah

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.model.TambahPelapor
import com.example.disnakeragenda.ui.pelapor.RiwayatPelaporActivity
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TambahPelaporActivity : AppCompatActivity() {

    private var idPelapor: Int = -1
    private var namaPelapor: String = "Tidak Ada"
    private lateinit var etNamaPihak1: TextInputEditText
    private lateinit var etNamaPihak2: TextInputEditText
    private lateinit var etTanggalMediasi: TextInputEditText
    private lateinit var etWaktuMediasi: TextInputEditText
    private lateinit var etTempatMediasi: TextInputEditText
    private lateinit var etJenisKasus: TextInputEditText
    private lateinit var etDeskripsiKasus: TextInputEditText
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private lateinit var buttonSimpan: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pelapor)
        supportActionBar?.hide()

        // Set status bar color dan mode light
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi view
        etNamaPihak1 = findViewById(R.id.etNamaPihak1)
        etNamaPihak2 = findViewById(R.id.etNamaPihak2)
        etTanggalMediasi = findViewById(R.id.etTanggalMediasi)
        etWaktuMediasi = findViewById(R.id.etWaktuMediasi)
        etTempatMediasi = findViewById(R.id.etTempatMediasi)
        etJenisKasus = findViewById(R.id.etJenisKasus)
        etDeskripsiKasus = findViewById(R.id.etDeskripsiKasus)
        buttonSimpan = findViewById(R.id.buttonSimpan)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // ID dari SharedPreferences
        namaPelapor = getUserNamaFromSharedPreferences().toString()
        idPelapor = getUserIdFromSharedPreferences()

        // Mencegah user mengetik langsung, hanya bisa memilih dari dialog
        etTanggalMediasi.isFocusable = false
        etTanggalMediasi.isClickable = true

        etWaktuMediasi.isFocusable = false
        etWaktuMediasi.isClickable = true

        etNamaPihak1.setText(namaPelapor)

        etTanggalMediasi.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalMediasi.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        etWaktuMediasi.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                etWaktuMediasi.setText(selectedTime)
            }, hour, minute, true)

            timePicker.show()
        }

        buttonSimpan.setOnClickListener {
            simpanMediasi()
        }
    }

    private fun simpanMediasi() {
        val namaPihak1 = etNamaPihak1.text.toString().trim()
        val namaPihak2 = etNamaPihak2.text.toString().trim()
        val tempatMediasi = etTempatMediasi.text.toString().trim()
        val jenisKasus = etJenisKasus.text.toString().trim()
        val deskripsiKasus = etDeskripsiKasus.text.toString().trim()

        // Validasi input
        if (namaPihak1.isEmpty() || namaPihak2.isEmpty() ||
            selectedDate.isEmpty() || selectedTime.isEmpty() ||
            tempatMediasi.isEmpty() || jenisKasus.isEmpty() || deskripsiKasus.isEmpty()
        ) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = LocalDate.parse(selectedDate, inputFormatter)
        val formattedDate = parsedDate.format(outputFormatter)

        // Konversi waktu dari "HH:mm" ke "HH:mm:ss"
        val formattedTime = if (selectedTime.length == 5) "$selectedTime:00" else selectedTime

        // Data request untuk dikirim ke API
        val request = TambahPelapor(
            id_pelapor = idPelapor,
            nama_pihak_satu = namaPihak1,
            nama_pihak_dua = namaPihak2,
            tgl_mediasi = formattedDate,
            waktu_mediasi = formattedTime,
            tempat = tempatMediasi,
            jenis_kasus = jenisKasus,
            deskripsi_kasus = deskripsiKasus
        )

        Log.d("TambahPelaporActivity", "Request yang dikirim: $request")

        RetrofitClient.instance.tambahPelapor(request).enqueue(object : Callback<ApiResponse<TambahPelapor>> {
            override fun onResponse(call: Call<ApiResponse<TambahPelapor>>, response: Response<ApiResponse<TambahPelapor>>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    Log.d("TambahPelaporActivity", "Response berhasil diterima: $result")

                    if (result != null && result.status) {
                        Log.d("TambahPelaporActivity", "Mediasi berhasil disimpan!")

                        Toast.makeText(this@TambahPelaporActivity, "Mediasi berhasil disimpan!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@TambahPelaporActivity, RiwayatPelaporActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("TambahPelaporActivity", "Gagal menyimpan mediasi. Error: $errorBody")
                        Toast.makeText(this@TambahPelaporActivity, "Gagal menyimpan mediasi!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("TambahPelaporActivity", "Response error: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@TambahPelaporActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<TambahPelapor>>, t: Throwable) {
                Log.e("TambahPelaporActivity", "Gagal menghubungi server: ${t.message}", t)
                Toast.makeText(this@TambahPelaporActivity, "Gagal menghubungi server!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun getUserNamaFromSharedPreferences(): String? {
        val sharedPreferences = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("nama", "0")
    }
}