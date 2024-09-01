package com.cleanlotos.qyroscope

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class ViewBindingDelegate<T : ViewBinding>(
    val bindingFactory: (View) -> T,
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null
    private val cb: FragmentManager.FragmentLifecycleCallbacks =
        object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                binding = null
                super.onFragmentViewDestroyed(fm, f)
            }
        }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        binding?.let { return it }
        thisRef.parentFragmentManager.registerFragmentLifecycleCallbacks(cb, true)

        val lifecycle = thisRef.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
        }

        return bindingFactory(thisRef.requireView()).also { this.binding = it }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(factory: (View) -> T) =
    ViewBindingDelegate(factory)