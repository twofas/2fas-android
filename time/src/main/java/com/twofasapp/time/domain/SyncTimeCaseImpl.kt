package com.twofasapp.time.domain

class SyncTimeCaseImpl(
    private val recalculateTimeDeltaCase: RecalculateTimeDeltaCase
) : SyncTimeCase {

    override suspend fun invoke() {
        // TODO: Refactor
//        Single.fromCallable {
//            listOf(
//                "time.google.com",
//                "time1.google.com",
//                "time.apple.com",
//                "pool.ntp.org",
//                "0.pool.ntp.org",
//                "1.pool.ntp.org",
//                "2.pool.ntp.org",
//            ).random()
//        }
//            .flatMap { ntpServer ->
//                TrueTimeRx.build()
//                    .initializeRx(ntpServer)
//                    .subscribeOn(Schedulers.io())
//            }
//            .retry(5)
//            .await()
//            .let {
//                Timber.d("TrueTime: $it")
//                recalculateTimeDeltaCase.invoke()
//            }
    }
}