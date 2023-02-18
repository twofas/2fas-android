package com.twofasapp.services.navigation

import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.common.navigation.NavNode

object ServiceGraph : NavGraph {
    override val route: String = "service"
}

//internal object NavArg {
//    val ExtensionId = navArgument("id") { type = NavType.StringType }
//}

sealed class ServiceNode(override val path: String) : NavNode {
    override val graph: NavGraph = ServiceGraph

    object Main : ServiceNode("main")
    object DomainAssignment : ServiceNode("domain_assignment")
    object AdvancedSettings : ServiceNode("advanced_settings")
    object ChangeBrand : ServiceNode("change_brand")
    object ChangeLabel : ServiceNode("change_label")
    object RequestIcon : ServiceNode("request_icon")
    object Delete : ServiceNode("delete_service")

//    object PairingProgress : Node("pairing/progress?extensionId={${ExtensionId.name}}")
//    object BrowserDetails : Node("details/{${ExtensionId.name}}")
}