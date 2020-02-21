package com.graytsar.idleclickercompanion

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface AlarmDAO {
    @Insert
    fun insertAppAlarm(alarm:AlarmModel):Long

    @Update(onConflict = REPLACE)
    fun updateAppAlarm(alarm:AlarmModel)

    @Delete
    fun deleteAppAlarm(alarm: AlarmModel)

    @Query("SELECT * FROM Alarm WHERE idListAlarm = :key")
    fun getAllAppAlarm(key: Long):Array<AlarmModel>

    @Query("SELECT * FROM Alarm WHERE idAlarm = :key")
    fun dbg(key: Long):Array<AlarmModel>
}