package com.graytsar.idleclickercompanion

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

import androidx.navigation.fragment.NavHostFragment
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "AppCard")
class AppModel constructor(
    @PrimaryKey(autoGenerate = true) var idApp:Long,
    @ColumnInfo(name = "appName") var applicationLabel: String,
    @ColumnInfo(name = "userName") var userName: MutableLiveData<String>?,
    @Ignore var icon: Bitmap?,
    @ColumnInfo(name = "appPath") var packageName: String,
    @ColumnInfo(name = "startAll") var startAll: MutableLiveData<Boolean>?) {

    constructor():this( 0, "", null, null, "", null)

    fun onCardClick(view: View){
        val f = (view.context as MainActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController:NavController = f.navController

        val bundle = Bundle().apply { putLong("key", idApp) }

        navController.navigate(R.id.appDetailFragment, bundle)
    }

    fun onSwitchClick(view:View){
        val b = startAll!!.value!!
        startAll!!.value = !b

        activateAll(view.context)
    }

    private fun activateAll(context: Context){
        val array = SingletonStatic.db.alarmDao().findAlarm(idApp)

        array.forEach {
            it.activate(context, startAll!!.value!!)
        }
    }
}