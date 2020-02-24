package com.graytsar.idleclickercompanion

import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.view.forEach
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.room.TypeConverter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*


@BindingMethods(value = [
    BindingMethod(
        type = androidx.constraintlayout.utils.widget.ImageFilterView::class,
        attribute = "app:srcCompat",
        method = "setImageBitmap" )])

//toDo: remove Singleton Class
object SingletonStatic {
    var activity:MainActivity? = null
    var notificationManager:NotificationManager? = null
    var channelID:String? = null
    val notificationID:Int = 101

    lateinit var db:PersistentRoomDatabase

    fun pushNotify(appPath:String, title:String, text:String){
        val launchIntent = activity!!.packageManager.getLaunchIntentForPackage(appPath)
        val pendingIntent:PendingIntent = PendingIntent.getActivity(activity, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)

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