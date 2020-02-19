package com.graytsar.idleclickercompanion

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.item_game_alert.view.*
import java.text.SimpleDateFormat
import java.util.*

class AppAlarmModel(var context:FragmentActivity, val appName:String, val appPath:String, val hour:Int, val minute:Int, val action:String, val repeat:Int): JobService() {
    var hourLeft = hour
    var minuteLeft = minute
    var repeatLeft:Int = repeat

    var repeatLeftObs:MutableLiveData<String> = MutableLiveData()
    var textDays:MutableLiveData<String> = MutableLiveData()
    var timeLeft:MutableLiveData<String> = MutableLiveData()
    var startAlarm:MutableLiveData<Boolean> = MutableLiveData()

    private var expandVisibility:Boolean = false

    var map = mutableMapOf(
        context.resources.getString(R.string.monday) to true,
        context.resources.getString(R.string.tuesday) to true,
        context.resources.getString(R.string.wednesday) to true,
        context.resources.getString(R.string.thursday) to true,
        context.resources.getString(R.string.friday) to true,
        context.resources.getString(R.string.saturday) to true,
        context.resources.getString(R.string.sunday) to true)

    init{
        repeatLeftObs.value = "$repeatLeft Times"
        textDays.value = "Daily"
        timeLeft.value = "${String.format("%02d",hour)}:${String.format("%02d",minute)}"
        startAlarm.value = false

        var mMS = minute * 600000
        var hMS = hour * 3600000

        var endT = System.currentTimeMillis() + mMS + hMS

        var calendar:Calendar = Calendar.getInstance()
        calendar.timeInMillis = endT

        var a = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").apply {
            timeZone = calendar.timeZone
        }
        Log.d("DBG: ", " "  + a.format(calendar.time))
    }

    fun onClickStartAlarm(view:View){
        /*val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(
            JobInfo.Builder(1, ComponentName(context, AppAlarmModel::class.java))
                .setPeriodic(10000)
                .build()
        )*/

        val a = object:CountDownTimer(10000, 1000) {
            override fun onFinish() {
                timeLeft.value = "${String.format("%02d",hour)}:${String.format("%02d",minute)}"
                hourLeft = hour
                minuteLeft = minute

                if(repeatLeft == 0){
                    repeatLeft = repeat
                } else {
                    repeatLeft--
                }

                repeatLeftObs.value = "$repeatLeft Times"
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

                timeLeft.value = "${String.format("%02d",hourLeft)}:${String.format("%02d",minuteLeft)}"
            }
        }.start()
    }


    private fun pushNotify(){
        SingletonNotify.pushNotify(appPath, appName, action)
    }

    fun onDayClick(view:View){
        when(view){
            view.buttonMonday ->{
                map.put(context.resources.getString(R.string.monday), !map.getValue(context.resources.getString(R.string.monday)))
            }
            view.buttonTuesday ->{
                map.put(context.resources.getString(R.string.tuesday), !map.getValue(context.resources.getString(R.string.tuesday)))
            }
            view.buttonWednesday ->{
                map.put(context.resources.getString(R.string.wednesday), !map.getValue(context.resources.getString(R.string.wednesday)))

            }
            view.buttonThursday ->{
                map.put(context.resources.getString(R.string.thursday), !map.getValue(context.resources.getString(R.string.thursday)))

            }
            view.buttonFriday ->{
                map.put(context.resources.getString(R.string.friday), !map.getValue(context.resources.getString(R.string.friday)))

            }
            view.buttonSaturday ->{
                map.put(context.resources.getString(R.string.saturday), !map.getValue(context.resources.getString(R.string.saturday)))

            }
            view.buttonSunday ->{
                map.put(context.resources.getString(R.string.sunday), !map.getValue(context.resources.getString(R.string.sunday)))

            }
        }

        var stringDays = ""
        var iDays = 0
        var alwaysTrue = true
        map.forEach{
            if(it.value){
                stringDays += it.key
            }
            else{
                alwaysTrue = false
                iDays++
            }
        }

        textDays.value = if(alwaysTrue)
            "Daily"
        else if(!alwaysTrue && iDays == 7)
            "Never"
        else
            stringDays

        Log.d("DBG: ", textDays.value)
    }

    fun onExpandClick(view: View){
        expandVisibility = !expandVisibility

        if(expandVisibility){
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_accent_24dp))
            //Log.d("DBG: ", view.parent.toString())
            (view.parent as ConstraintLayout).layoutExpandDays.visibility = View.VISIBLE
        }
        else{
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_accent_24dp))
            (view.parent as ConstraintLayout).layoutExpandDays.visibility = View.GONE
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("DBG: ", "Stop Job")
        return false
    }

    //when it is time to execute
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("DBG: ", "Start Job")
        return false
    }
}