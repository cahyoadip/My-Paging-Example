package com.example.mypaging.ui.fragment

import com.example.mypaging.ui.adapter.ReposAdapter
import android.content.Context
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
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.mypaging.R
import com.example.mypaging.ui.view_model.RepoViewModel
import com.example.mypaging.ui.view_model.RepoViewModelFactory
import com.example.mypaging.ui.adapter.ReposLoadStateAdapter
import kotlinx.coroutines.launch

class LivedataRepoFragment: Fragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getFlowRepoToLiveData()
    }

    override fun onDestroyView() {
        adapterRepo = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterRepo = ReposAdapter().also {
            it.addLoadStateListener(getAdapterLoadStateListener(view.context))
        }
        viewModel.pagingData.observe(viewLifecycleOwner) { data ->
            lifecycleScope.launch {
                data.let { adapterRepo?.submitData(it) }
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

    private fun getAdapterLoadStateListener(
        context: Context
    ): (CombinedLoadStates) -> Unit = { loadState ->
        val isRefreshStateNotLoading = loadState.refresh is LoadState.NotLoading
        val isAppendStateNotLoading = loadState.append is LoadState.NotLoading
        val itemCount = adapterRepo?.itemCount
        val shouldShowEmptyState = isRefreshStateNotLoading && itemCount == 0
        val shouldShowContent =
            isRefreshStateNotLoading && isAppendStateNotLoading && (itemCount ?: 0) > 0
        when {
            shouldShowEmptyState -> {
                progressBar.isVisible = false
                retryButton.isVisible = false
                recyclerView.isVisible = false
                emptyList.isVisible = !shouldShowContent
            }
            shouldShowContent -> {
                progressBar.isVisible = false
                retryButton.isVisible = false
                emptyList.isVisible = false
                recyclerView.isVisible = shouldShowContent
            }
            loadState.refresh is LoadState.Loading -> {
                progressBar.isVisible = true
                retryButton.isVisible = false
                recyclerView.isVisible = false
                emptyList.isVisible = false
            }
            loadState.refresh is LoadState.Error -> {
                retryButton.isVisible = true
                progressBar.isVisible = false
                recyclerView.isVisible = false
                emptyList.isVisible = false
            }
            loadState.append is LoadState.Error -> {
                val errorState = loadState.append as LoadState.Error
                Toast.makeText(
                    context,
                    "\uD83D\uDE28 Wooops ${errorState.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}