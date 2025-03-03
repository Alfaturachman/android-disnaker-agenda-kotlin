package com.example.disnakeragenda.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.disnakeragenda.databinding.FragmentDashboardBinding
import com.example.disnakeragenda.ui.pelapor.detail.DetailPelaporActivity
import com.example.disnakeragenda.ui.pelapor.tambah.TambahPelaporActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Menambahkan event click pada CardView
        binding.cardViewRiwayatLaporan.setOnClickListener {
            val intent = Intent(requireContext(), DetailPelaporActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewTambahLaporan.setOnClickListener {
            val intent = Intent(requireContext(), TambahPelaporActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}