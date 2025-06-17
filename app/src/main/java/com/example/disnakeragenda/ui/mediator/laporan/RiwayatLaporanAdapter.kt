package com.example.disnakeragenda.ui.mediator.laporan

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.example.disnakeragenda.model.AgendaLaporan
import com.example.disnakeragenda.ui.mediator.agenda.detail.DetailAgendaActivity
import com.example.disnakeragenda.ui.mediator.laporan.detail.DetailLaporanActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatLaporanAdapter(
    private var agendaList: List<AgendaLaporan>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatLaporanAdapter.AgendaViewHolder>() {

    class AgendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNomorMediasi: TextView = view.findViewById(R.id.tvNomorMediasi)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvJenisKasus: TextView = view.findViewById(R.id.tvJenisKasus)
        val tvTempatMediasi: TextView = view.findViewById(R.id.tvTempatMediasi)
        val cardView: View = view.findViewById(R.id.cardView)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_riwayat_laporan, parent, false)
        return AgendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        val item = agendaList[position]
        val context = holder.itemView.context

        holder.tvNomorMediasi.text = "#${item.nomor_mediasi}"
        holder.tvTanggal.text = "Tanggal : ${DateHelper.formatDate(item.tgl_mediasi.toString())}"
        holder.tvStatus.text = "${item.status}"
        holder.tvJenisKasus.text = "Jenis Kasus: ${item.jenis_kasus}"
        holder.tvTempatMediasi.text = "Tempat: ${item.tempat}"

        // Cek status berdasarkan id_laporan
        val statusText = if (item.id_laporan != null && item.id_laporan != 0) {
            item.status
        } else {
            item.status
        }
        holder.tvStatus.text = statusText

        // status
        val statusColor = when (statusText) {
            "diproses" -> R.color.badge_warning
            "disetujui" -> R.color.badge_success
            "ditolak" -> R.color.badge_danger
            else -> R.color.badge_secondary
        }
        holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, statusColor)

        // PERBAIKAN: Gunakan context yang tepat dan tambahkan validation
        holder.btnDelete.setOnClickListener {
            Log.d("RiwayatLaporanAdapter", "btnHapus diklik untuk item: ${item.id_laporan}")

            // Pastikan context adalah Activity context
            val activityContext = if (context is androidx.appcompat.app.AppCompatActivity) {
                context
            } else {
                // Fallback: cari Activity context dari view
                var currentContext = context
                while (currentContext is android.content.ContextWrapper) {
                    if (currentContext is androidx.appcompat.app.AppCompatActivity) {
                        break
                    }
                    currentContext = currentContext.baseContext
                }
                currentContext
            }

            // Validasi id_laporan
            if (item.id_laporan != null && item.id_laporan != 0) {
                showDeleteConfirmationDialog(activityContext, item.id_laporan)
            } else {
                Toast.makeText(context, "ID Laporan tidak valid", Toast.LENGTH_SHORT).show()
                Log.e("RiwayatLaporanAdapter", "ID Laporan null atau 0: ${item.id_laporan}")
            }
        }

        // Button CardView
        holder.cardView.setOnClickListener {
            val intent = Intent(context, DetailLaporanActivity::class.java).apply {
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
                putExtra("deskripsi_kasus", item.jenis_kasus)
                putExtra("tempat_mediasi", item.jenis_kasus)
            }
            startForResult.launch(intent)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, laporanId: Int?) {
        Log.d("RiwayatLaporanAdapter", "showDeleteConfirmationDialog dipanggil dengan ID: $laporanId")

        if (laporanId == null) {
            Log.e("RiwayatLaporanAdapter", "laporanId is null")
            return
        }

        try {
            // PERBAIKAN: Tambahkan try-catch dan pastikan context adalah Activity
            val dialog = AlertDialog.Builder(context)
                .setTitle("Hapus Laporan") // Perbaiki title
                .setMessage("Apakah Anda yakin ingin menghapus laporan ini?")
                .setPositiveButton("Hapus") { dialog, _ ->
                    Log.d("RiwayatLaporanAdapter", "User mengkonfirmasi hapus")
                    deleteLaporan(laporanId, context)
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    Log.d("RiwayatLaporanAdapter", "User membatalkan hapus")
                    dialog.dismiss()
                }
                .setCancelable(true)
                .create()

            // Pastikan dialog ditampilkan di UI thread
            if (context is androidx.appcompat.app.AppCompatActivity) {
                if (!context.isFinishing && !context.isDestroyed) {
                    dialog.show()
                    Log.d("RiwayatLaporanAdapter", "Dialog berhasil ditampilkan")
                } else {
                    Log.e("RiwayatLaporanAdapter", "Activity is finishing or destroyed")
                }
            } else {
                Log.e("RiwayatLaporanAdapter", "Context bukan AppCompatActivity: ${context.javaClass.simpleName}")
            }
        } catch (e: Exception) {
            Log.e("RiwayatLaporanAdapter", "Error showing dialog: ${e.message}", e)
            Toast.makeText(context, "Terjadi kesalahan saat menampilkan dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteLaporan(laporanId: Int, context: Context) {
        Log.d("RiwayatLaporanAdapter", "Menghapus laporan dengan ID: $laporanId")

        val jsonObject = JSONObject().apply {
            put("id_laporan", laporanId)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deleteLaporan(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("RiwayatLaporanAdapter", "Laporan berhasil dihapus")
                    Toast.makeText(context, "Laporan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Log.e("RiwayatLaporanAdapter", "Gagal menghapus laporan: ${response.code()}")
                    Toast.makeText(context, "Gagal menghapus laporan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("RiwayatLaporanAdapter", "Error menghapus laporan: ${t.message}", t)
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = agendaList.size

    fun updateData(newData: List<AgendaLaporan>) {
        agendaList = newData
        notifyDataSetChanged()
    }
}