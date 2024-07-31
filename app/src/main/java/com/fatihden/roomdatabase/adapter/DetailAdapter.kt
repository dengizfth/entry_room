package com.fatihden.roomdatabase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.fatihden.roomdatabase.UI.ListeFragmentDirections
import com.fatihden.roomdatabase.databinding.RecyclerRowBinding
import com.fatihden.roomdatabase.model.Detail

class DetailAdapter(val detayListesi : List<Detail>) : RecyclerView.Adapter<DetailAdapter.DetailHolder>() {

    class DetailHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DetailHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return detayListesi.size
    }

    override fun onBindViewHolder(holder: DetailHolder, position: Int) {
        holder.binding.rowItem.text = detayListesi[position].isim
        holder.itemView.setOnClickListener {
            val action = ListeFragmentDirections.actionListeFragmentToDetayFragment(
                bilgi = "eski" ,
                id = detayListesi[position].id
            )
            Navigation.findNavController(it).navigate(action)

        }



    }


}