package com.graytsar.idleclickercompanion

import androidx.room.*

@Dao
interface AlarmDAO {
    @Insert
    fun insertAppAlarm(alarm:AlarmModel):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAppAlarm(alarm:AlarmModel)

    @Delete
    fun deleteAppAlarm(alarm: AlarmModel)

    @Query("SELECT * FROM Alarm WHERE idListAlarm = :key")
    fun getAllAppAlarm(key: Long):Array<AlarmModel>

    @Query("SELECT * FROM ALARM WHERE idAlarm = :key")
    fun getAlarm(key: Long):Array<AlarmModel>

}