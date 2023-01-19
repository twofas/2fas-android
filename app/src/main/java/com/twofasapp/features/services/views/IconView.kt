package com.twofasapp.features.services.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.twofasapp.databinding.ViewIconBinding
import com.twofasapp.extensions.loadAsset
import com.twofasapp.extensions.setBackgroundTint
import com.twofasapp.extensions.visible
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.isNight
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.model.toColor

class IconView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = ViewIconBinding.inflate(LayoutInflater.from(context), this)

    fun updateIcon(service: ServiceDto) {
        with(binding) {

            if (service.selectedImageType == ServiceDto.ImageType.Label) {
                labelText.text = service.labelText
                labelIcon.setBackgroundTint(service.labelBackgroundColor.toColor(context))
            } else {
                icon.loadAsset(ServiceIcons.getIcon(service.iconCollectionId, isDark = context.isNight()))
            }

            icon.visible(service.selectedImageType != ServiceDto.ImageType.Label)
            label.visible(service.selectedImageType == ServiceDto.ImageType.Label)
        }
    }
}