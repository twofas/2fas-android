@file:OptIn(KoinInternalApi::class)

package com.twofasapp.di.extensions

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.module.KoinDefinition
import org.koin.core.module._scopedInstanceFactory
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.setupInstance
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

inline fun <reified R> ScopeDSL.scopedOf(
    crossinline constructor: () -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R> ScopeDSL.scopedOf(
    crossinline constructor: () -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1> ScopeDSL.scopedOf(
    crossinline constructor: (T1) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1> ScopeDSL.scopedOf(
    crossinline constructor: (T1) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see scopedOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): KoinDefinition<R> = scoped { new(constructor) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified T23> ScopeDSL.scopedOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_scopedInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22, reified T23> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
