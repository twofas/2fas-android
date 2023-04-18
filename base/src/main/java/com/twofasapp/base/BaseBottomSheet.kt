package com.twofasapp.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twofasapp.resources.R
import org.koin.android.scope.createScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

abstract class BaseBottomSheet<Binding : ViewBinding> : BottomSheetDialogFragment(), KoinScopeComponent {

    override val scope: Scope by lazy { createScope(this) }

    private var currentBinding: Binding? = null
    protected val binding: Binding get() = currentBinding!!
    private lateinit var presenter: BasePresenter

    @Suppress("UNUSED_PARAMETER")
    protected fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding,
    ): View {
        logLifecycle("onCreateView", LifecycleOperation.IN)
        currentBinding = bindingInflater(inflater, container, false)
        return binding.root
    }

    protected fun setPresenter(presenter: BasePresenter) {
        this.presenter = presenter
        presenter.onViewAttached()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated", LifecycleOperation.IN)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext()) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val width = context.resources.getDimensionPixelSize(R.dimen.bottom_sheet_width)
                window!!.setLayout(
                    if (width > 0) width else ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume", LifecycleOperation.IN)
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause", LifecycleOperation.OUT)
    }

    override fun onDestroyView() {
        presenter.onViewDetached()
        closeScope()
        currentBinding = null
        logLifecycle("onDestroyView", LifecycleOperation.OUT)
        super.onDestroyView()
    }
}
