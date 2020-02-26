package com.graytsar.idleclickercompanion

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDAO {
    @Insert
    fun insertAlarm(alarm:AlarmModel):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAlarm(alarm:AlarmModel)

    @Delete
    fun deleteAlarm(alarm: AlarmModel)

    @Query("SELECT * FROM Alarm WHERE idListAlarm = :key")
    fun getAllAlarm(key: Long):LiveData<List<AlarmModel>>

    @Query("SELECT * FROM Alarm WHERE idListAlarm = :key")
    fun getAllAlarmSimple(key: Long):Array<AlarmModel>

    @Query("SELECT * FROM Alarm WHERE idAlarm = :key")
    fun findAlarmByIdAlarm(key: Long):Array<AlarmModel>

    @Query("SELECT * FROM Alarm WHERE idListAlarm = :key")
    fun findAlarmByIdApp(key: Long):Array<AlarmModel>

}