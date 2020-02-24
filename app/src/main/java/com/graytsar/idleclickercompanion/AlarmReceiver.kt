package com.graytsar.idleclickercompanion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
            //Log.d("DBG: ", "Alarm onReceive")
        }

        val bundle = intent.extras!!
        val idAlarm = bundle.getLong("idAlarm")

        val alarm = SingletonStatic.db.alarmDao().findAlarm(idAlarm)[0]
        alarm.startAlarm!!.value = false
        SingletonStatic.db.alarmDao().updateAlarm(alarm)

        val array = SingletonStatic.db.alarmDao().findAlarm(alarm.idListAlarm)
        var allFalse = true
        array.forEach {
            if(it.startAlarm!!.value!! == true){
                allFalse = false
            }
        }

        val app = SingletonStatic.db.appDao().findApp(alarm.idListAlarm)[0]

        if(allFalse == true){
            app.startAll!!.value = false
            //SingletonStatic.db.appCardDao().updateApp(this)
        }
        else if (allFalse == false){
            app.startAll!!.value = true
        }
        SingletonStatic.db.appDao().updateApp(app)







        //(context as MainActivity).recyclerHome?.adapter?.notifyDataSetChanged()

        //val time = intent.getLong("time")
        //val path = bundle.getString("appPath")
        //val title =  bundle.getString("appName")
        //val text = bundle.getString("action")
        SingletonStatic.pushNotify(bundle.getString("appPath")!!, bundle.getString("appName")!!, bundle.getString("action")!!)
    }
}