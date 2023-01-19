package com.twofasapp.usecases.services

import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

typealias IsSearchEnabled = Boolean
typealias SearchQuery = String

class SearchStateObserver {

    var isSearchEnabled: IsSearchEnabled = false
        private set(value) {
            field = value
        }

    private val processor = PublishProcessor.create<IsSearchEnabled>()
    private val processorQuery = PublishProcessor.create<SearchQuery>()

    fun observeEnabled(): Flowable<IsSearchEnabled> = processor.share()
    fun observeQuery(): Flowable<SearchQuery> = processorQuery.share()
    fun offer(isSearchEnabled: IsSearchEnabled): Boolean {
        this.isSearchEnabled = isSearchEnabled
        return processor.offer(isSearchEnabled)
    }

    fun offer(query: SearchQuery) = processorQuery.offer(query)
}