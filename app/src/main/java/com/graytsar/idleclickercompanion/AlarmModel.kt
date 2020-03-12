package com.graytsar.idleclickercompanion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


@Entity(tableName = "Alarm",
    foreignKeys = [ForeignKey(entity = AppModel::class, parentColumns = arrayOf("idApp"), childColumns = arrayOf("idListAlarm"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)],
    indices = [Index(value = ["idListAlarm"])]
)
class AlarmModel(
    @PrimaryKey(autoGenerate = true) var idAlarm:Long,
    @ColumnInfo(name = "idListAlarm") var idListApp:Long,
    @ColumnInfo(name = "appName")var applicationLabel:String,
    @ColumnInfo(name = "appPath")var packageName:String,
    @ColumnInfo(name = "hour") var selectedHour:Int,
    @ColumnInfo(name = "minute") var selectedMinute:Int,
    @ColumnInfo(name = "action") var selectedAction:String,
    @ColumnInfo(name = "fireAlarmIn") var fireAlarmIn:Long,
    @ColumnInfo(name = "startAlarm") var startAlarm:MutableLiveData<Boolean>?,
    @ColumnInfo(name = "selectedDaysAr") var selectedDaysAr:Array<Boolean>,
    @ColumnInfo(name = "position") var position: Int) {

    @Ignore var obsTextDays:MutableLiveData<String> = MutableLiveData()
    @Ignore var obsTimeLeft:MutableLiveData<String> = MutableLiveData()

    @Ignore private var expandVisibility:Boolean = false

    @Ignore private val minuteInMs:Long = 60000
    @Ignore private val secondInMs:Long = 1000

    constructor():this(0,0,"","",1,0,"",0,null, arrayOf(true,true,true,true,true,true,true), 0)

    init{
        obsTextDays.postValue("Daily")
        obsTimeLeft.postValue("${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}:00")

        setupDayString()
    }

    //only case when called when not in lifecycle
    fun activate(context:Context, _start:Boolean):Boolean{
        var start = _start

        val calendar = Calendar.getInstance()
        when (calendar[Calendar.DAY_OF_WEEK]) {
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

        SingletonStatic.db?.alarmDao()?.updateAlarm(this)

        return start
    }

    private fun setAlarm(context:Context, start:Boolean){
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        if(start){
            startAlarm!!.value = true
            fireAlarmIn = System.currentTimeMillis() + selectedMinute * minuteInMs + selectedHour * 3600000
            intent.putExtra("time", fireAlarmIn)
            intent.putExtra("packageName", packageName)
            intent.putExtra("action", selectedAction)
            intent.putExtra("applicationLabel", applicationLabel)
            intent.putExtra("idAlarm", idAlarm)
            intent.putExtra("idApp", idListApp)
            val alarmIntent:PendingIntent = PendingIntent.getBroadcast(context, idAlarm.toInt() + 100000 * idListApp.toInt(), intent, PendingIntent.FLAG_ONE_SHOT)


            if(Build.VERSION.SDK_INT < 23 ){
                alarmManager.apply { set(AlarmManager.RTC_WAKEUP, fireAlarmIn, alarmIntent) }
            } else {
                alarmManager.apply { setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireAlarmIn, alarmIntent) }
            }

            GlobalScope.launch {
                SingletonStatic.db?.alarmDao()?.updateAlarm(this@AlarmModel)
            }

            launchCountdown()
        } else if (!start) {
            val alarmIntent:PendingIntent = PendingIntent.getBroadcast(context, idAlarm.toInt() + 100000 * idListApp.toInt(), intent, 0)
            alarmManager.cancel(alarmIntent)
            fireAlarmIn = 0
            startAlarm!!.value = false

            obsTimeLeft.value = "${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}:00"
        }

    }

    fun onClickStartAlarm(view:View){
        val b = startAlarm!!.value!!
        startAlarm!!.value = !b

        if(startAlarm!!.value!!){
            setAlarm(view.context, true)
        } else {
            setAlarm(view.context, false)
        }

        //SingletonStatic.db.alarmDao().updateAlarm(this)
    }

    fun launchCountdown(){
        GlobalScope.launch {
            var ms = fireAlarmIn - System.currentTimeMillis()

            while(ms > 0){
                delay(secondInMs)
                ms = fireAlarmIn - System.currentTimeMillis()

                val hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(ms),
                TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)))

                obsTimeLeft.postValue(hms)
            }
            startAlarm!!.postValue(false)

            obsTimeLeft.postValue("${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}:00")
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

        //SingletonStatic.db.alarmDao().updateAlarm(this)
    }

    fun onExpandClick(view: View){
        expandVisibility = !expandVisibility

        if(expandVisibility){
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_keyboard_arrow_up_black_24dp))
            //Log.d("DBG: ", view.parent.toString())
            (view.parent as ConstraintLayout).listExpandDays.visibility = View.VISIBLE
        }
        else{
            (view as ImageView).setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_keyboard_arrow_down_black_24dp))
            (view.parent as ConstraintLayout).listExpandDays.visibility = View.GONE
        }
    }

    private fun setupDayString(){
        var stringDays = ""
        var iFalseDays = 0
        var alwaysTrue = true

        val c = SingletonStatic.activity!!.applicationContext

        for(i in 0..6){
            if(selectedDaysAr[i]){
                when (i) {
                    0 -> {
                        stringDays += c.getString(R.string.mo)
                    }
                    1 -> {
                        stringDays += c.getString(R.string.tu)
                    }
                    2 -> {
                        stringDays += c.getString(R.string.we)
                    }
                    3 -> {
                        stringDays += c.getString(R.string.th)
                    }
                    4 -> {
                        stringDays += c.getString(R.string.fr)
                    }
                    5 -> {
                        stringDays += c.getString(R.string.sa)
                    }
                    6 -> {
                        stringDays += c.getString(R.string.so)
                    }
                }
            } else {
                alwaysTrue = false
                iFalseDays++
            }
        }

        val str = if(alwaysTrue)
            c.getString(R.string.daily)
        else if(!alwaysTrue && iFalseDays == 7)
            c.getString(R.string.never)
        else
            stringDays

        try{
            obsTextDays.value = str
        } catch (e:Exception){
            obsTextDays.postValue(str)
        }

    }
}