package com.infinity.modelviewerapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.infinity.modelviewerapp.repository.repository

class ViewModelProviderFactory ( val repository: repository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  SharedViewModel(repository) as T
    }
}
