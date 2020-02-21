package com.graytsar.idleclickercompanion

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = arrayOf(AppModel::class, AlarmModel::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PersistentRoomDatabase: RoomDatabase() {
    abstract fun appCardDao():AppDAO
    abstract fun appAlarmDao():AlarmDAO
}