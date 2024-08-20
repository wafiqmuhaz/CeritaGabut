package com.example.ceritagabut

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.ceritagabut.adapter.ItemAdapters
import com.example.ceritagabut.adapter.ItemAdapters.Companion.APP_CALLBACK
import com.example.ceritagabut.data.ItemRepository
import com.example.ceritagabut.models.AppMainViewModel
import com.example.ceritagabut.responses.ListResultItems
import com.example.ceritagabut.utils.DataDummy
import com.example.ceritagabut.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

class ItemResultPagingSource : PagingSource<Int, LiveData<List<ListResultItems>>>() {
    companion object {
        fun snapshot(items: List<ListResultItems>): PagingData<ListResultItems> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListResultItems>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListResultItems>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainRules = MainRule()
    @Mock
    private lateinit var itemRepository: ItemRepository
    private lateinit var mainViewModelApp: AppMainViewModel
    private val dummyItem = DataDummy.generateDummyItemEntity(10)
    private val dummyItemNull = DataDummy.generateDummyItemEntity(0)
    @Before
    fun setUp() {
        mainViewModelApp = AppMainViewModel(itemRepository)
    }


    @Test
    fun `when Get Item Success`() = runTest {
        val data: PagingData<ListResultItems> = ItemResultPagingSource.snapshot(dummyItem)
        val expectedItem = MutableLiveData<PagingData<ListResultItems>>()
        expectedItem.value = data
        Mockito.`when`(itemRepository.getAllItems("")).thenReturn(expectedItem)
        val mainViewModel = AppMainViewModel(itemRepository)
        val actualItem: PagingData<ListResultItems> = mainViewModel.getListItems("").getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = APP_CALLBACK,
            updateCallback = listUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualItem)
        advanceUntilIdle()
        Mockito.verify(itemRepository).getAllItems("")
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyItem.size, differ.snapshot().size)

        // Menambahkan assertion untuk memeriksa data pertama yang dikembalikan
        val firstItem = differ.snapshot().items.firstOrNull()
        Assert.assertNotNull(firstItem)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertNotNull(differ.snapshot().size)
        // Tambahkan assertion sesuai dengan data yang diharapkan
        Assert.assertEquals(dummyItem.first(), firstItem)
    }

    @Test
    fun `when There Are No Stories`() = runTest {
        val expectedItem = MutableLiveData<PagingData<ListResultItems>>()
        expectedItem.value = PagingData.from(dummyItemNull)
        Mockito.`when`(itemRepository.getAllItems("")).thenReturn(expectedItem)
        val actualItem: PagingData<ListResultItems> = mainViewModelApp.getListItems("").getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = APP_CALLBACK,
            updateCallback = listUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualItem)
        Mockito.verify(itemRepository).getAllItems("")
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }

}

val listUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

@ExperimentalCoroutinesApi
class MainRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}