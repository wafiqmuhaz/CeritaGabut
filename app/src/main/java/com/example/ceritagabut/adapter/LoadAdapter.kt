package com.example.ceritagabut.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.ceritagabut.adapter.classadapters.LoadingAppStateViewHolder
import com.example.ceritagabut.databinding.LoadingBinding

class LoadingAppStateAdapter(private val load: () -> Unit) : LoadStateAdapter<LoadingAppStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingAppStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val appBinding = LoadingBinding.inflate(inflater, parent, false)
        return LoadingAppStateViewHolder(appBinding, load)
    }
    override fun onBindViewHolder(holder: LoadingAppStateViewHolder, loadState: LoadState) {
        holder.bindingApp(loadState)
    }
}