package com.erdi.SecureBox.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

class HomeViewModel : ViewModel() {
    private val mText: MutableLiveData<String> = MutableLiveData()

    init {
        mText.value = "This is home fragment"
    }

    val text: LiveData<String>
        get() = mText
}