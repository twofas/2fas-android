package com.twofasapp.services.ui

import com.twofasapp.designsystem.lazy.ListItem

sealed class EditServiceListItem(override val key: Any, override val type: Any) : ListItem {
    object HeaderInfo : EditServiceListItem("HeaderInfo", "Header")
    object HeaderPersonalization : EditServiceListItem("HeaderPersonalization", "Header")
    object HeaderOther : EditServiceListItem("HeaderOther", "Header")
    object InputName : EditServiceListItem("InputName", "InputName")
    object InputSecret : EditServiceListItem("InputSecret", "InputSecret")
    object InputInfo : EditServiceListItem("InputInfo", "InputInfo")
    object Advanced : EditServiceListItem("Advanced", "Link")
    object IconSelector : EditServiceListItem("IconSelector", "IconSelector")
    object ChangeBrand : EditServiceListItem("ChangeBrand", "Link")
    object EditLabel : EditServiceListItem("EditLabel", "Link")
    object BadgeColor : EditServiceListItem("BadgeColor", "BadgeColor")
    object Group : EditServiceListItem("Group", "Group")
    object BrowserExtension : EditServiceListItem("BrowserExtension", "Link")
    object Delete : EditServiceListItem("Delete", "Link")
}