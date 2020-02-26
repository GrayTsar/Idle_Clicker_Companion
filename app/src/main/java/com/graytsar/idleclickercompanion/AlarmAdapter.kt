package com.graytsar.idleclickercompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.idleclickercompanion.databinding.ItemGameAlertBinding

class AppAlarmAdapter(private val activity: AppDetailFragment): ListAdapter<AlarmModel, ViewHolderAlarm>(AlarmModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAlarm {
        val binding = DataBindingUtil.inflate<ItemGameAlertBinding>(LayoutInflater.from(activity.context), R.layout.item_game_alert, parent, false)
        return ViewHolderAlarm(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolderAlarm, position: Int) {
        holder.binding.lifecycleOwner = activity
        holder.binding.alarmModel = getItem(position)

        if(getItem(position).startAlarm!!.value!!){
            getItem(position).launchCountdown()
        }
    }
}

class ViewHolderAlarm(view: View, val binding: ItemGameAlertBinding): RecyclerView.ViewHolder(view){

}

class AlarmModelDiffCallback:DiffUtil.ItemCallback<AlarmModel>(){
    override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
        return oldItem.idAlarm == newItem.idAlarm
    }

    override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
        return (oldItem.selectedAction == newItem.selectedAction) && (oldItem.selectedHour == newItem.selectedHour) && (oldItem.selectedMinute == newItem.selectedMinute)
    }
}