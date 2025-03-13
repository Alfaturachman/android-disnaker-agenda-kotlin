package com.example.disnakeragenda.ui.mediator.laporan.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.helpers.DateHelper
import com.example.disnakeragenda.model.AgendaMediasi
import com.example.disnakeragenda.model.Mediator
import com.example.disnakeragenda.model.TambahPelapor
import com.example.disnakeragenda.model.UpdateMediator
import com.example.disnakeragenda.ui.mediator.agenda.RiwayatAgendaActivity
import com.example.disnakeragenda.ui.mediator.laporan.RiwayatLaporanActivity
import com.example.disnakeragenda.ui.mediator.laporan.tambah.TambahLaporanActivity
import com.example.disnakeragenda.ui.pelapor.RiwayatPelaporActivity
import com.example.disnakeragenda.ui.pelapor.tambah.TambahPelaporActivity
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailLaporanActivity : AppCompatActivity() {

    private var idAgenda: Int = -1
    private var idMediator: Int = -1
    private var statusPelaporan: String = "Tidak ada"
    private lateinit var tvNomorMediasi: TextView
    private lateinit var tvNamaPihak1: TextView
    private lateinit var tvNamaPihak2: TextView
    private lateinit var tvTanggalMediasi: TextView
    private lateinit var tvWaktuMediasi: TextView
    private lateinit var tvStatusPelaporan: TextView
    private lateinit var tvTempatMediasi: TextView
    private lateinit var tvJenisKasus: TextView
    private lateinit var tvDeskripsiKasus: TextView
    private lateinit var tvNamaFilePdf: TextView
    private lateinit var tvTanggalPenutupan: TextView
    private lateinit var tvHasilMediasi: TextView
    private lateinit var tvNamaMediator: TextView
    private lateinit var cardViewEditLaporan: CardView
    private lateinit var layoutLaporan: LinearLayout
    private lateinit var layoutFilePdf: LinearLayout
    private lateinit var ivPdfIcon: ImageView
    private lateinit var mediatorAdapter: LaporanSpinnerAdapter
    private val mediatorList = mutableListOf<Mediator>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_laporan)
        supportActionBar?.hide()

        // Set status bar color dan mode light
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi
        tvNomorMediasi = findViewById(R.id.tvNomorMediasi)
        tvNamaPihak1 = findViewById(R.id.tvNamaPihak1)
        tvNamaPihak2 = findViewById(R.id.tvNamaPihak2)
        tvTanggalMediasi = findViewById(R.id.tvTanggalMediasi)
        tvWaktuMediasi = findViewById(R.id.tvWaktuMediasi)
        tvStatusPelaporan = findViewById(R.id.tvStatusPelaporan)
        tvTempatMediasi = findViewById(R.id.tvTempatMediasi)
        tvJenisKasus = findViewById(R.id.tvJenisKasus)
        tvDeskripsiKasus = findViewById(R.id.tvDeskripsiKasus)
        tvNamaFilePdf = findViewById(R.id.tvNamaFilePdf)
        tvTanggalPenutupan = findViewById(R.id.tvTanggalPenutupan)
        tvHasilMediasi = findViewById(R.id.tvHasilMediasi)
        tvNamaMediator = findViewById(R.id.tvNamaMediator)
        cardViewEditLaporan = findViewById(R.id.cardViewEditLaporan)
        layoutLaporan = findViewById(R.id.layoutLaporan)
        layoutFilePdf = findViewById(R.id.layoutFilePdf)
        ivPdfIcon = findViewById(R.id.ivPdfIcon)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        cardViewEditLaporan.setOnClickListener {
            showEditAgendaDialog()
        }

        layoutFilePdf.visibility = View.GONE

        // Ambil data dari intent
        val intent = intent
        idAgenda = intent.getIntExtra("id_mediasi", 0)
        idMediator = intent.getIntExtra("id_mediator", 0)
        val nomorMediasi = intent.getIntExtra("nomor_mediasi", 0)
        val namaPihak1 = intent.getStringExtra("nama_pihak_satu")
        val namaPihak2 = intent.getStringExtra("nama_pihak_dua")
        val tanggalMediasi = intent.getStringExtra("tanggal_mediasi")
        val waktuMediasi = intent.getStringExtra("waktu_mediasi")
        val tempatMediasi = intent.getStringExtra("tempat_mediasi")
        val jenisKasus = intent.getStringExtra("jenis_kasus")
        val deskripsiKasus = intent.getStringExtra("deskripsi_kasus")
        val namaFilePdf = intent.getStringExtra("file_pdf")

        statusPelaporan = intent.getStringExtra("status").toString()
        tvStatusPelaporan.text = statusPelaporan

        tvNomorMediasi.text = "#$nomorMediasi"
        tvNamaPihak1.text = namaPihak1
        tvNamaPihak2.text = namaPihak2
        tvTanggalMediasi.text = DateHelper.formatDate(tanggalMediasi.toString())
        tvWaktuMediasi.text = DateHelper.formatDate(waktuMediasi.toString())
        tvTempatMediasi.text = tempatMediasi
        tvJenisKasus.text = jenisKasus
        tvDeskripsiKasus.text = deskripsiKasus
        tvNamaFilePdf.text = namaFilePdf

        fetchMediatorSpinner()
        fetchDetailMediasi(idAgenda)
    }

    private fun fetchDetailMediasi(idAgenda: Int) {
        val requestBody = hashMapOf("id_mediasi" to idAgenda)

        RetrofitClient.instance.getDetailPelaporanPelapor(requestBody)
            .enqueue(object : Callback<ApiResponse<AgendaMediasi>> {
                override fun onResponse(
                    call: Call<ApiResponse<AgendaMediasi>>,
                    response: Response<ApiResponse<AgendaMediasi>>
                ) {
                    Log.d("API_RESPONSE", "Response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val mediasiResponse = response.body()

                        if (mediasiResponse?.status == true) {
                            mediasiResponse.data?.let { mediasi ->
                                Log.d("API_RESPONSE", "Data berhasil diterima: $mediasi")

                                layoutLaporan.visibility = View.GONE
                                tvNamaMediator.text = mediasi.nama_mediator ?: "-"
                                cardViewEditLaporan.visibility = View.GONE

                                if (mediasi.id_laporan != null && mediasi.id_laporan != 0) {
                                    layoutLaporan.visibility = View.VISIBLE

                                    tvStatusPelaporan.text = mediasi.status_laporan
                                    tvTanggalPenutupan.text = mediasi.tgl_penutupan
                                    tvHasilMediasi.text = mediasi.hasil_mediasi
                                } else {
                                    Log.d("API_RESPONSE", "id_laporan null, tidak menampilkan data")
                                }
                            }
                        } else {
                            Log.e("API_ERROR", "Response gagal: ${mediasiResponse?.message}")
                            Toast.makeText(
                                this@DetailLaporanActivity,
                                mediasiResponse?.message ?: "Data tidak ditemukan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Response Error: $errorBody")
                        Toast.makeText(this@DetailLaporanActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<AgendaMediasi>>, t: Throwable) {
                    Log.e("API_ERROR", "Failure: ${t.message}", t)
                    Toast.makeText(this@DetailLaporanActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchMediatorSpinner() {
        RetrofitClient.instance.getMediator().enqueue(object : Callback<ApiResponse<List<Mediator>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Mediator>>>,
                response: Response<ApiResponse<List<Mediator>>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.status) {
                            // Pastikan apiResponse.data adalah List<Mediator>
                            mediatorList.apply {
                                clear()
                                addAll(apiResponse.data ?: emptyList())
                            }
                            setupSpinner()
                        } else {
                            showToast("Gagal mengambil data mediator: Response tidak valid")
                        }
                    } ?: showToast("Response body null")
                } else {
                    showToast("Gagal mengambil data mediator: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Mediator>>>, t: Throwable) {
                showToast("Error: ${t.localizedMessage}")
                Log.e("fetchMediatorData", "Request failed", t)
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this@DetailLaporanActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupSpinner() {
        // Tambahkan opsi default di awal list
        val defaultMediator = Mediator(
            id_mediator = -1, nama = "- Pilih Mediator -",
            id_user = 0, telp = "", nip = "", bidang = "", alamat = ""
        )
        val updatedMediatorList = mutableListOf(defaultMediator) + mediatorList

        mediatorAdapter = LaporanSpinnerAdapter(this, updatedMediatorList)
    }

    @SuppressLint("MissingInflatedId")
    private fun showEditAgendaDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_agenda, null)
        val spinnerEditAgenda = dialogView.findViewById<Spinner>(R.id.spinnerEditAgenda)
        val spinnerStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        // Set adapter untuk mediator
        spinnerEditAgenda.adapter = mediatorAdapter

        // Tentukan posisi default berdasarkan idMediator
        val defaultPosition = mediatorList.indexOfFirst { it.id_mediator == idMediator }
        spinnerEditAgenda.setSelection(if (defaultPosition != -1) defaultPosition + 1 else 0)

        // Tambahkan adapter untuk spinnerStatus
        val statusList = listOf("diproses", "ditolak", "disetujui")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusList)
        spinnerStatus.adapter = statusAdapter

        // Set status default jika ada
        val defaultStatusPosition = statusList.indexOf(statusPelaporan)
        spinnerStatus.setSelection(if (defaultStatusPosition != -1) defaultStatusPosition else 0)

        // Buat AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Agenda")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedPosition = spinnerEditAgenda.selectedItemPosition
                val selectedStatus = spinnerStatus.selectedItem.toString()

                // Pastikan pengguna memilih mediator yang valid
                if (selectedPosition == 0) {
                    Toast.makeText(this, "Silakan pilih mediator!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedMediator = mediatorAdapter.getItem(selectedPosition) as Mediator
                handleEditAgenda(selectedMediator.id_mediator, selectedMediator.nama, idAgenda, selectedStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun handleEditAgenda(mediatorId: Int, mediatorName: String, idAgenda: Int, selectedStatus: String) {
        Log.d("DetailLaporanActivity", "Mediator dipilih: $mediatorName (ID: $mediatorId) (Status: $selectedStatus)")

        val request = UpdateMediator(
            id = idAgenda,
            id_mediator = mediatorId,
            status = selectedStatus
        )

        Log.d("DetailLaporanActivity", "Request yang dikirim: $request")

        // Panggil API untuk update id_mediator
        RetrofitClient.instance.updateMediator(request).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    Log.d("DetailLaporanActivity", "Response berhasil diterima: $result")

                    if (result != null && result.status) {
                        Log.d("DetailLaporanActivity", "Mediator berhasil diupdate!")

                        Toast.makeText(this@DetailLaporanActivity, "Mediator berhasil diupdate!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("DetailLaporanActivity", "Mediator sudah dipilih sebelumnya. Error: $errorBody")
                        Toast.makeText(this@DetailLaporanActivity, "Gagal mengupdate mediator!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("DetailLaporanActivity", "Response error: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@DetailLaporanActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                Log.e("DetailLaporanActivity", "Gagal menghubungi server: ${t.message}", t)
                Toast.makeText(this@DetailLaporanActivity, "Gagal menghubungi server!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}