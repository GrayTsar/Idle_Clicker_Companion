package com.graytsar.idleclickercompanion

import androidx.room.*

@Dao
interface AlarmDAO {
    @Insert
    suspend fun insertAppAlarm(alarm:AlarmModel)

    @Update
    suspend fun updateAppAlarm(alarm:AlarmModel)

    @Delete
    suspend fun deleteAppAlarm(alarm: AlarmModel)

    /*@Query( "SELECT * FROM Alarm WHERE idListAlarm = :key")
    suspend fun findAppAlarm(key: Int)*/
}