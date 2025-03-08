package com.example.disnakeragenda.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.databinding.FragmentHomeBinding
import com.example.disnakeragenda.model.PelaporTotalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var idUser: Int = -1
    private var userNama: String = "Tidak ada"
    private lateinit var tvNama: TextView
    private lateinit var tvDataDiproses: TextView
    private lateinit var tvDataDisetujui: TextView
    private lateinit var tvDataDitolak: TextView

    private lateinit var chartData: BarChart
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tvNama = binding?.tvNama!!
        tvDataDiproses = binding?.tvDataDiproses!!
        tvDataDisetujui = binding?.tvDataDisetujui!!
        tvDataDitolak = binding?.tvDataDitolak!!

        idUser = getuserIdFromSharedPreferences()
        userNama = getNamaFromSharedPreferences().toString()
        tvNama.text = "Halo, $userNama"

        // Level dari SharedPreferences
        val level = getLevelFromSharedPreferences()
        when (level) {
            "pelapor" -> {
                fetchPelaporTotalData(idUser)
                chartStatsData()
            }
            "mediator" -> {
                chartStatsData()
            }
            else -> {
                Toast.makeText(requireContext(), "Level pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        // BarChart binding
        chartData = binding?.chartData!!
        chartStatsData()

        return root
    }

    private fun chartStatsData() {
        // Data statis untuk stok per bulan
        val stokBelumDiambil = floatArrayOf(10f, 20f, 15f, 25f, 30f, 18f, 12f, 22f, 17f, 19f, 21f, 16f)
        val stokSudahDiambil = floatArrayOf(5f, 10f, 7f, 12f, 15f, 9f, 6f, 11f, 8f, 10f, 9f, 7f)

        // Buat data untuk BarChart
        val entriesBelumDiambil = mutableListOf<BarEntry>()
        val entriesSudahDiambil = mutableListOf<BarEntry>()

        for (i in 0 until 12) {
            entriesBelumDiambil.add(BarEntry(i.toFloat(), stokBelumDiambil[i]))
            entriesSudahDiambil.add(BarEntry(i.toFloat(), stokSudahDiambil[i]))
        }

        // Set data set untuk BarChart
        val dataSetBelumDiambil = BarDataSet(entriesBelumDiambil, "Stok Belum Diambil").apply {
            color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_warning)
            valueTextColor = Color.BLACK
        }

        val dataSetSudahDiambil = BarDataSet(entriesSudahDiambil, "Stok Sudah Diambil").apply {
            color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_success)
            valueTextColor = Color.BLACK
        }

        // Buat BarData untuk chart
        val barData = BarData(dataSetBelumDiambil, dataSetSudahDiambil)
        barData.barWidth = 0.5f // Lebar bar

        // Set data ke chart
        binding?.chartData?.data = barData

        // Konfigurasi sumbu X
        val bulanLabels = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
        )
        binding?.chartData?.xAxis?.apply {
            valueFormatter = IndexAxisValueFormatter(bulanLabels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
        }

        // Konfigurasi sumbu Y
        binding?.chartData?.axisLeft?.apply {
            axisMinimum = 0f
            granularity = 1f
        }
        binding?.chartData?.axisRight?.isEnabled = false

        // Konfigurasi legenda
        binding?.chartData?.legend?.apply {
            isEnabled = true
            textColor = Color.BLACK
        }

        // Menampilkan chart
        binding?.chartData?.invalidate()
    }

    private fun fetchPelaporTotalData(userId: Int) {
        val requestBody = hashMapOf("id_pelapor" to userId)

        RetrofitClient.instance.TotalStokPelapor(requestBody)
            .enqueue(object : Callback<ApiResponse<PelaporTotalData>> {
                override fun onResponse(
                    call: Call<ApiResponse<PelaporTotalData>>,
                    response: Response<ApiResponse<PelaporTotalData>>
                ) {
                    Log.d("API_RESPONSE", "Response Code: ${response.code()}")

                    if (response.isSuccessful) {
                        val pelaporResponse = response.body()
                        Log.d("API_RESPONSE", "Response Body: $pelaporResponse")

                        if (pelaporResponse?.status == true) {
                            pelaporResponse.data?.let { pelapor ->
                                tvDataDiproses.text = pelapor.diproses.toInt().toString()
                                tvDataDisetujui.text = pelapor.disetujui.toInt().toString()
                                tvDataDitolak.text = pelapor.ditolak.toInt().toString()
                                Log.d("API_SUCCESS", "Data berhasil diperoleh: $pelapor")
                            }
                        } else {
                            Log.e("API_ERROR", "Pesan error dari server: ${pelaporResponse?.message}")
                            Toast.makeText(requireContext(), pelaporResponse?.message ?: "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Response Error Body: $errorBody")
                        Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<PelaporTotalData>>, t: Throwable) {
                    Log.e("API_FAILURE", "Failure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getuserIdFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun getuserIdDetailFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user_detail", -1)
    }

    private fun getNamaFromSharedPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("nama", "0")
    }

    private fun getLevelFromSharedPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("level", "0")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}