package com.graytsar.idleclickercompanion

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = arrayOf(AppModel::class, AlarmModel::class), version = 1)
abstract class PersistentRoomDatabase: RoomDatabase() {
    abstract fun appCardDao():AppDAO
    abstract fun appAlarmDao():AlarmDAO
}