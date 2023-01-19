package com.twofasapp.navigation

import com.twofasapp.navigation.base.Directions

sealed interface ServiceDirections : Directions {
    object GoBack : ServiceDirections
    object Service : ServiceDirections
    object Advanced : ServiceDirections
    object ChangeBrand : ServiceDirections
    object ChangeLabel : ServiceDirections
    object RequestIcon : ServiceDirections
    object DomainAssignment : ServiceDirections
    object Delete : ServiceDirections
    object Security : ServiceDirections
    object AuthenticateSecret : ServiceDirections
}