package com.twofasapp.locale

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
@Immutable
class Strings(c: Context) {
    val placeholder = "Lorem ipsum"
    val placeholderMedium =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    val placeholderLong =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."

    val commonNext = c.getString(R.string.commons__next)
    val commonContinue = c.getString(R.string.commons__continue)
    val commonSkip = c.getString(R.string.commons__skip)
    val commonEdit = c.getString(R.string.commons__edit)
    val commonDelete = c.getString(R.string.commons__delete)
    val commonCancel = c.getString(R.string.commons__cancel)
    val commonSave = c.getString(R.string.commons__save)
    val commonAdd = c.getString(R.string.commons__add)

    val startupTermsLabel = c.getString(R.string.introduction__tos)
    val startupStepOneHeader = c.getString(R.string.introduction__page_1_title)
    val startupStepOneBody = c.getString(R.string.introduction__page_1_content)
    val startupStepTwoHeader = c.getString(R.string.introduction__page_2_title)
    val startupStepTwoBody = c.getString(R.string.introduction__page_2_content)
    val startupStepThreeHeader = c.getString(R.string.introduction__page_3_title)
    val startupStepThreeBody = c.getString(R.string.introduction__page_3_content)
    val startupStepFourHeader = c.getString(R.string.introduction__page_4_title)
    val startupStepFourBody = c.getString(R.string.introduction__page_4_content_android)
    val startupStartCta = c.getString(R.string.introduction__title)

    val servicesEmptyBody = c.getString(R.string.introduction__description_title)
    val servicesEmptyImportCta = c.getString(R.string.introduction__import_external_app)
    val servicesEmptyPairServiceCta = c.getString(R.string.introduction__pair_new_service)
    val servicesMyTokens = c.getString(R.string.tokens__my_tokens)
    val servicesManageList = "Manage list"
    val groupsAdd = c.getString(R.string.tokens__add_group)
    val groupsEdit = c.getString(R.string.commons__edit)
    val groupsName = c.getString(R.string.tokens__group_name)

    val externalImportTitle = c.getString(R.string.settings__external_import)
    val externalImportHeader = c.getString(R.string.externalimport_select_app)
    val externalImportGoogleAuthenticator = c.getString(R.string.externalimport_google_authenticator)
    val externalImportAegis = c.getString(R.string.externalimport_aegis)
    val externalImportRaivo = c.getString(R.string.externalimport_raivo)
    val externalImportNotice = c.getString(R.string.externalimport_description)

    val trashTitle = c.getString(R.string.settings__trash)
    val trashEmpty = c.getString(R.string.settings__trash_is_empty)
    val trashRestoreCta = c.getString(R.string.settings__restore)
    val trashDisposeCta = c.getString(R.string.tokens__remove_forever)

    val notificationsTitle = c.getString(R.string.commons__notifications)
    val notificationsEmpty = c.getString(R.string.notifications__no_notifications)

    val aboutTitle = c.getString(R.string.settings__about)
    val aboutGeneral = c.getString(R.string.settings__general)
    val aboutShare = c.getString(R.string.settings__share_app)
    val aboutWriteReview = c.getString(R.string.settings__write_a_review)
    val aboutPrivacyPolicy = c.getString(R.string.settings__privacy_policy)
    val aboutTerms = c.getString(R.string.settings__terms_of_service)
    val aboutLicenses = c.getString(R.string.about_licenses)
    val aboutTellFriend = c.getString(R.string.settings__tell_a_friend)
    val aboutTellFriendShareText = c.getString(R.string.settings__recommendation)

    val settingsBackup = c.getString(R.string.backup__2fas_backup)
    val settingsSecurity = c.getString(R.string.settings__security)
    val settingsAppearance = "Appearance"
    val settingsExternalImport = c.getString(R.string.settings__external_import)
    val settingsBrowserExt = c.getString(R.string.browser__browser_extension)
    val settingsTrash = c.getString(R.string.settings__trash)
    val settingsSupport = c.getString(R.string.settings__support)
    val settingsAbout = c.getString(R.string.settings__about)
    val settingsDonate = c.getString(R.string.settings__donate_twofas)

    val browserExtTitle = c.getString(R.string.browser__browser_extension)
    val browserExtHeader = c.getString(R.string.browser__info_title)
    val browserExtBody1 = c.getString(R.string.browser__info_description_first)
    val browserExtBody2 = c.getString(R.string.browser__info_description_second)
    val browserExtMore1 = c.getString(R.string.browser__more_info)
    val browserExtMore2 = c.getString(R.string.browser__more_info_link_title)
    val browserExtCta = c.getString(R.string.browser__pair_with_web_browser)
    val browserExtAddNew = c.getString(R.string.browser__add_new)
    val browserExtPairedDevices = c.getString(R.string.browser__paired_devices_browser_title)
    val browserExtDeviceName = c.getString(R.string.browser__this_device_name)
    val browserExtDeviceNameSubtitle = c.getString(R.string.browser__this_device_footer)

    val settingsTheme = c.getString(R.string.settings__option_theme)
    val settingsShowNextCode = c.getString(R.string.settings__show_next_token)
    val settingsShowNextCodeBody = "Show next token when current one is about to expire."
    val settingsAutoFocusSearch = c.getString(R.string.appearance__toggle_active_search)
    val settingsAutoFocusSearchBody = c.getString(R.string.appearance__active_search_description)
    val settingsServicesStyle = "List style"

//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
//    val browserExt = c.getString(R.string.)
}