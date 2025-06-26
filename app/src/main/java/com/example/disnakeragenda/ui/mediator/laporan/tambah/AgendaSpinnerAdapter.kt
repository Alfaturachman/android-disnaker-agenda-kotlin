package com.example.disnakeragenda.ui.mediator.laporan.tambah

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.disnakeragenda.model.AgendaLaporan

class AgendaSpinnerAdapter(context: Context, private val agendaList: List<AgendaLaporan>) :
    ArrayAdapter<AgendaLaporan>(context, android.R.layout.simple_spinner_item, agendaList) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        val agenda = agendaList[position]
        view.text = formatAgendaText(agenda) // Format teks sesuai kebutuhan
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        val agenda = agendaList[position]
        view.text = formatAgendaText(agenda) // Format teks sesuai kebutuhan
        return view
    }

    // Fungsi untuk memformat teks agenda
    private fun formatAgendaText(agenda: AgendaLaporan): String {
        return if (agenda.id == -1) {
            // Jika agenda default, tampilkan "Pilih Agenda"
            "Pilih Agenda"
        } else {
            // Format: "nama_pihak_satu & nama_pihak_dua - nama_kasus"
            "${agenda.nama_pihak_satu ?: ""} & ${agenda.nama_pihak_dua ?: ""} - ${agenda.jenis_kasus ?: ""}"
        }
    }
}