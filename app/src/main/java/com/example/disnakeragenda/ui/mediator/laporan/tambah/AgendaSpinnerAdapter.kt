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
        view.text = formatText(getItem(position))
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = formatText(getItem(position))
        return view
    }

    private fun formatText(agenda: AgendaLaporan?): String {
        return if (agenda == null ||
            (agenda.nama_pihak_satu == null &&
                    agenda.nama_pihak_dua == null &&
                    agenda.nama_kasus == null)
        ) {
            "Pilih Agenda Mediasi"
        } else {
            "${agenda.nama_pihak_satu ?: "-"} & ${agenda.nama_pihak_dua ?: "-"} - ${agenda.nama_kasus ?: "-"}"
        }
    }
}
