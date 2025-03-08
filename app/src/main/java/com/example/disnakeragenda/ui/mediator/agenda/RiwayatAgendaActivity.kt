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

    private var idPelapor: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatAgendaAdapter

    // ActivityResultLauncher
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat_agenda)
        supportActionBar!!.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewRiwayatAgenda)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RiwayatAgendaAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchRiwayatPelaporan()
    }

    private fun fetchRiwayatPelaporan() {
        val call = RetrofitClient.instance.getAgenda()
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

    public fun refreshData() {
        fetchRiwayatPelaporan()
    }
}