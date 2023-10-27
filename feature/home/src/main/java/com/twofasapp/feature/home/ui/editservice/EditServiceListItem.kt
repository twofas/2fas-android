package com.twofasapp.feature.home.ui.editservice

import com.twofasapp.designsystem.lazy.ListItem

sealed class EditServiceListItem(override val key: Any, override val type: Any) : ListItem {
    data object HeaderInfo : EditServiceListItem("HeaderInfo", "Header")
    data object HeaderPersonalization : EditServiceListItem("HeaderPersonalization", "Header")
    data object HeaderOther : EditServiceListItem("HeaderOther", "Header")
    data object InputName : EditServiceListItem("InputName", "InputName")
    data object InputSecret : EditServiceListItem("InputSecret", "InputSecret")
    data object InputInfo : EditServiceListItem("InputInfo", "InputInfo")
    data object Advanced : EditServiceListItem("Advanced", "Link")
    data object IconSelector : EditServiceListItem("IconSelector", "IconSelector")
    data object ChangeBrand : EditServiceListItem("ChangeBrand", "Link")
    data object EditLabel : EditServiceListItem("EditLabel", "Link")
    data object BadgeColor : EditServiceListItem("BadgeColor", "BadgeColor")
    data object Group : EditServiceListItem("Group", "Group")
    data object BrowserExtension : EditServiceListItem("BrowserExtension", "Link")
    data object Delete : EditServiceListItem("Delete", "Link")
}