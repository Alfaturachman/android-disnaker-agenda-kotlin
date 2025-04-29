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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.databinding.FragmentHomeBinding
import com.example.disnakeragenda.model.StatsTotalData
import com.example.disnakeragenda.model.TotalRekapData
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var idUser: Int = -1
    private var idUserDetail: Int = -1
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
        idUserDetail = getuserIdDetailFromSharedPreferences()
        userNama = getNamaFromSharedPreferences().toString()
        tvNama.text = "Halo, $userNama"

        // Level dari SharedPreferences
        val level = getLevelFromSharedPreferences()
        when (level) {
            "pelapor" -> {
                fetchPelaporTotalData(idUser)
                pelaporStatsData(idUser)
            }
            "mediator" -> {
                fetchMediatorTotalData(idUserDetail)
                mediatorStatsData(idUserDetail)
            }
            else -> {
                Toast.makeText(requireContext(), "Level pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        // Ambil waktu hari ini
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy", Locale("id", "ID"))
        val bulanTahun = dateFormat.format(calendar.time)

        // Tampilkan di TextView
            binding.titleLaporanStats.text = "Statistik Agenda pada $bulanTahun"

        // BarChart binding
        chartData = binding?.chartData!!

        return root
    }

    private fun pelaporStatsData(userId: Int) {
        // Buat request body
        val requestBody = HashMap<String, Int>()
        requestBody["id_pelapor"] = userId

        // Panggil API
        val call = RetrofitClient.instance.StatsDataPelapor(requestBody)
        call.enqueue(object : Callback<ApiResponse<List<StatsTotalData>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<StatsTotalData>>>,
                response: Response<ApiResponse<List<StatsTotalData>>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    Log.d("API_RESPONSE", apiResponse.toString())

                    val stokDataList = apiResponse.data

                    // Struktur data
                    val dataDiproses = FloatArray(12) { 0f }
                    val dataDisetujui = FloatArray(12) { 0f }
                    val dataDitolak = FloatArray(12) { 0f }

                    // Spinner tahun
                    // val tahun = selectedYearFromDropdown

                    // Tahun sekarang
                    val calendar = Calendar.getInstance()
                    val tahun = calendar.get(Calendar.YEAR)

                    // Gabungkan data stok berdasarkan bulan untuk tahun yang diinginkan
                    for (stokData in stokDataList) {
                        if (stokData.tahun == tahun) { // Filter tahun
                            val bulanIndex = stokData.bulan - 1 // Konversi bulan (1-12) ke index (0-11)
                            if (bulanIndex in 0..11) {
                                dataDiproses[bulanIndex] += stokData.total_diproses.toFloat()
                                dataDisetujui[bulanIndex] += stokData.total_disetujui.toFloat()
                                dataDitolak[bulanIndex] += stokData.total_ditolak.toFloat()
                            }
                        }
                    }

                    // Buat data untuk BarChart
                    val entriesDiproses = mutableListOf<BarEntry>()
                    val entriesDisetujui = mutableListOf<BarEntry>()
                    val entriesDitolak = mutableListOf<BarEntry>()

                    for (i in 0 until 12) {
                        entriesDiproses.add(BarEntry(i.toFloat(), dataDiproses[i]))
                        entriesDisetujui.add(BarEntry(i.toFloat(), dataDisetujui[i]))
                        entriesDitolak.add(BarEntry(i.toFloat(), dataDitolak[i]))
                    }

                    // Set data set untuk BarChart
                    val dataSetDiproses = BarDataSet(entriesDiproses, "Diproses").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_warning)
                        valueTextColor = Color.BLACK
                    }

                    val dataSetDisetujui = BarDataSet(entriesDisetujui, "Disetujui").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_success)
                        valueTextColor = Color.BLACK
                    }

                    val dataSetDitolak = BarDataSet(entriesDitolak, "Ditolak").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_danger)
                        valueTextColor = Color.BLACK
                    }

                    val intValueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }

                    dataSetDiproses.valueFormatter = intValueFormatter
                    dataSetDisetujui.valueFormatter = intValueFormatter
                    dataSetDitolak.valueFormatter = intValueFormatter

                    // Buat BarData untuk chart
                    val barData = BarData(dataSetDiproses, dataSetDisetujui, dataSetDitolak)
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
                } else {
                    Log.e("API_ERROR", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<StatsTotalData>>>, t: Throwable) {
                Log.e("API_FAILURE", "Request gagal: ${t.message}", t)
            }
        })
    }

    private fun mediatorStatsData(userId: Int) {
        // Buat request body
        val requestBody = HashMap<String, Int>()
        requestBody["id_mediator"] = userId

        // Panggil API
        val call = RetrofitClient.instance.StatsDataMediator(requestBody)
        call.enqueue(object : Callback<ApiResponse<List<StatsTotalData>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<StatsTotalData>>>,
                response: Response<ApiResponse<List<StatsTotalData>>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    Log.d("API_RESPONSE", apiResponse.toString())

                    val stokDataList = apiResponse.data

                    // Struktur data
                    val dataDiproses = FloatArray(12) { 0f }
                    val dataDisetujui = FloatArray(12) { 0f }
                    val dataDitolak = FloatArray(12) { 0f }

                    // Spinner tahun
                    // val tahun = selectedYearFromDropdown

                    // Tahun sekarang
                    val calendar = Calendar.getInstance()
                    val tahun = calendar.get(Calendar.YEAR)

                    // Gabungkan data stok berdasarkan bulan untuk tahun yang diinginkan
                    for (stokData in stokDataList) {
                        if (stokData.tahun == tahun) { // Filter tahun
                            val bulanIndex = stokData.bulan - 1 // Konversi bulan (1-12) ke index (0-11)
                            if (bulanIndex in 0..11) {
                                dataDiproses[bulanIndex] += stokData.total_diproses.toFloat()
                                dataDisetujui[bulanIndex] += stokData.total_disetujui.toFloat()
                                dataDitolak[bulanIndex] += stokData.total_ditolak.toFloat()
                            }
                        }
                    }

                    // Buat data untuk BarChart
                    val entriesDiproses = mutableListOf<BarEntry>()
                    val entriesDisetujui = mutableListOf<BarEntry>()
                    val entriesDitolak = mutableListOf<BarEntry>()

                    for (i in 0 until 12) {
                        entriesDiproses.add(BarEntry(i.toFloat(), dataDiproses[i]))
                        entriesDisetujui.add(BarEntry(i.toFloat(), dataDisetujui[i]))
                        entriesDitolak.add(BarEntry(i.toFloat(), dataDitolak[i]))
                    }

                    // Set data set untuk BarChart
                    val dataSetDiproses = BarDataSet(entriesDiproses, "Diproses").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_warning)
                        valueTextColor = Color.BLACK
                    }

                    val dataSetDisetujui = BarDataSet(entriesDisetujui, "Disetujui").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_success)
                        valueTextColor = Color.BLACK
                    }

                    val dataSetDitolak = BarDataSet(entriesDitolak, "Ditolak").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.disnakeragenda.R.color.badge_danger)
                        valueTextColor = Color.BLACK
                    }

                    val intValueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }

                    dataSetDiproses.valueFormatter = intValueFormatter
                    dataSetDisetujui.valueFormatter = intValueFormatter
                    dataSetDitolak.valueFormatter = intValueFormatter

                    // Buat BarData untuk chart
                    val barData = BarData(dataSetDiproses, dataSetDisetujui, dataSetDitolak)
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
                } else {
                    Log.e("API_ERROR", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<StatsTotalData>>>, t: Throwable) {
                Log.e("API_FAILURE", "Request gagal: ${t.message}", t)
            }
        })
    }

    private fun fetchPelaporTotalData(userId: Int) {
        val requestBody = hashMapOf("id_pelapor" to userId)

        RetrofitClient.instance.TotalDataPelapor(requestBody)
            .enqueue(object : Callback<ApiResponse<TotalRekapData>> {
                override fun onResponse(
                    call: Call<ApiResponse<TotalRekapData>>,
                    response: Response<ApiResponse<TotalRekapData>>
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

                override fun onFailure(call: Call<ApiResponse<TotalRekapData>>, t: Throwable) {
                    Log.e("API_FAILURE", "Failure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchMediatorTotalData(userId: Int) {
        val requestBody = hashMapOf("id_mediator" to userId)

        RetrofitClient.instance.TotalDataMediator(requestBody)
            .enqueue(object : Callback<ApiResponse<TotalRekapData>> {
                override fun onResponse(
                    call: Call<ApiResponse<TotalRekapData>>,
                    response: Response<ApiResponse<TotalRekapData>>
                ) {
                    Log.d("API_RESPONSE", "Response Code: ${response.code()}")

                    if (response.isSuccessful) {
                        val mediatorResponse = response.body()
                        Log.d("API_RESPONSE", "Response Body: $mediatorResponse")

                        if (mediatorResponse?.status == true) {
                            mediatorResponse.data?.let { mediator ->
                                tvDataDiproses.text = mediator.diproses.toInt().toString()
                                tvDataDisetujui.text = mediator.disetujui.toInt().toString()
                                tvDataDitolak.text = mediator.ditolak.toInt().toString()
                                Log.d("API_SUCCESS", "Data berhasil diperoleh: $mediator")
                            }
                        } else {
                            Log.e("API_ERROR", "Pesan error dari server: ${mediatorResponse?.message}")
                            Toast.makeText(requireContext(), mediatorResponse?.message ?: "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Response Error Body: $errorBody")
                        Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<TotalRekapData>>, t: Throwable) {
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