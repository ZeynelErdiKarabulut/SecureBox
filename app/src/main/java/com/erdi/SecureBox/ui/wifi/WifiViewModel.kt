package com.erdi.SecureBox.ui.wifi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.erdi.SecureBox.repos.CredsRepository
import androidx.lifecycle.LiveData
import com.erdi.SecureBox.models.ViyCred

class WifiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CredsRepository
    val allCreds: LiveData<List<ViyCred>>
    val allWifi: LiveData<List<ViyCred>>

    init {
        repository = CredsRepository(application)
        allCreds = repository.allNotes
        allWifi = repository.allWifi
    }

    fun insert(viyCred: ViyCred?) {
        repository.insert(viyCred)
    }

    fun update(viyCred: ViyCred?) {
        repository.update(viyCred)
    }

    fun delete(viyCred: ViyCred?) {
        repository.delete(viyCred)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }
}