package com.example.mypaging.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mypaging.api.GithubService

class RepoViewModelFactory(
    private val service: GithubService = GithubService.create(),
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RepoViewModel(service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
