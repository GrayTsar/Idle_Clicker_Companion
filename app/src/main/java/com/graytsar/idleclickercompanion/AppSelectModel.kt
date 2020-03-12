package com.graytsar.idleclickercompanion

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.picker_app_select.view.*

class AppSelectModel(private val activity: Activity, val applicationLabel:String, val applicationIcon: Drawable, val packageName:String) {

    fun onClickAppSelect(view:View){
        val result = Intent()

        val builder = AlertDialog.Builder(activity)
        val picker = activity.layoutInflater.inflate(R.layout.picker_app_select, null)

        builder.setView(picker)
        builder.create()
        builder.show()

        picker.dialogButtonAppSelect.setOnClickListener {
            if(picker.textPickerAppSelect.text.isEmpty()){
                picker.textPickerAppSelect.error = activity.getString(R.string.textPickerErrorHint)
            } else {
                result.putExtra("userName", picker.textPickerAppSelect.text.toString())
                result.putExtra("applicationLabel", applicationLabel)
                result.putExtra("packageName", packageName)
                activity.setResult(1, result)
                activity.finish()
            }
        }
    }
}