package com.graytsar.idleclickercompanion

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

import androidx.navigation.fragment.NavHostFragment
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.material.snackbar.Snackbar

@Entity(tableName = "AppCard")
class AppModel constructor(
    @PrimaryKey(autoGenerate = true) var idApp:Long,
    @ColumnInfo(name = "appName") var applicationLabel: String,
    @ColumnInfo(name = "userName") var userName: MutableLiveData<String>?,
    @Ignore var icon: Drawable?,
    @ColumnInfo(name = "appPath") var packageName: String,
    @ColumnInfo(name = "startAll") var startAll: MutableLiveData<Boolean>?,
    @ColumnInfo(name = "position") var position: Int) {


    constructor():this( 0, "", null, null, "", null, 0)

    fun onCardClick(view: View){
        val f = (view.context as MainActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController:NavController = f.navController

        val bundle = Bundle().apply { putLong("key", idApp) }

        navController.navigate(R.id.appDetailFragment, bundle)
    }

    fun onSwitchClick(view:View) {
        val b = startAll!!.value!!
        startAll!!.value = !b


        val array = SingletonStatic.db!!.alarmDao().getAllAlarmSimple(idApp)

        var alwaysTrue = true
        var atLeastOneTrue = false
        array.forEach {
            if (!it.activate(view.context, startAll!!.value!!)) {
                alwaysTrue = false
            } else {
                atLeastOneTrue = true
            }
        }

        //only if alarm was set to activate and at least one alarm was startable
        if((!b && !alwaysTrue && !atLeastOneTrue) || array.isNullOrEmpty()){
            startAll!!.value = false
            Snackbar.make(view, view.context.getString(R.string.noAlarmsStartable), Snackbar.LENGTH_LONG).show()
        }
    }
}