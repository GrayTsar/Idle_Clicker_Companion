package com.graytsar.idleclickercompanion

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController

import androidx.navigation.fragment.NavHostFragment
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "AppCard")
class AppModel(
    @PrimaryKey(autoGenerate = true) var idApp:Int,
    @ColumnInfo(name = "appName") var appName: String,
    @ColumnInfo(name = "userName") var userName: String,
    @Ignore var icon: Bitmap?,
    @ColumnInfo(name = "appPath") var appPath: String) {

    constructor():this( 0,"", "", null, "")

    //var serializableObj = SerializableAppCard(appName, userName, icon, appPath)

    fun onCardClick(view: View){
        val f = (view.context as MainActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController:NavController = f.navController
        val bundle = Bundle().apply { putString("obj", Gson().toJson(this@AppModel)) }

        navController.navigate(R.id.appDetailFragment, bundle)
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