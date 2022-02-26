package com.example.mypaging.ui.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mypaging.ui.adapter.view_holder.ReposLoadStateViewHolder

class ReposLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ReposLoadStateViewHolder {
        Log.e("cahyo2", loadState.toString())
        return ReposLoadStateViewHolder.create(parent, retry)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {
        Log.e("cahyo2", loadState.toString())
        (holder as ReposLoadStateViewHolder).bind(loadState)
    }
}
