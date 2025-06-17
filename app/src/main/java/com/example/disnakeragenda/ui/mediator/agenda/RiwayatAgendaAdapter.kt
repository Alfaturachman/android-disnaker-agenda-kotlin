package com.example.disnakeragenda.ui.mediator.agenda

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.helpers.DateHelper
import com.example.disnakeragenda.model.AgendaMediasi
import com.example.disnakeragenda.ui.mediator.agenda.detail.DetailAgendaActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatAgendaAdapter(
    private var agendaList: List<AgendaMediasi>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatAgendaAdapter.AgendaViewHolder>() {

    class AgendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvNomorMediasi: TextView = view.findViewById(R.id.tvNomorMediasi)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvJenisKasus: TextView = view.findViewById(R.id.tvJenisKasus)
        val tvTempatMediasi: TextView = view.findViewById(R.id.tvTempatMediasi)
        val cardView: View = view.findViewById(R.id.cardView)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_riwayat_agenda, parent, false)
        return AgendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        val item = agendaList[position]
        val context = holder.itemView.context

        // Ambil status akhir berdasarkan id_laporan atau status utama
        val statusText = if (item.id_laporan != null && item.id_laporan != 0) {
            item.status_laporan
        } else {
            item.status
        }

        // Set teks ke TextView
        holder.tvNomorMediasi.text = "#${item.nomor_mediasi}"
        holder.tvTanggal.text = "Tanggal Mediasi: ${DateHelper.formatDate(item.tgl_mediasi)}"
        holder.tvStatus.text = statusText
        holder.tvJenisKasus.text = "Jenis Kasus: ${item.jenis_kasus}"
        holder.tvTempatMediasi.text = "Tempat: ${item.tempat}"

        // Set warna latar status
        val statusColor = when (statusText.lowercase()) {
            "diproses" -> R.color.badge_warning
            "disetujui", "selesai" -> R.color.badge_success
            "ditolak", "gagal" -> R.color.badge_danger
            else -> R.color.badge_secondary
        }
        holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, statusColor)

        // Daftar status yang menyebabkan tombol hapus dinonaktifkan
        val statusNonAktif = listOf("disetujui", "dilanjut ke pengadilan", "selesai")

        val isHapusDisabled = statusNonAktif.any { it.equals(statusText, ignoreCase = true) }

        holder.btnDelete.isEnabled = !isHapusDisabled
        holder.btnDelete.alpha = if (isHapusDisabled) 0.5f else 1.0f

        holder.btnDelete.setOnClickListener {
            if (!isHapusDisabled) {
                showDeleteConfirmationDialog(context, item.id)
            }
        }

        // Listener untuk cardView buka detail
        holder.cardView.setOnClickListener {
            val intent = Intent(context, DetailAgendaActivity::class.java).apply {
                putExtra("id_mediasi", item.id)
                putExtra("id_laporan", item.id_laporan)
                putExtra("id_mediator", item.id_mediator)
                putExtra("nomor_mediasi", item.nomor_mediasi)
                putExtra("nama_pihak_satu", item.nama_pihak_satu)
                putExtra("nama_pihak_dua", item.nama_pihak_dua)
                putExtra("tanggal_mediasi", item.tgl_mediasi)
                putExtra("waktu_mediasi", item.waktu_mediasi)
                putExtra("status", item.status)
                putExtra("jenis_kasus", item.jenis_kasus)
                putExtra("deskripsi_kasus", item.deskripsi_kasus)
                putExtra("tempat_mediasi", item.tempat)
                putExtra("file_pdf", item.file_pdf)
            }
            startForResult.launch(intent)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, mediasiId: Int?) {
        if (mediasiId == null) return

        AlertDialog.Builder(context)
            .setTitle("Hapus Petugas")
            .setMessage("Apakah Anda yakin ingin menghapus mediasi ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteLaporan(mediasiId, context)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun deleteLaporan(mediasiId: Int, context: Context) {
        val jsonObject = JSONObject().apply {
            put("id_mediasi", mediasiId)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deleteMediasi(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Mediasi berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus Mediasi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = agendaList.size

    fun updateData(newData: List<AgendaMediasi>) {
        agendaList = newData
        notifyDataSetChanged()
    }
}