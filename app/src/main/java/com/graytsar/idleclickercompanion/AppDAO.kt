package com.graytsar.idleclickercompanion

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDAO {

    @Insert
    fun insertApp(app:AppModel):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateApp(app:AppModel)

    @Delete
    fun deleteApp(app: AppModel)

    @Query("SELECT * FROM AppCard WHERE idApp = :key ORDER BY position ASC")
    fun findApp(key: Long): Array<AppModel>

    @Query("SELECT * FROM AppCard")
    fun getAllApp(): LiveData<List<AppModel>>
}