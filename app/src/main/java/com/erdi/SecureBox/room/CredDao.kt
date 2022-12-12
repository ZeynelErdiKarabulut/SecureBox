package com.erdi.SecureBox.room

import com.erdi.SecureBox.models.ViyCred
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CredDao {
    @Insert
    fun insert(viyCred: ViyCred?)

    @Update
    fun update(viyCred: ViyCred?)

    @Delete
    fun delete(viyCred: ViyCred?)

    @Query("DELETE FROM creds_table")
    fun deleteAllNotes()

    @get:Query("SELECT * FROM creds_table")
    val allCreds: LiveData<List<ViyCred?>?>?

    @get:Query("SELECT * FROM creds_table WHERE provider = 'mail'")
    val allMails: LiveData<List<ViyCred?>?>?

    @get:Query("SELECT * FROM creds_table WHERE provider = 'wifi'")
    val allWifi: LiveData<List<ViyCred?>?>?

    @get:Query("SELECT * FROM creds_table WHERE provider = 'social'")
    val allSocial: LiveData<List<ViyCred?>?>?
}