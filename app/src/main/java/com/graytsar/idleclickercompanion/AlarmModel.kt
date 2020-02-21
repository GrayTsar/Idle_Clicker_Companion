package com.graytsar.idleclickercompanion

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import kotlinx.android.synthetic.main.item_game_alert.view.*
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
    @ColumnInfo(name = "hourLeft") var hourLeft:Int,
    @ColumnInfo(name = "minuteLeft") var minuteLeft:Int,
    @ColumnInfo(name = "repeatLeft") var repeatLeft:Int,
    @ColumnInfo(name = "startAlarm") var startAlarm:Boolean,
    @ColumnInfo(name = "selectedDaysAr") var selectedDaysAr:Array<Boolean>) {

    @Ignore var obsRepeatLeft:MutableLiveData<Int> = MutableLiveData()
    @Ignore var obsTextDays:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsTimeLeft:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsStartAlarm:MutableLiveData<Boolean> = MutableLiveData()

    @Ignore private var expandVisibility:Boolean = false

    @Ignore private var countDownTimer:CountDownTimer? = null

    constructor():this(0,0,"","",1,0,0,"",0,0,0,false, arrayOf(true,true,true,true,true,true,true))

    init{
        obsRepeatLeft.value = repeatLeft
        obsTextDays.value = "Daily"
        obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
        obsStartAlarm.value = startAlarm

        /*val mMS = selectedMinute * 600000
        val hMS = selectedHour * 3600000

        val endT = System.currentTimeMillis() + mMS + hMS

        val calendar:Calendar = Calendar.getInstance()
        calendar.timeInMillis = endT

        val a = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").apply {
            timeZone = calendar.timeZone
        }
        Log.d("DBG: ", " "  + a.format(calendar.time))*/

        setupDayString()
    }

    fun onClickStartAlarm(view:View){
        startAlarm = !startAlarm
        obsStartAlarm.value = startAlarm

        if(startAlarm){
            countDownTimer?.cancel()
            //selectedMinute * 600000L + selectedHour * 3600000L, 60000
            countDownTimer = object:CountDownTimer(10000, 1000) {
                override fun onFinish() {
                    obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
                    hourLeft = selectedHour
                    minuteLeft = selectedMinute

                    if(repeatLeft == 0){
                        repeatLeft = selectedRepeat
                    } else {
                        repeatLeft--
                    }

                    obsRepeatLeft.value = repeatLeft
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

                    obsTimeLeft.value = "${String.format("%02d",hourLeft)}:${String.format("%02d",minuteLeft)}"
                }
            }.start()

        } else {
            countDownTimer?.cancel()
        }

        obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
        hourLeft = selectedHour
        minuteLeft = selectedMinute
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