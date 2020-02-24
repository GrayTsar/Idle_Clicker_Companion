package com.graytsar.idleclickercompanion

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class AppSelectModel(val activity: Activity, val applicationLabel:String, val applicationIcon:Drawable, val packageName:String) {

    fun onClickAppSelect(view:View){
        val result = Intent()
        val editText = EditText(activity).apply{
            maxLines = 1
            inputType = InputType.TYPE_CLASS_TEXT
        }
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Name")
        builder.setView(editText)
        builder.setPositiveButton("OK") { _, _ ->
            result.putExtra("userName", editText.text.toString())
            result.putExtra("applicationLabel", applicationLabel)
            result.putExtra("packageName", packageName)
            activity.setResult(1, result)
            activity.finish()
        }
        builder.show()
    }
}