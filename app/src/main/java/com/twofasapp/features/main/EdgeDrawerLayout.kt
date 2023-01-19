package com.twofasapp.features.main

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.WindowManager
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import kotlin.math.max

class EdgeDrawerLayout : DrawerLayout {

    private val swipeEdgeSize by lazy {
        val displaySize = Point()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(displaySize)
        (displaySize.x * 0.2).toInt()
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val leftDraggerField = DrawerLayout::class.java.getDeclaredField("mLeftDragger")
        leftDraggerField.isAccessible = true
        val viewDragHelper = leftDraggerField[this] as ViewDragHelper

        val edgeSizeField = ViewDragHelper::class.java.getDeclaredField("mEdgeSize")
        edgeSizeField.isAccessible = true
        val origEdgeSize = edgeSizeField[viewDragHelper] as Int
        edgeSizeField.setInt(viewDragHelper, max(swipeEdgeSize, origEdgeSize))
    }
}