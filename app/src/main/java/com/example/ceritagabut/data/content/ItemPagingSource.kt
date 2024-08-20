package com.example.ceritagabut.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ceritagabut.apis.ApiServices
import com.example.ceritagabut.data.content.contentfunction.ItemPagingFunc
import com.example.ceritagabut.responses.ListResultItems
import com.example.ceritagabut.responses.PersonDatastore


class ItemPagingSource(
    private val apiServicePagingApp: ApiServices,
    private val dataStoreRepositoryApp: PersonDatastore
) : PagingSource<Int, ListResultItems>() {

    private val itemPagingFunc = ItemPagingFunc()

    override fun getRefreshKey(state: PagingState<Int, ListResultItems>): Int? =
        itemPagingFunc.getAppKey(state)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListResultItems> =
        itemPagingFunc.loadApp(params, apiServicePagingApp, dataStoreRepositoryApp)
            .mapDataToLoadResult()

    private fun LoadResult<Int, ListResultItems>.mapDataToLoadResult(): LoadResult<Int, ListResultItems> {
        return when (this) {
            is LoadResult.Page -> LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
            is LoadResult.Error -> LoadResult.Error(throwable)
            is LoadResult.Invalid -> TODO()
        }
    }
}