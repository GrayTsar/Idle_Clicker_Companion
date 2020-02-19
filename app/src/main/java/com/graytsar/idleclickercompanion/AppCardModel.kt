package com.graytsar.idleclickercompanion

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController

import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.google.gson.annotations.Expose


class AppCardModel(
    @Expose(serialize = false, deserialize = false) val activity:FragmentActivity,
    @Expose val appName:String,
    @Expose val userName:String,
    @Expose val icon: Drawable,
    @Expose val appPath:String)
{
    var serializableObj = SerializableAppDetail(appName, userName, icon.toBitmap(), appPath)

    fun onCardClick(view: View){
        val f = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController:NavController = f.navController
        val bundle = Bundle().apply { putString("obj", Gson().toJson(serializableObj)) }

        navController.navigate(R.id.appDetailFragment, bundle)
    }

    override fun equals(other: Any?): Boolean {
        var bool = false

        if(other != null && other is AppCardModel){
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
        var result = activity.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + appPath.hashCode()
        return result
    }
}