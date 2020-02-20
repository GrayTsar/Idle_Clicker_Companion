package com.graytsar.idleclickercompanion

import androidx.room.*

@Dao
interface AppDAO {

    @Insert
    suspend fun insertAppCard(app:AppModel)

    @Update
    suspend fun updateAppCard(app:AppModel)

    @Delete
    suspend fun deleteAppCard(app: AppModel)

    @Query("SELECT * FROM AppCard WHERE appPath = :path AND userName = :name")
    suspend fun findAppCard(path:String, name:String): Array<AppModel>

    @Query("SELECT * FROM AppCard")
    suspend fun getAll():Array<AppModel>
}