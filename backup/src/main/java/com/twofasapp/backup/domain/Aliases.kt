package com.twofasapp.backup.domain

@JvmInline
value class Password(val value: String)

@JvmInline
value class KeyEncoded(val value: String)

@JvmInline
value class DataEncoded(val value: String)

@JvmInline
value class SaltEncoded(val value: String)

@JvmInline
value class IvEncoded(val value: String)