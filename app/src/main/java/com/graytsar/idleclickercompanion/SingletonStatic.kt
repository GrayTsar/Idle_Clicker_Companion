package com.graytsar.idleclickercompanion

import android.app.NotificationManager
import android.app.PendingIntent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods

@BindingMethods(value = [
    BindingMethod(
        type = androidx.constraintlayout.utils.widget.ImageFilterView::class,
        attribute = "app:srcCompat",
        method = "setImageDrawable" )])

//toDo: remove Singleton Class
object SingletonStatic {
    var activity:MainActivity? = null
    var notificationManager:NotificationManager? = null
    var channelID:String = "com.graytsar.idleclickercompanion.Alarm"
    private const val notificationID:Int = 101

    var db:PersistentRoomDatabase? = null

    fun pushNotify(appPath:String, title:String, text:String){
        val launchIntent = activity!!.packageManager.getLaunchIntentForPackage(appPath)
        val pendingIntent:PendingIntent = PendingIntent.getActivity(activity, 0, launchIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(activity!!, channelID).apply {
            setContentTitle(title)
            setContentText(text)
            setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            setContentIntent(pendingIntent)
            setChannelId(channelID)
        }.build()

        notificationManager!!.notify(notificationID, notification)
    }

}