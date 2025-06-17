package com.example.disnakeragenda.ui.pelapor

import android.app.Activity
import android.content.Context
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
import com.example.disnakeragenda.ui.mediator.agenda.RiwayatAgendaAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatPelaporActivity : AppCompatActivity() {

    private var idPelapor: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatPelaporAdapter

    // ActivityResultLauncher
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat_pelapor)
        supportActionBar!!.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // ID dari SharedPreferences
        idPelapor = getUserIdFromSharedPreferences()
        Log.d("TAG", "onCreate: $idPelapor")

        recyclerView = findViewById(R.id.recyclerViewRiwayatPelapor)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter kosong
        adapter = RiwayatPelaporAdapter(emptyList(), startForResult) {
            refreshData()
        }

        recyclerView.adapter = adapter

        fetchRiwayatPelaporan(idPelapor)
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun fetchRiwayatPelaporan(idPelapor: Int) {
        val requestBody = hashMapOf("id_pelapor" to idPelapor)

        Log.d("RiwayatPelapor", "Mengirim request ke server dengan body: $requestBody")

        val call = RetrofitClient.instance.getRiwayatPelaporanPelapor(requestBody)
        call.enqueue(object : Callback<ApiResponse<List<AgendaMediasi>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<AgendaMediasi>>>,
                response: Response<ApiResponse<List<AgendaMediasi>>>
            ) {
                Log.d("RiwayatPelapor", "Response diterima dengan kode: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        Log.d("RiwayatPelapor", "Data berhasil diterima: ${responseBody.data}")
                        responseBody.data?.let {
                            adapter.updateData(it)
                        }
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
        fetchRiwayatPelaporan(idPelapor)
    }
}