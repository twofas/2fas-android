/*
 * Copyright 2021 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.burnoutcrew.reorderable

fun <T> MutableList<T>.move(fromIdx: Int, toIdx: Int) {
    when {
        fromIdx == toIdx -> {
            return
        }
        toIdx > fromIdx -> {
            for (i in fromIdx until toIdx) {
                this[i] = this[i + 1].also { this[i + 1] = this[i] }
            }
        }
        else -> {
            for (i in fromIdx downTo toIdx + 1) {
                this[i] = this[i - 1].also { this[i - 1] = this[i] }
            }
        }
    }
}