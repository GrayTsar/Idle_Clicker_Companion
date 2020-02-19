package com.graytsar.idleclickercompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.idleclickercompanion.databinding.ItemGameAlertBinding
import kotlinx.android.synthetic.main.item_game_alert.view.*

class AppAlarmAdapter(val activity: AppDetailFragment, val list:ArrayList<AppAlarmModel>): RecyclerView.Adapter<ViewHolderAppAlarm>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAppAlarm {
        val binding = DataBindingUtil.inflate<ItemGameAlertBinding>(LayoutInflater.from(activity.context), R.layout.item_game_alert, parent, false)
        return ViewHolderAppAlarm(binding.root, binding)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolderAppAlarm, position: Int) {
        holder.binding.lifecycleOwner = activity
        holder.binding.appAlarmModel = list[position]
        holder.timeLeft = list[position].repeatLeftObs.toString() + ":" + list[position].minuteLeft.toString()
    }
}

class ViewHolderAppAlarm(view: View, val binding: ItemGameAlertBinding):RecyclerView.ViewHolder(view){
    var timeLeft:String = view.textTimeLeft.text as String
}