package com.twofasapp.common.navigation

class IllegalNavArgException(argName: String) : RuntimeException("Illegal value for \"${argName}\" argument")
