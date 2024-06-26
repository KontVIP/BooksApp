package com.kontvip.list.domain

import com.kontvip.common.core.DispatcherList
import com.kontvip.list.domain.core.BooksListUiFactory
import com.kontvip.list.domain.core.ListRepository
import com.kontvip.list.domain.core.ListScreenUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

interface FetchBooksUseCase {

    suspend fun invoke(onResultBlock: (ListScreenUiState) -> Unit)

    class Default(
        private val repository: ListRepository,
        private val booksListUiFactory: BooksListUiFactory,
        private val dispatcherList: DispatcherList
    ) : FetchBooksUseCase {

        companion object {
            private const val MAX_REPEAT_COUNT = 3
            private const val DELAY_BETWEEN_REQUESTS = 2000L
        }

        private var areBooksAlreadyFetched: Boolean = false

        override suspend fun invoke(onResultBlock: (ListScreenUiState) -> Unit) {
            if (areBooksAlreadyFetched) return
            withContext(dispatcherList.io()) {
                val cache = booksListUiFactory.construct(repository.getCachedBooks())
                val canCacheBeDisplayed = cache.canBeDisplayed()
                if (canCacheBeDisplayed) {
                    onResultBlock.invoke(cache)
                }
                var result = repository.fetchBooksFromCloud()
                var repeatCount = 0
                while (repeatCount < MAX_REPEAT_COUNT && result.shouldRequestAgain()) {
                    delay(DELAY_BETWEEN_REQUESTS)
                    result = repository.fetchBooksFromCloud()
                    repeatCount++
                }
                val cloud = booksListUiFactory.construct(result)
                if (cloud.isFail() && canCacheBeDisplayed) {
                    return@withContext
                }
                onResultBlock.invoke(cloud)
                areBooksAlreadyFetched = result.isSuccessful()
            }
        }
    }

}

