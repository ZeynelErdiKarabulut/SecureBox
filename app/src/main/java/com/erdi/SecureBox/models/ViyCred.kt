package com.erdi.SecureBox.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creds_table")
class ViyCred(val provider: String, var providerName: String, var email: String, val cat: String) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}