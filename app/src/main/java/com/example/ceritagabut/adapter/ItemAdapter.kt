package com.example.ceritagabut.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.ceritagabut.adapter.classadapters.MyViewHolderItems
import com.example.ceritagabut.databinding.ItemsBinding
import com.example.ceritagabut.responses.ListResultItems

class ItemAdapters :
    PagingDataAdapter<ListResultItems, MyViewHolderItems>(APP_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderItems {
        val appBinding = ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderItems(appBinding)
    }


    override fun onBindViewHolder(holder: MyViewHolderItems, position: Int) {
        val appData = getItem(position)
        appData?.let { holder.bind(it) }
    }

    companion object {
        public val APP_CALLBACK = object : DiffUtil.ItemCallback<ListResultItems>() {
            override fun areItemsTheSame(
                oldItem: ListResultItems,
                newItem: ListResultItems
            ): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: ListResultItems,
                newItem: ListResultItems
            ): Boolean =
                oldItem.id == newItem.id
        }
    }

}