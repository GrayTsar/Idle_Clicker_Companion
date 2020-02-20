package com.graytsar.idleclickercompanion

import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat

//toDo: remove Singleton Class
object SingletonStatic {
    var activity:MainActivity? = null
    var notificationManager:NotificationManager? = null
    var channelID:String? = null
    val notificationID:Int = 101

    lateinit var db:PersistentRoomDatabase


    fun pushNotify(appPath:String, title:String, text:String){
        var launchIntent = activity!!.packageManager.getLaunchIntentForPackage(appPath)
        var pendingIntent:PendingIntent = PendingIntent.getActivity(activity, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val notification = NotificationCompat.Builder(activity!!, channelID!!)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_keyboard_arrow_down_black_24dp)
            .setChannelId(channelID!!)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager!!.notify(notificationID, notification)
    }
}