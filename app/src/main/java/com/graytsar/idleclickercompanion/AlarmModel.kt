package com.graytsar.idleclickercompanion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import kotlinx.android.synthetic.main.item_game_alert.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "Alarm",
    foreignKeys = [ForeignKey(entity = AppModel::class, parentColumns = arrayOf("idApp"), childColumns = arrayOf("idListAlarm"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)],
    indices = [Index(value = ["idListAlarm"])]
)
class AlarmModel(
    @PrimaryKey(autoGenerate = true) var idAlarm:Long,
    @ColumnInfo(name = "idListAlarm") var idListAlarm:Long,
    @ColumnInfo(name = "appName")var appName:String,
    @ColumnInfo(name = "appPath")var appPath:String,
    @ColumnInfo(name = "hour") var selectedHour:Int,
    @ColumnInfo(name = "minute") var selectedMinute:Int,
    @ColumnInfo(name = "repeat") var selectedRepeat:Int,
    @ColumnInfo(name = "action") var selectedAction:String,
    @ColumnInfo(name = "fireAlarmIn") var fireAlarmIn:Long,
    @ColumnInfo(name = "startAlarm") var startAlarm:Boolean,
    @ColumnInfo(name = "selectedDaysAr") var selectedDaysAr:Array<Boolean>) {

    @Ignore var obsTextDays:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsTimeLeft:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsStartAlarm:MutableLiveData<Boolean> = MutableLiveData()

    @Ignore private var expandVisibility:Boolean = false

    @Ignore private val minuteInMs:Long = 1000L

    constructor():this(0,0,"","",1,0,0,"",0,false, arrayOf(true,true,true,true,true,true,true))

    init{
        obsTextDays.value = "Daily"
        obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}:00"
        obsStartAlarm.value = startAlarm

        setupDayString()
    }

    fun activate(context:Context, _start:Boolean){
        var start = _start

        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]

        when (day) {
            Calendar.MONDAY -> {
                if(!selectedDaysAr[0]){
                    start = false
                }
            }
            Calendar.TUESDAY -> {
                if(!selectedDaysAr[1]){
                    start = false
                }
            }
            Calendar.WEDNESDAY -> {
                if(!selectedDaysAr[2]){
                    start = false
                }
            }
            Calendar.THURSDAY -> {
                if(!selectedDaysAr[3]){
                    start = false
                }
            }
            Calendar.FRIDAY -> {
                if(!selectedDaysAr[4]){
                    start = false
                }
            }
            Calendar.SATURDAY -> {
                if(!selectedDaysAr[5]){
                    start = false
                }
            }
            Calendar.SUNDAY -> {
                if(!selectedDaysAr[6]){
                    start = false
                }
            }
        }

        setAlarm(context, start)

        SingletonStatic.db.appAlarmDao().updateAppAlarm(this)
    }

    private fun setAlarm(context:Context, start:Boolean){
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        if(start == true){
            startAlarm = true
            fireAlarmIn = System.currentTimeMillis() + selectedMinute * minuteInMs + selectedHour * 3600000
            intent.putExtra("time", fireAlarmIn)
            intent.putExtra("appPath", appPath)
            intent.putExtra("action", selectedAction)
            intent.putExtra("appName", appName)
            intent.putExtra("idAlarm", idAlarm)
            val alarmIntent:PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            alarmManager.apply {
                set(AlarmManager.RTC_WAKEUP, fireAlarmIn, alarmIntent)
                //min sdk 23
                setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireAlarmIn, alarmIntent)
            }
        } else if (start == false) {
            val alarmIntent:PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.cancel(alarmIntent)
            fireAlarmIn = 0
            startAlarm = false
        }
    }

    fun onClickStartAlarm(view:View){
        /*startAlarm = !startAlarm
        obsStartAlarm.value = startAlarm

        if(startAlarm){
            setAlarm(view.context, true)

            launchCountdown()
        } else {
            setAlarm(view.context, false)
            obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}:00"
        }

        SingletonStatic.db.appAlarmDao().updateAppAlarm(this)*/
    }

    fun launchCountdown(){
        GlobalScope.launch {
            SingletonStatic.db.appAlarmDao().updateAppAlarm(this@AlarmModel)
            val formatter = SimpleDateFormat("HH:mm:ss")
            var ms = fireAlarmIn - System.currentTimeMillis()

            while(ms > 0){
                delay(minuteInMs)
                ms = fireAlarmIn - System.currentTimeMillis()

                obsTimeLeft.postValue(formatter.format(Calendar.getInstance().apply { timeInMillis = ms }.time))
            }
            startAlarm = !startAlarm
            obsStartAlarm.postValue(startAlarm)
            obsTimeLeft.postValue("${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}:00")
            //pushNotify()

            SingletonStatic.db.appAlarmDao().updateAppAlarm(this@AlarmModel)
        }
    }

    fun onDayClick(view:View){
        when(view){
            view.buttonMonday ->{
                selectedDaysAr[0] = !selectedDaysAr[0]
            }
            view.buttonTuesday ->{
                selectedDaysAr[1] = !selectedDaysAr[1]
            }
            view.buttonWednesday ->{
                selectedDaysAr[2] = !selectedDaysAr[2]
            }
            view.buttonThursday ->{
                selectedDaysAr[3] = !selectedDaysAr[3]
            }
            view.buttonFriday ->{
                selectedDaysAr[4] = !selectedDaysAr[4]
            }
            view.buttonSaturday ->{
                selectedDaysAr[5] = !selectedDaysAr[5]
            }
            view.buttonSunday ->{
                selectedDaysAr[6] = !selectedDaysAr[6]
            }
        }

        setupDayString()

        SingletonStatic.db.appAlarmDao().updateAppAlarm(this)
    }

    fun onExpandClick(view: View){
        expandVisibility = !expandVisibility

        if(expandVisibility){
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_keyboard_arrow_up_accent_24dp))
            //Log.d("DBG: ", view.parent.toString())
            (view.parent as ConstraintLayout).listExpandDays.visibility = View.VISIBLE
        }
        else{
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_keyboard_arrow_down_accent_24dp))
            (view.parent as ConstraintLayout).listExpandDays.visibility = View.GONE
        }
    }

    private fun setupDayString(){
        var stringDays = ""
        var iFalseDays = 0
        var alwaysTrue = true

        for(i in 0..6){
            if(selectedDaysAr[i]){
                when (i) {
                    0 -> {
                        stringDays += "Mo."
                    }
                    1 -> {
                        stringDays += "Tu."
                    }
                    2 -> {
                        stringDays += "We."
                    }
                    3 -> {
                        stringDays += "Th."
                    }
                    4 -> {
                        stringDays += "Fr."
                    }
                    5 -> {
                        stringDays += "Sa."
                    }
                    6 -> {
                        stringDays += "So."
                    }
                }
            } else {
                alwaysTrue = false
                iFalseDays++
            }
        }

        obsTextDays.value = if(alwaysTrue)
            "Daily"
        else if(!alwaysTrue && iFalseDays == 7)
            "Never"
        else
            stringDays
    }

    private fun pushNotify(){
        SingletonStatic.pushNotify(appPath, appName, selectedAction)
    }
}