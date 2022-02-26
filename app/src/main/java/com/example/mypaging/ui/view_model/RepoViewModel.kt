package com.example.mypaging.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.android.codelabs.paging.data.GithubPagingSource
import com.example.android.codelabs.paging.data.NETWORK_PAGE_SIZE
import com.example.mypaging.api.GithubService
import com.example.mypaging.model.UiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RepoViewModel(
    private val service: GithubService
) : ViewModel() {

    private val _pagingData: MutableLiveData<PagingData<UiModel>> = MutableLiveData()
    val pagingData: LiveData<PagingData<UiModel>> get() = _pagingData
    val pagingDataFlow: Flow<PagingData<UiModel>>

    init {
        pagingDataFlow = getFlowRepo().cachedIn(viewModelScope)
    }

    private fun getFlowRepo(): Flow<PagingData<UiModel>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { GithubPagingSource(service) }
        ).flow
            .map { pagingData -> pagingData.map { UiModel.RepoItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                    }
                    // check between 2 items
                    if (before.roundedStarCount > after.roundedStarCount) {
                        if (after.roundedStarCount >= 1) {
                            UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        } else {
                            UiModel.SeparatorItem("< 10.000+ stars")
                        }
                    } else {
                        // no separator
                        null
                    }
                }
            }
    }


    fun getFlowRepoToLiveData() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                pagingSourceFactory = { GithubPagingSource(service) }
            ).flow
                .map { pagingData -> pagingData.map { UiModel.RepoItem(it) } }
                .map {
                    it.insertSeparators { before, after ->
                        if (after == null) {
                            // we're at the end of the list
                            return@insertSeparators null
                        }

                        if (before == null) {
                            // we're at the beginning of the list
                            return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        }
                        // check between 2 items
                        if (before.roundedStarCount > after.roundedStarCount) {
                            if (after.roundedStarCount >= 1) {
                                UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                            } else {
                                UiModel.SeparatorItem("< 10.000+ stars")
                            }
                        } else {
                            // no separator
                            null
                        }
                    }
                }
                .cachedIn(viewModelScope)
                .collect {
                    _pagingData.postValue(it)
                }
        }
    }

    private val UiModel.RepoItem.roundedStarCount: Int
        get() = this.repo.stars / 10_000
}

