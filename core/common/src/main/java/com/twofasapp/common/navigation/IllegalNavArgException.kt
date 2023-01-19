package com.twofasapp.common.navigation

import androidx.navigation.NamedNavArgument

@Suppress("NOTHING_TO_INLINE")
inline fun errorNavArg(arg: NamedNavArgument): Nothing = throw IllegalNavArgException(arg)

class IllegalNavArgException(arg: NamedNavArgument) : RuntimeException("Illegal value for \"${arg.name}\" argument")