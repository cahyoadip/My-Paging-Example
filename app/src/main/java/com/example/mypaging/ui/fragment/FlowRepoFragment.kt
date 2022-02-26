package com.example.mypaging.ui.fragment

import com.example.mypaging.ui.adapter.ReposAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.mypaging.R
import com.example.mypaging.ui.view_model.RepoViewModel
import com.example.mypaging.ui.view_model.RepoViewModelFactory
import com.example.mypaging.ui.adapter.ReposLoadStateAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FlowRepoFragment: Fragment() {

    private val viewModel by viewModels<RepoViewModel> {
        RepoViewModelFactory()
    }
    private val recyclerView: RecyclerView by lazy {
        requireView().findViewById(R.id.list)
    }
    private val emptyList: TextView by lazy {
        requireView().findViewById(R.id.emptyList)
    }
    private val progressBar: ProgressBar by lazy {
        requireView().findViewById(R.id.progress_bar)
    }
    private val retryButton: Button by lazy {
        requireView().findViewById(R.id.retry_button)
    }
    private var adapterRepo: ReposAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onDestroyView() {
        adapterRepo = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterRepo = ReposAdapter()
        lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest {
                adapterRepo?.submitData(it)
            }
        }
        lifecycleScope.launch {
            adapterRepo?.loadStateFlow?.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapterRepo?.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                recyclerView.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible = loadState.source.refresh is LoadState.Error
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        val contactAdapter = adapterRepo?.withLoadStateFooter(
            footer = ReposLoadStateAdapter {
                ReposLoadStateAdapter {
                    adapterRepo?.retry()
                }
            }
        )
        recyclerView.adapter = contactAdapter
    }
}