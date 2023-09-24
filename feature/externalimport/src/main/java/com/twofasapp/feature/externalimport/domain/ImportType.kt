package com.twofasapp.feature.externalimport.domain

import com.twofasapp.feature.externalimport.R

enum class ImportType {
    GoogleAuthenticator,
    Aegis,
    Raivo,
    LastPass,
    AuthenticatorPro,
    ;
}

val ImportType.image: Int
    get() = when (this) {
        ImportType.GoogleAuthenticator -> R.drawable.ic_import_ga
        ImportType.Aegis -> R.drawable.ic_import_aegis
        ImportType.Raivo -> R.drawable.ic_import_raivo
        ImportType.LastPass -> R.drawable.ic_import_lastpass
        ImportType.AuthenticatorPro -> R.drawable.ic_import_authenticatorpro
    }