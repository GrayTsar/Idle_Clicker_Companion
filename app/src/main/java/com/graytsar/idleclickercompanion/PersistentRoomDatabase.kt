package com.graytsar.idleclickercompanion

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [AppModel::class, AlarmModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PersistentRoomDatabase: RoomDatabase() {
    abstract fun appDao():AppDAO
    abstract fun alarmDao():AlarmDAO
}