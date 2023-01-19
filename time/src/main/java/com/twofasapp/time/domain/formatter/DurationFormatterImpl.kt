package com.twofasapp.time.domain.formatter

import android.content.Context
import com.twofasapp.time.R
import com.twofasapp.time.domain.TimeProvider
import kotlin.math.abs
import kotlin.math.sign

internal class DurationFormatterImpl(
    private val context: Context,
    private val timeProvider: TimeProvider,
) : DurationFormatter {

    private val timeUnits = TimeUnit.values().reversed()

    override fun formatFromNow(): String {
        return format(timeProvider.systemCurrentTime())
    }

    override fun format(millis: Long): String {
        val diff = timeProvider.systemCurrentTime() - millis
        val diffSign = diff.sign
        val diffAbs = abs(diff)

        timeUnits.forEach { timeUnit ->
            val diffInUnit = diffAbs / timeUnit.millis
            val diffInUnitModulo = diffAbs % timeUnit.millis
            val timeUnitHalf = timeUnit.millis / 2

            if (diffInUnit >= 1 && diffInUnitModulo >= timeUnitHalf) {
                return format(diffInUnit + 1, diffSign, timeUnit)
            }

            if (diffInUnit >= 1 && diffInUnitModulo < timeUnitHalf) {
                return format(diffInUnit, diffSign, timeUnit)
            }
        }

        return format(diff, diffSign, timeUnits.last())
    }

    private fun format(quantity: Long, sign: Int, timeUnit: TimeUnit): String {
        return context.resources.getQuantityString(
            if (sign > 0) timeUnit.pastStringRes else timeUnit.pastStringRes, // TODO: Handle future values
            quantity.toInt(),
            quantity.toInt(),
        )
    }

    enum class TimeUnit(val millis: Long, val pastStringRes: Int) {
        Second(1_000L, R.plurals.past_duration_seconds),
        Minute(60 * 1_000L, R.plurals.past_duration_minutes),
        Hour(60 * 60 * 1_000L, R.plurals.past_duration_hours),
        Day(24 * 60 * 60 * 1_000L, R.plurals.past_duration_days),
        Week(7 * 24 * 60 * 60 * 1_000L, R.plurals.past_duration_weeks),
        Month(4 * 7 * 24 * 60 * 60 * 1_000L, R.plurals.past_duration_months),
    }
}