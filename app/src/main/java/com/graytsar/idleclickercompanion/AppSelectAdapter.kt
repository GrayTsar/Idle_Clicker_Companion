package com.graytsar.idleclickercompanion

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.idleclickercompanion.databinding.ItemAppSelectBinding
import kotlinx.android.synthetic.main.item_app_select.view.*

class AppSelectAdapter(val context: Context, val list:ArrayList<AppSelectModel>): RecyclerView.Adapter<ViewHolderAppSelect>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAppSelect {
        val binding = DataBindingUtil.inflate<ItemAppSelectBinding>(LayoutInflater.from(context), R.layout.item_app_select, parent, false)
        return ViewHolderAppSelect(binding.root, binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolderAppSelect, position: Int) {
        holder.binding.appSelectModel = list[position]
        holder.icon.setImageDrawable(list[position].icon)
    }
}

class ViewHolderAppSelect (view: View, val binding: ItemAppSelectBinding) : RecyclerView.ViewHolder(view) {
    var icon: ImageView = view.imageAppSelect
}