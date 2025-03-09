package com.example.disnakeragenda.ui.mediator.agenda.detail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.disnakeragenda.R
import com.example.disnakeragenda.model.Mediator

class MediatorSpinnerAdapter(
    context: Context,
    private val mediatorList: List<Mediator>
) : ArrayAdapter<Mediator>(context, R.layout.item_spinner, mediatorList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = mediatorList[position].nama
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = mediatorList[position].nama
        return view
    }

    // Mengembalikan id_mediator dari item yang dipilih
    fun getMediatorId(position: Int): Int {
        return mediatorList[position].id_mediator
    }
}