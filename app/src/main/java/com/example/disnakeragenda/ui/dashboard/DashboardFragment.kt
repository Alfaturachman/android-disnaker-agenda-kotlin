package com.example.disnakeragenda.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.disnakeragenda.databinding.FragmentDashboardBinding
import com.example.disnakeragenda.ui.mediator.agenda.RiwayatAgendaActivity
import com.example.disnakeragenda.ui.mediator.agenda.tambah.TambahAgendaActivity
import com.example.disnakeragenda.ui.mediator.laporan.riwayat.RiwayatLaporanActivity
import com.example.disnakeragenda.ui.mediator.laporan.tambah.TambahLaporanActivity
import com.example.disnakeragenda.ui.pelapor.RiwayatPelaporActivity
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

        // Level dari SharedPreferences
        val level = getLevelFromSharedPreferences()
        when (level) {
            "pelapor" -> {
                binding.layoutPelapor.visibility = View.VISIBLE
                binding.layoutMediator.visibility = View.GONE

                binding.cardViewRiwayatLaporan.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatPelaporActivity::class.java)
                    startActivity(intent)
                }

                binding.cardViewTambahLaporan.setOnClickListener {
                    val intent = Intent(requireContext(), TambahPelaporActivity::class.java)
                    startActivity(intent)
                }
            }
            "mediator" -> {
                binding.layoutPelapor.visibility = View.GONE
                binding.layoutMediator.visibility = View.VISIBLE

                binding.cardViewTambahAgenda.setOnClickListener {
                        val intent = Intent(requireContext(), TambahAgendaActivity::class.java)
                    startActivity(intent)
                }

                binding.cardViewRiwayatAgenda.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatAgendaActivity::class.java)
                    startActivity(intent)
                }

                binding.cardViewTambahLaporanMediator.setOnClickListener {
                    val intent = Intent(requireContext(), TambahLaporanActivity::class.java)
                    startActivity(intent)
                }

                binding.cardViewRiwayatLaporanMediator.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatLaporanActivity::class.java)
                    startActivity(intent)
                }
            }
            else -> {
                Toast.makeText(requireContext(), "Level pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        return root
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