package com.example.disnakeragenda.ui.mediator.agenda

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.model.AgendaMediasi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatAgendaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var agendaAdapter: RiwayatAgendaAdapter
    private var agendaList: List<AgendaMediasi> = listOf()

    // ActivityResultLauncher untuk menangkap hasil dari aktivitas lain
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat_agenda)
        supportActionBar?.hide() // Hindari crash jika supportActionBar null

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Tombol kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener { finish() }

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRiwayatAgenda)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter kosong
        agendaAdapter = RiwayatAgendaAdapter(emptyList(), startForResult) {
            refreshData()
        }

        recyclerView.adapter = agendaAdapter

        // Ambil data dari API
        fetchRiwayatPelaporan()
    }

    private fun fetchRiwayatPelaporan() {
        RetrofitClient.instance.getAgenda().enqueue(object : Callback<ApiResponse<List<AgendaMediasi>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<AgendaMediasi>>>,
                response: Response<ApiResponse<List<AgendaMediasi>>>
            ) {
                Log.d("RiwayatPelapor", "Response diterima dengan kode: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        Log.d("RiwayatPelapor", "Data berhasil diterima: ${responseBody.data}")
                        agendaList = responseBody.data ?: listOf()

                        // Update adapter dengan data terbaru
                        agendaAdapter.updateData(agendaList)
                    } else {
                        Log.e("RiwayatPelapor", "Gagal mendapatkan data: ${responseBody?.message}")
                    }
                } else {
                    Log.e("RiwayatPelapor", "Request gagal dengan kode: ${response.code()}, pesan: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<AgendaMediasi>>>, t: Throwable) {
                Log.e("RiwayatPelapor", "Gagal menghubungi server: ${t.localizedMessage}", t)
            }
        })
    }

    private fun refreshData() {
        fetchRiwayatPelaporan()
    }
}
