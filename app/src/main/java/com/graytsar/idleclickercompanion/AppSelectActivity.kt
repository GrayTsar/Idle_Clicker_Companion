package com.graytsar.idleclickercompanion

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_app_select.*
import kotlinx.android.synthetic.main.item_app_select.view.*
import kotlinx.android.synthetic.main.toolbar.*

class AppSelectActivity : AppCompatActivity() {

    private var listAppSelect = ArrayList<AppSelectModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_select)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //for back arrow

        val pm = packageManager
        val listPackages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val linearLayoutManager = LinearLayoutManager(this)

        recyclerAppSelect.layoutManager = linearLayoutManager
        recyclerAppSelect.adapter = AppSelectAdapter(this, listAppSelect)

        for (applicationInfo in listPackages) {

            if(applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0){
                val view = layoutInflater.inflate(R.layout.item_app_select, null)
                view.textAppSelect.text = packageManager.getApplicationLabel(packageManager.getApplicationInfo(applicationInfo.packageName, 0)).toString()
                view.imageAppSelect.setImageDrawable(packageManager.getApplicationIcon(packageManager.getApplicationInfo(applicationInfo.packageName, 0)))

                val appSelectModel = AppSelectModel(this,
                    packageManager.getApplicationLabel(packageManager.getApplicationInfo(applicationInfo.packageName, 0)).toString(),
                    packageManager.getApplicationIcon(packageManager.getApplicationInfo(applicationInfo.packageName, 0)),
                    applicationInfo.packageName)

                listAppSelect.add(appSelectModel)
            }
        }
        recyclerAppSelect.adapter!!.notifyDataSetChanged()
    }
}
