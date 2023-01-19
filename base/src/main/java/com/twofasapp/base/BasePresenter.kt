package com.twofasapp.base

import androidx.annotation.CallSuper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

abstract class BasePresenter {

    private val disposables: MutableMap<String, MutableList<Disposable>> = HashMap()

    open fun onViewAttached() = Unit

    open fun onResume() = Unit

    open fun onPause() = Unit

    @CallSuper
    open fun onViewDetached() = clearDisposables()

    protected fun clearDisposables(tag: String? = null) {
        if (tag != null) {
            disposables[tag]?.forEach { disposable -> disposable.dispose() }
            disposables.remove(tag)
        } else {
            disposables.forEach { it.value.forEach { disposable -> disposable.dispose() } }
            disposables.clear()
        }
    }

    @Deprecated("Use safelySubscribe")
    protected fun Disposable.addToDisposables(tag: String = javaClass.simpleName): Disposable = apply {
        if (disposables.contains(tag).not()) {
            disposables[tag] = mutableListOf()
        }

        disposables[tag]?.add(this)
    }

    protected fun <T : Any> Flowable<T>.safelySubscribe(
        tag: String = javaClass.simpleName,
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {},
        onNext: (T) -> Unit = {},
    ) = subscribeBy(onError, onComplete, onNext).addToDisposables(tag)

    protected fun <T : Any> Single<T>.safelySubscribe(
        tag: String = javaClass.simpleName,
        onError: (Throwable) -> Unit = {},
        onSuccess: (T) -> Unit = {},
    ) = subscribeBy(onError, onSuccess).addToDisposables(tag)

    protected fun <T : Any> Maybe<T>.safelySubscribe(
        tag: String = javaClass.simpleName,
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {},
        onSuccess: (T) -> Unit = {},
    ) = subscribeBy(onError, onComplete, onSuccess).addToDisposables(tag)

    protected fun Completable.safelySubscribe(
        tag: String = javaClass.simpleName,
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {},
    ) = subscribeBy(onError, onComplete).addToDisposables(tag)
}