package com.example.disnakeragenda.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.disnakeragenda.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}