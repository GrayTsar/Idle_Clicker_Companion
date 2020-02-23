package com.graytsar.idleclickercompanion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
            //Log.d("DBG: ", "Alarm onReceive")
        }
        //toDo update recylcerHome from context. context == MainActivity


        val bundle = intent.extras!!

        val alarmDB = SingletonStatic.db.appAlarmDao().getAlarm(bundle.getLong("idAlarm"))[0]
        val alarm = AlarmModel(alarmDB.idAlarm, alarmDB.idListAlarm, alarmDB.appName, alarmDB.appPath, alarmDB.selectedHour, alarmDB.selectedMinute, alarmDB.selectedRepeat, alarmDB.selectedAction, 0, false, alarmDB.selectedDaysAr)
        SingletonStatic.db.appAlarmDao().updateAppAlarm(alarm)



        //val time = intent.getLong("time")
        //val path = bundle.getString("appPath")
        //val title =  bundle.getString("appName")
        //val text = bundle.getString("action")
        SingletonStatic.pushNotify(bundle.getString("appPath")!!, bundle.getString("appName")!!, bundle.getString("action")!!)
    }
}