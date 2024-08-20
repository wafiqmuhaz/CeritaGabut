package com.example.ceritagabut.adapter.classadapters

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.ceritagabut.databinding.LoadingBinding

class LoadingAppStateViewHolder(private val appBinding: LoadingBinding, retry: () -> Unit) :
    RecyclerView.ViewHolder(appBinding.root) {
    init {
        appBinding.retryButtonLoad.setOnClickListener { retry.invoke() }
    }
    fun bindingApp(loadState: LoadState) {
        with(appBinding) {
            progressBarItem.isVisible = loadState is LoadState.Loading
            retryButtonLoad.isVisible = loadState is LoadState.Error
            errorItemLoad.isVisible = loadState is LoadState.Error
            (loadState as? LoadState.Error)?.let { errorItemLoad.text = it.error.localizedMessage }
        }
    }
}