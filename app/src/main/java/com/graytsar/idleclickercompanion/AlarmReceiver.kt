package com.graytsar.idleclickercompanion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
            //Log.d("DBG: ", "Alarm onReceive")
        }

        val bundle = intent.extras!!
        val idAlarm = bundle.getLong("idAlarm", 9999)
        val idApp = bundle.getLong("idApp")


        if(SingletonStatic.db == null){
            SingletonStatic.db = Room.databaseBuilder(
                context!!.applicationContext,
                PersistentRoomDatabase::class.java, "AppCard_Database"
            ).allowMainThreadQueries().build()
        }

        //java.lang.RuntimeException: Unable to start receiver com.graytsar.idleclickercompanion.AlarmReceiver: java.lang.ArrayIndexOutOfBoundsException: length=0; index=0
        //detail -> open drawer manually -> click home
        val arAlarm = SingletonStatic.db!!.alarmDao().findAlarmByIdAlarm(idAlarm)
        val alarm= arAlarm[0]
        alarm.startAlarm!!.value = false
        SingletonStatic.db!!.alarmDao().updateAlarm(alarm)

        val array = SingletonStatic.db!!.alarmDao().findAlarmByIdApp(alarm.idListApp)
        val app = SingletonStatic.db!!.appDao().findApp(alarm.idListApp)[0]

        var alwaysTrue = true
        var atLeastOneTrue = false
        array.forEach {
            if(!it.startAlarm!!.value!!){
                alwaysTrue = false
            } else {
                atLeastOneTrue = true
            }
        }

        if(atLeastOneTrue){
            app.startAll!!.value = true
        }
        else if (!atLeastOneTrue){
            app.startAll!!.value = false
        }

        SingletonStatic.db!!.appDao().updateApp(app)







        //(context as MainActivity).recyclerHome?.adapter?.notifyDataSetChanged()

        //val time = intent.getLong("time")
        //val path = bundle.getString("appPath")
        //val title =  bundle.getString("appName")
        //val text = bundle.getString("action")
        SingletonStatic.pushNotify(bundle.getString("packageName")!!, bundle.getString("applicationLabel")!!, bundle.getString("action")!!)
    }
}