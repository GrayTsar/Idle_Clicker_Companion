package com.graytsar.idleclickercompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.idleclickercompanion.databinding.ItemGameCardBinding
import kotlinx.android.synthetic.main.item_game_card.view.*

class AppCardAdapter (val activity: HomeFragment, val list:List<AppModel>): RecyclerView.Adapter<ViewHolderAppCard>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAppCard {
        val binding = DataBindingUtil.inflate<ItemGameCardBinding>(LayoutInflater.from(activity.context), R.layout.item_game_card, parent, false)
        return ViewHolderAppCard(binding.root, binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolderAppCard, position: Int) {
        val pm = activity.context!!.packageManager
        list[position].icon = pm.getApplicationIcon(list[position].packageName).toBitmap()

        holder.binding.lifecycleOwner = activity
        holder.binding.appCardModel = list[position]
    }
}

class ViewHolderAppCard (view: View, val binding:ItemGameCardBinding) : RecyclerView.ViewHolder(view) {
    var icon: ImageFilterView = view.iconGame
}