package com.graytsar.idleclickercompanion

import androidx.room.*

@Dao
interface AppDAO {

    @Insert
    fun insertAppCard(app:AppModel):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAppCard(app:AppModel)

    @Delete
    fun deleteAppCard(app: AppModel)

    @Query("SELECT * FROM AppCard WHERE appPath = :path AND userName = :name")
    fun findAppCard(path:String, name:String): Array<AppModel>

    @Query("SELECT * FROM AppCard")
    fun getAll():Array<AppModel>
}