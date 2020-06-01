package com.graytsar.idleclickercompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.idleclickercompanion.databinding.ItemGameCardBinding

class AppCardAdapter (private val activity: HomeFragment): ListAdapter<AppModel, ViewHolderApp>(AppModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderApp {
        val binding = DataBindingUtil.inflate<ItemGameCardBinding>(LayoutInflater.from(activity.context), R.layout.item_game_card, parent, false)
        return ViewHolderApp(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolderApp, position: Int) {
        try{
            getItem(position).icon = activity.requireContext().packageManager.getApplicationIcon(getItem(position).packageName)
        } catch (e: Exception) {
            SingletonStatic.db!!.appDao().deleteApp(getItem(position))
        }


        holder.binding.lifecycleOwner = activity
        holder.binding.appCardModel = getItem(position)
    }
}

class ViewHolderApp (view: View, val binding:ItemGameCardBinding) : RecyclerView.ViewHolder(view) {

}

class AppModelDiffCallback: DiffUtil.ItemCallback<AppModel>(){
    override fun areItemsTheSame(oldItem: AppModel, newItem: AppModel): Boolean {
        return oldItem.idApp == newItem.idApp
    }

    override fun areContentsTheSame(oldItem: AppModel, newItem: AppModel): Boolean {
        return (oldItem.applicationLabel == newItem.applicationLabel) && (oldItem.packageName == newItem.packageName) && (oldItem.userName?.value == newItem.userName?.value) && (oldItem.startAll?.value == newItem.startAll?.value)
    }
}
