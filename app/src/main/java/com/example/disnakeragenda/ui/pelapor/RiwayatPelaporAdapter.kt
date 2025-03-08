package com.example.disnakeragenda.ui.pelapor

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.disnakeragenda.R
import com.example.disnakeragenda.model.RiwayatPelapor
import com.example.disnakeragenda.helpers.DateHelper
import com.example.disnakeragenda.ui.pelapor.detail.DetailPelaporActivity

class RiwayatPelaporAdapter(private var dataList: List<RiwayatPelapor>) :
    RecyclerView.Adapter<RiwayatPelaporAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvNomorMediasi: TextView = view.findViewById(R.id.tvNomorMediasi)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvJenisKasus: TextView = view.findViewById(R.id.tvJenisKasus)
        val cardView: View = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_riwayat_pelaporan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        val context = holder.itemView.context

        holder.tvNomorMediasi.text = "#${item.nomor_mediasi}"
        holder.tvTanggal.text = "Tanggal Mediasi: ${DateHelper.formatDate(item.tgl_mediasi)}"
        holder.tvStatus.text = "${item.status}"
        holder.tvJenisKasus.text = "Jenis Kasus: ${item.jenis_kasus}"

        // Cek status berdasarkan id_laporan
        val statusText = if (item.id_laporan != null && item.id_laporan != 0) {
            item.status_laporan
        } else {
            item.status
        }
        holder.tvStatus.text = statusText

        // status
        val statusColor = when (statusText) {
            "diproses" -> R.color.badge_warning
            "disetujui", "selesai" -> R.color.badge_success
            "ditolak" -> R.color.badge_danger
            else -> R.color.badge_secondary
        }
        holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, statusColor)

        // Button CardView
        holder.cardView.setOnClickListener {
            val intent = Intent(context, DetailPelaporActivity::class.java).apply {
                putExtra("id_mediasi", item.id)
                putExtra("id_laporan", item.id_laporan)
                putExtra("nomor_mediasi", item.nomor_mediasi)
                putExtra("nama_pihak_satu", item.nama_pihak_satu)
                putExtra("nama_pihak_dua", item.nama_pihak_dua)
                putExtra("tanggal_mediasi", item.tgl_mediasi)
                putExtra("waktu_mediasi", item.waktu_mediasi)
                putExtra("status", item.status)
                putExtra("jenis_kasus", item.jenis_kasus)
                putExtra("deskripsi_kasus", item.jenis_kasus)
                putExtra("tempat_mediasi", item.jenis_kasus)
                putExtra("file_pdf", item.file_pdf)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<RiwayatPelapor>) {
        dataList = newData
        notifyDataSetChanged()
    }
}
