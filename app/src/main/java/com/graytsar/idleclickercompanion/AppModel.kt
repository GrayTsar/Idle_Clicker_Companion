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
import com.google.gson.Gson
import com.google.gson.annotations.Expose

@Entity(tableName = "AppCard")
class AppModel(
    @PrimaryKey(autoGenerate = true) var idApp:Long,
    @ColumnInfo(name = "appName") var appName: String,
    @ColumnInfo(name = "userName") var userName: String,
    @Ignore var icon: Bitmap?,
    @ColumnInfo(name = "appPath") var appPath: String,
    @ColumnInfo(name = "startAll") var startAll: Boolean) {

    @Ignore @Transient var obsStartAll = MutableLiveData<Boolean>()

    constructor():this( 0,"", "", null, "", false)

    init {
        if(idApp > 0){
            val array = SingletonStatic.db.appAlarmDao().getAllAppAlarm(idApp)

            var allFalse = true
            array.forEach {
                if(it.startAlarm == true){
                    allFalse = false
                }
            }

            if(allFalse == true){
                startAll = false
                SingletonStatic.db.appCardDao().updateAppCard(this)
            }
        }

        obsStartAll.value = startAll
        val a = 0
    }

    //var serializableObj = SerializableAppCard(appName, userName, icon, appPath)

    fun onCardClick(view: View){
        val f = (view.context as MainActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController:NavController = f.navController

        val bundle = Bundle().apply { putString("obj", Gson().toJson(this@AppModel)) }

        navController.navigate(R.id.appDetailFragment, bundle)
    }

    fun onSwitchClick(view:View){
        startAll = !startAll
        obsStartAll.value = startAll


        activateAll(view.context)


        SingletonStatic.db.appCardDao().updateAppCard(this)
    }

    private fun activateAll(context: Context){
        val array = SingletonStatic.db.appAlarmDao().getAllAppAlarm(idApp)

        array.forEach {
            it.activate(context, startAll)
        }
    }

    override fun equals(other: Any?): Boolean {
        var bool = false

        if(other != null && other is AppModel){
            if(this.toString() == other.toString()){
                bool = true
            }
        }
        return bool
    }

    override fun toString(): String {
        return userName + appPath
    }

    override fun hashCode(): Int {
        var result = appName.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + appPath.hashCode()
        return result
    }
}