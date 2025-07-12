package com.example.disnakeragenda.ui.admin.agenda.detail

import android.annotation.SuppressLint
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
import com.example.disnakeragenda.model.UpdateAgendaAdmin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAgendaActivity : AppCompatActivity() {

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
    private lateinit var cardViewEditAgenda: CardView
    private lateinit var layoutLaporan: LinearLayout
    private lateinit var layoutFilePdf: LinearLayout
    private lateinit var ivPdfIcon: ImageView
    private lateinit var mediatorAdapter: AgendaSpinnerAdapter
    private val mediatorList = mutableListOf<Mediator>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_agenda_admin)
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
        idAgenda = intent.getIntExtra("id_mediasi", 0)
        idMediator = intent.getIntExtra("id_mediator", 0)
        val nomorMediasi = intent.getStringExtra("nomor_mediasi")
        val namaPihak1 = intent.getStringExtra("nama_pihak_satu")
        val namaPihak2 = intent.getStringExtra("nama_pihak_dua")
        val tanggalMediasi = intent.getStringExtra("tanggal_mediasi")
        val waktuMediasi = intent.getStringExtra("waktu_mediasi")
        val tempatMediasi = intent.getStringExtra("tempat_mediasi") ?: "-"
        val jenisKasus = intent.getStringExtra("jenis_kasus")
        val deskripsiKasus = intent.getStringExtra("deskripsi_kasus")
        val namaFilePdf = intent.getStringExtra("file_pdf")

        val statusPelaporan = intent.getStringExtra("status").toString()
        Log.d("TAG", "onCreate: statusPelaporan = $statusPelaporan")

        if (statusPelaporan.equals("Disetujui", ignoreCase = true) ||
            statusPelaporan.equals("Ditolak", ignoreCase = true)) {
            Log.d("TAG", "Status is 'Disetujui' or 'Selesai'. Hiding cardViewEditAgenda.")
            cardViewEditAgenda.visibility = View.GONE
        } else {
            Log.d("TAG", "Status is neither 'Disetujui' nor 'Selesai'. Showing cardViewEditAgenda.")
            cardViewEditAgenda.visibility = View.VISIBLE
        }

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
        // Tambahkan opsi default di awal list
        val defaultMediator = Mediator(
            id_mediator = -1, nama = "- Pilih Mediator -",
            id_user = 0, telp = "", nip = "", bidang = "", alamat = ""
        )
        val updatedMediatorList = mutableListOf(defaultMediator) + mediatorList

        mediatorAdapter = AgendaSpinnerAdapter(this, updatedMediatorList)
    }

    @SuppressLint("MissingInflatedId")
    private fun showEditAgendaDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_agenda_admin, null)
        val spinnerEditAgenda = dialogView.findViewById<Spinner>(R.id.spinnerEditAgenda)


        // === Spinner Edit Agenda ===
        spinnerEditAgenda.adapter = mediatorAdapter
        val defaultPosition = mediatorList.indexOfFirst { it.id_mediator == idMediator }
        spinnerEditAgenda.setSelection(if (defaultPosition != -1) defaultPosition + 1 else 0)

        // === AlertDialog ===
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Agenda")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedPosition = spinnerEditAgenda.selectedItemPosition

                val selectedMediator = mediatorAdapter.getItem(selectedPosition) as Mediator
                handleEditAgenda(
                    mediatorId = selectedMediator.id_mediator,
                    mediatorName = selectedMediator.nama,
                    idAgenda = idAgenda,
                )

                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun handleEditAgenda(
        mediatorId: Int,
        mediatorName: String,
        idAgenda: Int,
    ) {
        Log.d("EditAgenda", "Mediator ID: $mediatorId")
        Log.d("EditAgenda", "Mediator Name: $mediatorName")
        Log.d("EditAgenda", "Agenda ID: $idAgenda")

        // Buat objek UpdateMediator
        val requestBody = UpdateAgendaAdmin(
            id = idAgenda,
            id_mediator = mediatorId,
        )

        Log.d("DetailAgendaActivity", "Request yang dikirim: $requestBody")

        // Panggil API menggunakan Retrofit
        RetrofitClient.instance.updateAgendaAdmin(requestBody).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                Log.d("DetailAgendaActivity", "onResponse() called")

                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("DetailAgendaActivity", "Response berhasil diterima: $result")

                    if (result != null && result.status) {
                        Log.d("DetailAgendaActivity", "Update mediator berhasil")
                        Toast.makeText(this@DetailAgendaActivity, "Agenda berhasil diupdate!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("DetailAgendaActivity", "Gagal mengupdate agenda. Response status false.")
                        Log.e("DetailAgendaActivity", "Isi errorBody (server): $errorBody")
                        Toast.makeText(this@DetailAgendaActivity, "Gagal mengupdate agenda!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("DetailAgendaActivity", "Response error: ${response.code()} - ${response.message()}")
                    val rawBody = response.errorBody()?.string()
                    Log.e("DetailAgendaActivity", "Raw error body: $rawBody")
                    Toast.makeText(this@DetailAgendaActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                Log.e("DetailAgendaActivity", "onFailure() called")
                Log.e("DetailAgendaActivity", "Request failed: ${t.message}", t)
                Toast.makeText(this@DetailAgendaActivity, "Gagal menghubungi server!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}