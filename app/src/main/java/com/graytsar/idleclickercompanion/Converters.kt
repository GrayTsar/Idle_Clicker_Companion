package com.graytsar.idleclickercompanion

import androidx.lifecycle.MutableLiveData
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

    @TypeConverter
    fun mutableLiveDataFromString(str:String):MutableLiveData<String>{
        return MutableLiveData<String>(str)
    }

    @TypeConverter
    fun mutableLiveDataToString(data:MutableLiveData<String>):String{
        return data.value!!
    }

    @TypeConverter
    fun mutableLiveDataFromLong(num:Long):MutableLiveData<Long>{
        return MutableLiveData<Long>(num)
    }

    @TypeConverter
    fun mutableLiveDataToLong(data:MutableLiveData<Long>):Long{
        return data.value!!
    }

    @TypeConverter
    fun mutableLiveDataFromInt(num:Int):MutableLiveData<Int>{
        return MutableLiveData<Int>(num)
    }

    @TypeConverter
    fun mutableLiveDataToInt(data:MutableLiveData<Int>):Int{
        return data.value!!
    }

    @TypeConverter
    fun mutableLiveDataFromBoolean(boolean:Boolean):MutableLiveData<Boolean>{
        return MutableLiveData<Boolean>(boolean)
    }

    @TypeConverter
    fun mutableLiveDataToBoolean(data:MutableLiveData<Boolean>):Boolean{
        return data.value!!
    }
}