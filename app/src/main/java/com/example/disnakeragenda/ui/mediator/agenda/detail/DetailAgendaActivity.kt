package com.example.disnakeragenda.ui.mediator.agenda.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.example.disnakeragenda.ui.pelapor.RiwayatPelaporActivity
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAgendaActivity : AppCompatActivity() {

    private var idMediasi: Int = -1
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
    private lateinit var cardViewEditAgenda: CardView
    private lateinit var layoutLaporan: LinearLayout
    private lateinit var layoutFilePdf: LinearLayout
    private lateinit var ivPdfIcon: ImageView
    private lateinit var mediatorAdapter: MediatorSpinnerAdapter
    private val mediatorList = mutableListOf<Mediator>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_agenda)
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
        cardViewEditAgenda = findViewById(R.id.cardViewEditAgenda)
        layoutLaporan = findViewById(R.id.layoutLaporan)
        layoutFilePdf = findViewById(R.id.layoutFilePdf)
        ivPdfIcon = findViewById(R.id.ivPdfIcon)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        cardViewEditAgenda.setOnClickListener {
            showEditAgendaDialog()
        }

        layoutFilePdf.visibility = View.GONE

        // Ambil data dari intent
        val intent = intent
        idMediasi = intent.getIntExtra("id_mediasi", 0)
        val nomorMediasi = intent.getStringExtra("nomor_mediasi")
        val namaPihak1 = intent.getStringExtra("nama_pihak_satu")
        val namaPihak2 = intent.getStringExtra("nama_pihak_dua")
        val tanggalMediasi = intent.getStringExtra("tanggal_mediasi")
        val waktuMediasi = intent.getStringExtra("waktu_mediasi")
        val tempatMediasi = intent.getStringExtra("tempat_mediasi")
        val jenisKasus = intent.getStringExtra("jenis_kasus")
        val deskripsiKasus = intent.getStringExtra("deskripsi_kasus")
        val namaFilePdf = intent.getStringExtra("file_pdf")

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
        fetchDetailMediasi(idMediasi)
    }

    private fun fetchDetailMediasi(idMediasi: Int) {
        val requestBody = hashMapOf("id_mediasi" to idMediasi)

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

                                val statusPelaporan = intent.getStringExtra("status")
                                tvStatusPelaporan.text = statusPelaporan

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
                                this@DetailAgendaActivity,
                                mediasiResponse?.message ?: "Data tidak ditemukan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Response Error: $errorBody")
                        Toast.makeText(this@DetailAgendaActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<AgendaMediasi>>, t: Throwable) {
                    Log.e("API_ERROR", "Failure: ${t.message}", t)
                    Toast.makeText(this@DetailAgendaActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this@DetailAgendaActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupSpinner() {
        mediatorAdapter = MediatorSpinnerAdapter(this, mediatorList)
    }

    @SuppressLint("MissingInflatedId")
    private fun showEditAgendaDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_agenda, null)
        val spinnerEditAgenda = dialogView.findViewById<Spinner>(R.id.spinnerEditAgenda)

        // Set adapter untuk spinner
        spinnerEditAgenda.adapter = mediatorAdapter

        // Buat AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Agenda")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedMediatorId = mediatorAdapter.getMediatorId(spinnerEditAgenda.selectedItemPosition)
                val selectedMediatorName = mediatorList[spinnerEditAgenda.selectedItemPosition].nama
                handleEditAgenda(selectedMediatorId, selectedMediatorName, idMediasi)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

        // Tampilkan Dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun handleEditAgenda(mediatorId: Int, mediatorName: String, idMediasi: Int) {
        Log.d("DetailAgendaActivity", "Mediator dipilih: $mediatorName (ID: $mediatorId)")

        // Data request untuk dikirim ke API
        val request = UpdateMediator(
            id = idMediasi,
            id_mediator = mediatorId
        )

        Log.d("DetailAgendaActivity", "Request yang dikirim: $request")

        // Panggil API untuk update id_mediator
        RetrofitClient.instance.updateMediator(request).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    Log.d("DetailAgendaActivity", "Response berhasil diterima: $result")

                    if (result != null && result.status) {
                        Log.d("DetailAgendaActivity", "Mediator berhasil diupdate!")

                        Toast.makeText(this@DetailAgendaActivity, "Mediator berhasil diupdate!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("DetailAgendaActivity", "Mediator sudah dipilih sebelumnya. Error: $errorBody")
                        Toast.makeText(this@DetailAgendaActivity, "Gagal mengupdate mediator!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("DetailAgendaActivity", "Response error: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@DetailAgendaActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                Log.e("DetailAgendaActivity", "Gagal menghubungi server: ${t.message}", t)
                Toast.makeText(this@DetailAgendaActivity, "Gagal menghubungi server!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}