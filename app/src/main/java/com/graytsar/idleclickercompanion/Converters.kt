package com.graytsar.idleclickercompanion

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun arrayBooleanFromJson(str:String):Array<Boolean>{
        return Gson().fromJson(str, Array<Boolean>::class.java)
    }

    @TypeConverter
    fun arrayBooleanToJson(array:Array<Boolean>):String{
        return Gson().toJson(array)
    }
}