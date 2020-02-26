package com.graytsar.idleclickercompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.idleclickercompanion.databinding.ItemAppSelectBinding

class AppSelectAdapter(private val activity: AppSelectActivity): ListAdapter<AppSelectModel, ViewHolderAppSelect>(AppSelectDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAppSelect {
        val binding = DataBindingUtil.inflate<ItemAppSelectBinding>(LayoutInflater.from(activity), R.layout.item_app_select, parent, false)
        return ViewHolderAppSelect(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolderAppSelect, position: Int) {
        holder.binding.lifecycleOwner = activity
        holder.binding.appSelectModel = getItem(position)
    }
}

class ViewHolderAppSelect (view: View, val binding: ItemAppSelectBinding) : RecyclerView.ViewHolder(view) {

}

class AppSelectDiffCallback: DiffUtil.ItemCallback<AppSelectModel>(){
    override fun areItemsTheSame(oldItem: AppSelectModel, newItem: AppSelectModel): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: AppSelectModel, newItem: AppSelectModel): Boolean {
        return (oldItem.applicationLabel == newItem.applicationLabel) && (oldItem.packageName == newItem.packageName)
    }
}