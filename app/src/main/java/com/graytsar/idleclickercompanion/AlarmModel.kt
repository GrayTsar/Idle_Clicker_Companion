package com.graytsar.idleclickercompanion

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import kotlinx.android.synthetic.main.item_game_alert.view.*
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "Alarm",
    foreignKeys = arrayOf(ForeignKey(entity = AppModel::class, parentColumns = arrayOf("idApp"), childColumns = arrayOf("idListAlarm"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)),
    indices = [Index(value = ["idListAlarm"])]
)
class AlarmModel(
    @PrimaryKey(autoGenerate = true) var idAlarm:Int,
    @ColumnInfo(name = "idListAlarm") var idListAlarm:Int,
    @ColumnInfo(name = "appName")var appName:String,
    @ColumnInfo(name = "appPath")var appPath:String,
    @ColumnInfo(name = "hour") var selectedHour:Int,
    @ColumnInfo(name = "minute") var selectedMinute:Int,
    @ColumnInfo(name = "repeat") var selectedRepeat:Int,
    @ColumnInfo(name = "action") var selectedAction:String,
    @ColumnInfo(name = "hourLeft") var hL:Int,
    @ColumnInfo(name = "minuteLeft") var mL:Int,
    @ColumnInfo(name = "repeatLeft") var rL:Int,
    @ColumnInfo(name = "startAlarm") var sA:Boolean,
    @ColumnInfo(name = "d1") var d1:Boolean,
    @ColumnInfo(name = "d2") var d2:Boolean,
    @ColumnInfo(name = "d3") var d3:Boolean,
    @ColumnInfo(name = "d4") var d4:Boolean,
    @ColumnInfo(name = "d5") var d5:Boolean,
    @ColumnInfo(name = "d6") var d6:Boolean,
    @ColumnInfo(name = "d7")var d7:Boolean
)
{
    @Ignore var hourLeft = selectedHour
    @Ignore var minuteLeft = selectedMinute
    @Ignore var repeatLeft:Int = selectedRepeat
    @Ignore var startAlarm:Boolean = false


    @Ignore var obsRepeatLeft:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsTextDays:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsTimeLeft:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsStartAlarm:MutableLiveData<Boolean> = MutableLiveData()

    @Ignore private var expandVisibility:Boolean = false

    //var d1 = false
    //var d2 = false
    //var d3 = false
    //var d4 = false
    //var d5 = false
    //var d6 = false
    //var d7 = false

    constructor():this(0,0,"","",0,0,0,"",0,0,0,false,false,false,false,false,false,false,false)

    init{
        obsRepeatLeft.value = "$repeatLeft Times"
        obsTextDays.value = "Daily"
        obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
        obsStartAlarm.value = false

        val mMS = selectedMinute * 600000
        val hMS = selectedHour * 3600000

        val endT = System.currentTimeMillis() + mMS + hMS

        val calendar:Calendar = Calendar.getInstance()
        calendar.timeInMillis = endT

        val a = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").apply {
            timeZone = calendar.timeZone
        }
        Log.d("DBG: ", " "  + a.format(calendar.time))
    }

    fun onClickStartAlarm(view:View){
        val countDownTimer = object:CountDownTimer(10000, 1000) {
            override fun onFinish() {
                obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
                hourLeft = selectedHour
                minuteLeft = selectedMinute

                if(repeatLeft == 0){
                    repeatLeft = selectedRepeat
                } else {
                    repeatLeft--
                }

                obsRepeatLeft.value = "$repeatLeft Times"
                pushNotify()
            }

            override fun onTick(millisUntilFinished: Long) {
                if (minuteLeft == 0) {
                    if (hourLeft == 0) {
                        this.cancel()
                    }
                    minuteLeft = 59
                    hourLeft--
                }
                minuteLeft--
            }
        }.start()
    }


    private fun pushNotify(){
        SingletonStatic.pushNotify(appPath, appName, selectedAction)
    }

    fun onDayClick(view:View){
        when(view){
            view.buttonMonday ->{
                d1 = !d1
            }
            view.buttonTuesday ->{
                d2 = !d2
            }
            view.buttonWednesday ->{
                d3 = !d3
            }
            view.buttonThursday ->{
                d4 = !d4
            }
            view.buttonFriday ->{
                d5 = !d5
            }
            view.buttonSaturday ->{
                d6 = !d6
            }
            view.buttonSunday ->{
                d7 = !d7
            }
        }

        var stringDays = ""
        var iDays = 0
        var alwaysTrue = true

        for(i in 0..6){
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
                else -> {
                    alwaysTrue = false
                    iDays++
                }
            }
        }

        /*
        map.forEach{
            if(it.value){
                stringDays += it.key
            }
            else{
                alwaysTrue = false
                iDays++
            }
        }*/

        obsTextDays.value = if(alwaysTrue)
            "Daily"
        else if(!alwaysTrue && iDays == 7)
            "Never"
        else
            stringDays
    }

    fun onExpandClick(view: View){
        expandVisibility = !expandVisibility

        if(expandVisibility){
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_keyboard_arrow_up_accent_24dp))
            //Log.d("DBG: ", view.parent.toString())
            (view.parent as ConstraintLayout).layoutExpandDays.visibility = View.VISIBLE
        }
        else{
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_keyboard_arrow_down_accent_24dp))
            (view.parent as ConstraintLayout).layoutExpandDays.visibility = View.GONE
        }
    }
}