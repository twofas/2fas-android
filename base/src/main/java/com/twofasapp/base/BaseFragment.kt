package com.twofasapp.base

import android.content.ComponentCallbacks
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import org.koin.android.ext.android.get
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier

abstract class BaseFragment(layoutId: Int) : ScopeFragment(layoutId) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        logLifecycle("onCreateView", LifecycleOperation.IN)
        return super.onCreateView(inflater, container, savedInstanceState)
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
        super.onDestroyView()
    }

    inline fun <reified T : Any> ComponentCallbacks.injectThis(
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    ) = lazy(mode) { get<T>(qualifier) { parametersOf(requireActivity(), this) } }

    protected fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
        FragmentViewBindingDelegate(this, viewBindingFactory)
}
