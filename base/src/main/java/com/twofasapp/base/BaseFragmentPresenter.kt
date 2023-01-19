package com.twofasapp.base

import android.content.ComponentCallbacks
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.base.lifecycle.PresenterLifecycle
import org.koin.android.ext.android.get
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier

abstract class BaseFragmentPresenter<Binding : ViewBinding> : ScopeFragment() {

    private var _viewBinding: Binding? = null
    protected val viewBinding: Binding get() = _viewBinding!!

    @Suppress("UNUSED_PARAMETER")
    protected fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding,
    ): View {
        logLifecycle("onCreateView", LifecycleOperation.IN)
        _viewBinding = bindingInflater(inflater, container, false)
        return viewBinding.root
    }

    protected fun setPresenter(presenter: BasePresenter) {
        viewLifecycleOwner.lifecycle.addObserver(PresenterLifecycle(javaClass.simpleName, presenter))
        attachAuthLifecycle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated", LifecycleOperation.IN)
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
        logLifecycle("onDestroyView", LifecycleOperation.OUT)
        _viewBinding = null
        super.onDestroyView()
    }

    private fun attachAuthLifecycle() {
        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = null,
                authAware = this as? AuthAware
            )
        )
    }

    inline fun <reified T : Any> ComponentCallbacks.injectThis(
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    ) = lazy(mode) { get<T>(qualifier, { parametersOf(requireActivity(), this) }) }
}
