package com.kontvip.list.data

import com.kontvip.common.data.cache.BooksDao
import com.kontvip.common.data.cache.model.CacheBook
import com.kontvip.common.data.cloud.BooksApi
import com.kontvip.common.data.cloud.model.CloudBook
import com.kontvip.list.domain.core.ListRepository
import com.kontvip.list.domain.model.DomainListBook
import com.kontvip.list.domain.model.ListResult
import java.lang.Exception
import java.net.UnknownHostException

class DefaultListRepository(
    private val dao: BooksDao,
    private val api: BooksApi,
    private val cacheToDomainBookMapper: CacheBook.Mapper<DomainListBook>,
    private val cloudToCacheBookMapper: CloudBook.Mapper<CacheBook>,
    private val exceptionMessageFactory: ExceptionMessageFactory
) : ListRepository {

    override suspend fun fetchBooksFromCloud(): ListResult {
        try {
            val response = api.fetchBooks()
            val code = response.code()
            if (code == 500) {
                return ListResult.Fail(exceptionMessageFactory.map(InternalError()), true)
            }
            val cloudBooks = response.body()!!
            val cloudBooksAsCache = cloudBooks.filter { it.isValid() }.map {
                it.map(cloudToCacheBookMapper)
            }
            dao.insertBooks(cloudBooksAsCache)
            return ListResult.Success(
                cloudBooksAsCache.sortedByDescending { it.dateInMillis }.map { it.map(cacheToDomainBookMapper) }
            )
        } catch (e: UnknownHostException) {
            return ListResult.Fail(exceptionMessageFactory.map(e), true)
        } catch (e: Exception) {
            return ListResult.Fail(exceptionMessageFactory.map(e), false)
        }
    }

    override fun getCachedBooks(): ListResult {
        val cacheBooks = dao.getAllBooks()
        return ListResult.Success(cacheBooks.map { it.map(cacheToDomainBookMapper) })
    }

}