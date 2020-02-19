package com.graytsar.idleclickercompanion

import android.graphics.Bitmap

class SerializableAppDetail(val appName:String, val userName:String, val icon: Bitmap, val appPath:String){
    val listAlarm:ArrayList<SerializableAppAlarm> = ArrayList<SerializableAppAlarm>()
}