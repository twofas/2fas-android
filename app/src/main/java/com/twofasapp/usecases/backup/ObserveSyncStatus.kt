package com.twofasapp.usecases.backup

import com.twofasapp.base.usecase.UseCaseObservable
import com.twofasapp.usecases.backup.model.SyncStatus
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.processors.BehaviorProcessor

class ObserveSyncStatus : UseCaseObservable<SyncStatus> {

    private val processor: BehaviorProcessor<SyncStatus> = BehaviorProcessor.createDefault(SyncStatus.Default)

    override fun observe(subscribeScheduler: Scheduler, observeScheduler: Scheduler): Flowable<SyncStatus> {
        return processor.subscribeOn(subscribeScheduler).observeOn(observeScheduler)
    }

    override fun publish(model: SyncStatus) {
        processor.offer(model)
    }
}
