package com.example.disnakeragenda.ui.mediator.laporan.tambah

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.example.disnakeragenda.model.AgendaLaporan
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class TambahLaporanActivity : AppCompatActivity() {

    private lateinit var spinnerAgendaMediasi: Spinner
    private lateinit var spinnerStatusLaporan: Spinner
    private lateinit var agendaAdapter: AgendaSpinnerAdapter
    private val agendaList = listOf(
        AgendaLaporan(
            id = 1,
            id_mediator = 1,
            id_pelapor = 101,
            nomor_mediasi = 12345,
            nama_pihak_satu = "John Doe",
            nama_pihak_dua = "Jane Smith",
            nama_kasus = "Sengketa Lahan",
            tgl_mediasi = "2025-03-10",
            waktu_mediasi = "10:00",
            status = "Terjadwal",
            tempat = "Ruang Mediasi A",
            jenis_kasus = "Perdata",
            deskripsi_kasus = "Perselisihan kepemilikan tanah antara dua pihak.",
            id_laporan = 201,
            tgl_penutupan = null,
            hasil_mediasi = null
        ),
        AgendaLaporan(
            id = 2,
            id_mediator = 2,
            id_pelapor = 102,
            nomor_mediasi = 12346,
            nama_pihak_satu = "Michael Johnson",
            nama_pihak_dua = "Emily Davis",
            nama_kasus = "Sengketa Kontrak Kerja",
            tgl_mediasi = "2025-03-12",
            waktu_mediasi = "13:30",
            status = "Menunggu",
            tempat = "Ruang Mediasi B",
            jenis_kasus = "Ketenagakerjaan",
            deskripsi_kasus = "Perselisihan kontrak kerja antara karyawan dan perusahaan.",
            id_laporan = 202,
            tgl_penutupan = null,
            hasil_mediasi = null
        )
    )
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
        etTanggalMediasi.isFocusable = false
        etTanggalMediasi.isClickable = true
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
        setupStatusSpinner()

        // Button Simpan
        buttonSimpan.setOnClickListener {
            val selectedAgenda = spinnerAgendaMediasi.selectedItem as? AgendaLaporan
            val idLaporan = selectedAgenda?.id_laporan
            val tanggalPenutupan = etTanggalPenutupan.text.toString().trim()
            val statusLaporan = spinnerStatusLaporan.selectedItem.toString()
            val hasilMediasi = etHasilMediasi.text.toString().trim()

            // Validasi input sebelum menyimpan
            when {
                idLaporan == null -> {
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

            // Jika validasi lolos, lanjutkan menyimpan data
            Log.d("TambahLaporanActivity", "ID Laporan: $idLaporan")
            Log.d("TambahLaporanActivity", "Tanggal Penutupan: $tanggalPenutupan")
            Log.d("TambahLaporanActivity", "Status Laporan: $statusLaporan")
            Log.d("TambahLaporanActivity", "Hasil Mediasi: $hasilMediasi")

            Toast.makeText(this, "Data berhasil disimpan ke log", Toast.LENGTH_SHORT).show()
        }
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
        val statusList = listOf("Pilih Status Laporan", "Selesai", "Gagal", "Dilanjut ke Pengadilan")

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
}
