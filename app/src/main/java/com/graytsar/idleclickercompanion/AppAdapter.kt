package com.graytsar.idleclickercompanion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.idleclickercompanion.databinding.ItemGameCardBinding
import kotlinx.android.synthetic.main.item_game_card.view.*

class AppCardAdapter (val context: Context, val list:ArrayList<AppModel>): RecyclerView.Adapter<ViewHolderAppCard>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAppCard {
        val binding = DataBindingUtil.inflate<ItemGameCardBinding>(LayoutInflater.from(context), R.layout.item_game_card, parent, false)
        return ViewHolderAppCard(binding.root, binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolderAppCard, position: Int) {
        holder.binding.appCardModel = list[position]
        holder.icon.setImageBitmap(list[position].icon)
    }
}

class ViewHolderAppCard (view: View, val binding:ItemGameCardBinding) : RecyclerView.ViewHolder(view) {
    var icon: ImageFilterView = view.iconGame
}