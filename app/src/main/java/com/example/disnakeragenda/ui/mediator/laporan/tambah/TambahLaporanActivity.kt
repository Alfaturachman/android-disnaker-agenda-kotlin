package com.example.disnakeragenda.ui.mediator.laporan.tambah

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.model.AgendaLaporan
import com.example.disnakeragenda.model.LaporanRequest
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahLaporanActivity : AppCompatActivity() {

    private var idUserDetail: Int = -1
    private lateinit var spinnerAgendaMediasi: Spinner
    private lateinit var spinnerStatusLaporan: Spinner
    private lateinit var agendaAdapter: AgendaSpinnerAdapter
    private var agendaList: List<AgendaLaporan> = emptyList()
    private lateinit var etNamaPihak1: TextInputEditText
    private lateinit var etNamaPihak2: TextInputEditText
    private lateinit var etTanggalMediasi: TextInputEditText
    private lateinit var etWaktuMediasi: TextInputEditText
    private lateinit var etTempatMediasi: TextInputEditText
    private lateinit var etJenisKasus: TextInputEditText
    private lateinit var etDeskripsiKasus: TextInputEditText
    private lateinit var etTanggalPenutupan: TextInputEditText
    private lateinit var etHasilMediasi: TextInputEditText
    private var selectedDate: String = ""
    private lateinit var buttonSimpan: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_laporan)
        supportActionBar!!.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        idUserDetail = getuserIdDetailFromSharedPreferences()

        etNamaPihak1 = findViewById(R.id.etNamaPihak1)
        etNamaPihak2 = findViewById(R.id.etNamaPihak2)
        etTanggalMediasi = findViewById(R.id.etTanggalMediasi)
        etWaktuMediasi = findViewById(R.id.etWaktuMediasi)
        etTempatMediasi = findViewById(R.id.etTempatMediasi)
        etJenisKasus = findViewById(R.id.etJenisKasus)
        etDeskripsiKasus = findViewById(R.id.etDeskripsiKasus)
        etHasilMediasi = findViewById(R.id.etHasilMediasi)
        buttonSimpan = findViewById(R.id.buttonSimpan)

        // Tanggal Penutupan
        etTanggalPenutupan = findViewById(R.id.etTanggalPenutupan)
        etTanggalPenutupan.isFocusable = false
        etTanggalPenutupan.isClickable = true
        etTanggalPenutupan.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPenutupan.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        // Inisialisasi Spinner
        spinnerAgendaMediasi = findViewById(R.id.spinnerAgendaMediasi)
        spinnerStatusLaporan = findViewById(R.id.spinnerStatusLaporan)

        // Setup Spinner
        setupSpinner()
        fetchAgendaMediasi(idUserDetail)
        setupStatusSpinner()

        // Button Simpan
        buttonSimpan.setOnClickListener {
            val selectedAgenda = spinnerAgendaMediasi.selectedItem as? AgendaLaporan
            val idAgendaMediasi = selectedAgenda?.id
            val tanggalPenutupan = etTanggalPenutupan.text.toString().trim()
            val statusLaporan = spinnerStatusLaporan.selectedItem.toString()
            val hasilMediasi = etHasilMediasi.text.toString().trim()

            // Validasi input sebelum menyimpan
            when {
                idAgendaMediasi == null -> {
                    Toast.makeText(this, "Silakan pilih agenda mediasi terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                tanggalPenutupan.isEmpty() -> {
                    Toast.makeText(this, "Silakan isi tanggal penutupan!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                statusLaporan == "Pilih Status Laporan" -> {
                    Toast.makeText(this, "Silakan pilih status laporan!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                hasilMediasi.isEmpty() -> {
                    Toast.makeText(this, "Silakan isi hasil mediasi!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Buat request body
            val laporanRequest = LaporanRequest(
                idAgendaMediasi = idAgendaMediasi,
                tanggalPenutupan = tanggalPenutupan,
                statusLaporan = statusLaporan,
                hasilMediasi = hasilMediasi
            )

            // Kirim data ke server
            RetrofitClient.instance.simpanLaporan(laporanRequest).enqueue(object : Callback<ApiResponse<Unit>> {
                override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@TambahLaporanActivity, "Laporan berhasil disimpan", Toast.LENGTH_SHORT).show()
                        finish() // Tutup activity setelah berhasil menyimpan
                    } else {
                        Toast.makeText(this@TambahLaporanActivity, "Gagal menyimpan laporan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                    Toast.makeText(this@TambahLaporanActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                }
            })

            // Jika validasi lolos, lanjutkan menyimpan data
            Log.d("TambahLaporanActivity", "ID Agenda Mediasi: $idAgendaMediasi")
            Log.d("TambahLaporanActivity", "Tanggal Penutupan: $tanggalPenutupan")
            Log.d("TambahLaporanActivity", "Status Laporan: $statusLaporan")
            Log.d("TambahLaporanActivity", "Hasil Mediasi: $hasilMediasi")

            Toast.makeText(this, "Data berhasil disimpan ke log", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAgendaMediasi(userId: Int) {
        val requestBody = hashMapOf("id_mediator" to userId)

        RetrofitClient.instance.getAgendaMediasi(requestBody).enqueue(object : Callback<ApiResponse<List<AgendaLaporan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<AgendaLaporan>>>,
                response: Response<ApiResponse<List<AgendaLaporan>>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    agendaList = response.body()?.data ?: emptyList()
                    val defaultAgenda = AgendaLaporan(
                        id = -1,
                        id_mediator = null,
                        id_pelapor = null,
                        nomor_mediasi = null,
                        nama_pihak_satu = null,
                        nama_pihak_dua = null,
                        nama_kasus = "Pilih Agenda",
                        tgl_mediasi = null,
                        waktu_mediasi = null,
                        status = null,
                        tempat = null,
                        jenis_kasus = null,
                        deskripsi_kasus = null,
                        id_laporan = null,
                        tgl_penutupan = null,
                        hasil_mediasi = null
                    )

                    agendaAdapter.clear()
                    agendaAdapter.addAll(listOf(defaultAgenda) + agendaList)
                } else {
                    Toast.makeText(this@TambahLaporanActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<AgendaLaporan>>>, t: Throwable) {
                Toast.makeText(this@TambahLaporanActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinner() {
        val defaultAgenda = AgendaLaporan(
            id = -1,
            id_mediator = null,
            id_pelapor = null,
            nomor_mediasi = null,
            nama_pihak_satu = null,
            nama_pihak_dua = null,
            nama_kasus = null,
            tgl_mediasi = null,
            waktu_mediasi = null,
            status = null,
            tempat = null,
            jenis_kasus = null,
            deskripsi_kasus = null,
            id_laporan = null,
            tgl_penutupan = null,
            hasil_mediasi = null
        )
        val updatedAgendaList = mutableListOf(defaultAgenda) + agendaList

        // Buat adapter dan set ke Spinner
        agendaAdapter = AgendaSpinnerAdapter(this, updatedAgendaList)
        spinnerAgendaMediasi.adapter = agendaAdapter

        // Set listener untuk menangani item yang dipilih
        spinnerAgendaMediasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedAgenda = agendaAdapter.getItem(position) as AgendaLaporan

                if (selectedAgenda.id != -1) {
                    // Isi EditText sesuai dengan data yang dipilih dari Spinner
                    etNamaPihak1.setText(selectedAgenda.nama_pihak_satu)
                    etNamaPihak2.setText(selectedAgenda.nama_pihak_dua)
                    etTanggalMediasi.setText(selectedAgenda.tgl_mediasi)
                    etWaktuMediasi.setText(selectedAgenda.waktu_mediasi)
                    etTempatMediasi.setText(selectedAgenda.tempat)
                    etJenisKasus.setText(selectedAgenda.jenis_kasus)
                    etDeskripsiKasus.setText(selectedAgenda.deskripsi_kasus)

                    Log.d("TambahLaporanActivity", "Agenda Terpilih: ${selectedAgenda.nama_kasus}")
                    Toast.makeText(this@TambahLaporanActivity, "Agenda mediasi berhasil dipilih", Toast.LENGTH_SHORT).show()
                } else {
                    // Jika item default dipilih, kosongkan EditText
                    etNamaPihak1.text?.clear()
                    etNamaPihak2.text?.clear()
                    etTanggalMediasi.text?.clear()
                    etWaktuMediasi.text?.clear()
                    etTempatMediasi.text?.clear()
                    etJenisKasus.text?.clear()
                    etDeskripsiKasus.text?.clear()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }
    }

    private fun setupStatusSpinner() {
        val statusList = listOf("Pilih Status Laporan", "selesai", "gagal", "dilanjut ke Pengadilan")

        // Buat adapter untuk spinner status
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapter ke spinner
        spinnerStatusLaporan.adapter = statusAdapter

        // Set nilai default ke "Pilih Status Laporan"
        spinnerStatusLaporan.setSelection(0)

        // Set listener jika ingin menangani event pemilihan status
        spinnerStatusLaporan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = statusList[position]

                // Hindari tindakan jika user memilih "Pilih Status Laporan"
                if (selectedStatus == "Pilih Status Laporan") return

                Toast.makeText(this@TambahLaporanActivity, "Status dipilih: $selectedStatus", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }
    }

    private fun getuserIdDetailFromSharedPreferences(): Int {
        val sharedPreferences = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user_detail", -1)
    }
}
