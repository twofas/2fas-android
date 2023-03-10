package com.twofasapp.features.services

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.databinding.ItemGroupBinding
import com.twofasapp.entity.GroupModel

class GroupItem(
    model: GroupModel,
    val isInEditMode: Boolean,
    val isInSearchMode: Boolean,
    val isFirst: Boolean,
    val isLast: Boolean,
    private val onToggleClick: (model: GroupModel) -> Unit = {},
    private val onEditClick: (model: GroupModel) -> Unit = {},
    private val onDeleteClick: (model: GroupModel) -> Unit = {},
    private val onMoveUp: (model: GroupModel) -> Unit = {},
    private val onMoveDown: (model: GroupModel) -> Unit = {},
) : ModelAbstractBindingItem<GroupModel, ItemGroupBinding>(model) {

    override var identifier = model.group.id.hashCode().toLong()
    override val type = R.id.item_group
    override var isSelectable = false

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemGroupBinding.inflate(inflater, parent, false)

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: ItemGroupBinding, payloads: List<Any>) {
        with(binding) {
            if (isInEditMode) {
                title.setText("${model.group.name}")
            } else {
                title.setText("${model.group.name} (${model.services.size})")
            }

            toggle.isVisible = isInEditMode.not()
            moveUp.isVisible = isInEditMode && model.group.id.isNullOrBlank().not()
            moveDown.isVisible = isInEditMode && model.group.id.isNullOrBlank().not()
            edit.isVisible = isInEditMode && model.group.id.isNullOrBlank().not()

            moveUp.isEnabled = isFirst.not()
            moveUp.alpha = if (isFirst.not()) 1f else .2f
            moveDown.isEnabled = isLast.not()
            moveDown.alpha = if (isLast.not()) 1f else .2f

            toggle.setImageResource(if (model.group.isExpanded) R.drawable.ic_expanded_old else R.drawable.ic_collapsed_old)
            toggle.isVisible = model.services.isNotEmpty() && isInEditMode.not() && isInSearchMode.not()

            if (model.services.isNotEmpty() && isInEditMode.not() && isInSearchMode.not()) {
                if (root.foreground == null) {
                    root.foreground = root.context.getDrawableFromAttribute(android.R.attr.selectableItemBackground)
                }
                root.setOnClickListener { onToggleClick(model) }
            } else {
                root.foreground = null
                root.setOnClickListener { }
            }

            moveUp.setOnClickListener { onMoveUp(model) }
            moveDown.setOnClickListener { onMoveDown(model) }

            edit.setOnClickListener {
                val popup = PopupMenu(it.context!!, it)
                popup.menuInflater.inflate(com.twofasapp.R.menu.menu_group, popup.menu)
                popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        com.twofasapp.R.id.menu_edit -> onEditClick.invoke(model)
                        com.twofasapp.R.id.menu_delete -> onDeleteClick.invoke(model)
                    }
                    true
                }
                popup.show()
            }
        }
    }

    fun Context.getDrawableFromAttribute(attributeId: Int): Drawable? {
        val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
        return ContextCompat.getDrawable(this, typedValue.resourceId)
    }
}