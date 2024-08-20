package com.example.ceritagabut.data.content.contentfunction

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ceritagabut.apis.ApiServices
import com.example.ceritagabut.responses.ListResultItems
import com.example.ceritagabut.responses.PersonDatastore
import kotlinx.coroutines.flow.first

class ItemPagingFunc {

    fun getAppKey(state: PagingState<Int, ListResultItems>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    suspend fun loadApp(
        params: PagingSource.LoadParams<Int>,
        apiServicePagingApp: ApiServices,
        dataStoreRepositoryApp: PersonDatastore
    ): PagingSource.LoadResult<Int, ListResultItems> {
        val position = params.key ?: INITIAL_PAGE_INDEX

        return try {
            val token = dataStoreRepositoryApp.getPerson().first()

            val userToken = "Bearer ${token.usertoken}"

            val responseData =
                apiServicePagingApp.getListItems(userToken, position, params.loadSize)

            PagingSource.LoadResult.Page(
                data = responseData.listItems,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listItems.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}
