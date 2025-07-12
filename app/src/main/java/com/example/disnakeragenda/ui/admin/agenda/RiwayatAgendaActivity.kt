package com.example.disnakeragenda.ui.admin.agenda

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.model.AgendaMediasi
import com.example.disnakeragenda.ui.admin.agenda.RiwayatAgendaAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatAgendaActivity : AppCompatActivity() {

    private var idAdmin: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var agendaAdapter: RiwayatAgendaAdapter
    private var agendaList: List<AgendaMediasi> = listOf()

    // ActivityResultLauncher untuk menangkap hasil dari aktivitas lain
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                refreshData()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat_agenda_admin)
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
        fetchRiwayatAdmin()
    }

    private fun fetchRiwayatAdmin() {
        RetrofitClient.instance.getAllAgenda()
            .enqueue(object : Callback<ApiResponse<List<AgendaMediasi>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<AgendaMediasi>>>,
                    response: Response<ApiResponse<List<AgendaMediasi>>>
                ) {
                    when {
                        response.isSuccessful -> {
                            response.body()?.let { apiResponse ->
                                if (apiResponse.status) {
                                    apiResponse.data?.let { data ->
                                        Log.d(
                                            "Riwayat Admin",
                                            "Data berhasil diterima, jumlah item: ${data.size}"
                                        )
                                        agendaList = data
                                        agendaAdapter.updateData(agendaList)
                                    } ?: run {
                                        Log.e("Riwayat Admin", "Data null dalam response")
                                        agendaList = listOf()
                                        agendaAdapter.updateData(agendaList)
                                    }
                                } else {
                                    Log.e(
                                        "Riwayat Admin",
                                        "Status false: ${apiResponse.message}"
                                    )
                                    showErrorMessage(apiResponse.message ?: "Gagal memuat data")
                                }
                            } ?: run {
                                Log.e("Riwayat Admin", "Response body null")
                                showErrorMessage("Response tidak valid dari server")
                            }
                        }

                        else -> {
                            val errorBody = response.errorBody()?.string() ?: "Unknown error"
                            Log.e(
                                "Riwayat Admin",
                                "Error response: ${response.code()} - $errorBody"
                            )
                            showErrorMessage("Error ${response.code()}: $errorBody")
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<AgendaMediasi>>>, t: Throwable) {
                    Log.e("Riwayat Admin", "Network error: ${t.localizedMessage}", t)
                    showErrorMessage("Gagal terhubung ke server: ${t.localizedMessage}")
                }
            })
    }
    
    private fun refreshData() {
        fetchRiwayatAdmin()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}