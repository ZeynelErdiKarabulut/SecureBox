package com.erdi.SecureBox.ui.social

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.erdi.SecureBox.repos.CredsRepository
import androidx.lifecycle.LiveData
import com.erdi.SecureBox.models.ViyCred

class SocialViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CredsRepository
    val allCreds: LiveData<List<ViyCred>>? = null
    val allSocial: LiveData<List<ViyCred>>

    init {
        repository = CredsRepository(application)
        allSocial = repository.allSocial
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