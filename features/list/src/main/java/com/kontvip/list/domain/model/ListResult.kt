package com.kontvip.list.domain.model

sealed interface ListResult {

    fun shouldRequestAgain(): Boolean
    fun <T> map(successMapper: Success.Mapper<T>, failMapper: Fail.Mapper<T>): T

    class Success(private val books: List<DomainListBook>) : ListResult {
        override fun shouldRequestAgain(): Boolean = false

        override fun <T> map(successMapper: Mapper<T>, failMapper: Fail.Mapper<T>): T {
            return successMapper.map(books)
        }

        interface Mapper<T> {
            fun map(books: List<DomainListBook>): T
        }
    }

    class Fail(
        private val errorMessage: String,
        private val shouldRequestAgain: Boolean
    ) : ListResult {
        override fun shouldRequestAgain(): Boolean = shouldRequestAgain

        override fun <T> map(successMapper: Success.Mapper<T>, failMapper: Mapper<T>): T {
            return failMapper.map(errorMessage)
        }

        interface Mapper<T> {
            fun map(errorMessage: String): T
        }
    }

}