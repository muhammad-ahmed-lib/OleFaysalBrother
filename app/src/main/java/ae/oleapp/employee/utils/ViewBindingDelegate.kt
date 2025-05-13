package ae.oleapp.employee.utils

import android.app.Dialog
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * https://medium.com/@rahmicemreunal/auto-inflated-cleared-view-binding-in-fragments-using-delegation-2ada0b43a8e4
 */

class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (LayoutInflater) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    init {
        val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
            val viewLifecycleOwner = it ?: run {
                /**
                 * this block will run when fragment viewLifecycleOwner is null
                 * If the fragment onDestroyView runs before the view lifecycle is initialized (for example when navigating in onViewCreated),
                 * the binding reference must be cleared here because the viewLifeCycleObserver will not be triggered.
                 */
                binding = null
                return@Observer
            }

            //binding reference set to be null when view life cycle is destroyed
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            })
        }

        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(
                    viewLifecycleOwnerLiveDataObserver
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(
                    viewLifecycleOwnerLiveDataObserver
                )
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = binding
        if (binding != null && binding.root === thisRef.view) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed: ${fragment.javaClass.simpleName}")
        }

        return viewBindingFactory(thisRef.layoutInflater).also { this.binding = it }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (LayoutInflater) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

// In our Activity, we can instead use a lazy delegate that inflates the binding, as we don’t need to worry about a separate view lifecycle.
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(crossinline bindingInflater: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) { bindingInflater.invoke(layoutInflater) }

inline fun <T : ViewBinding> Dialog.viewBinding(crossinline bindingInflater: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) { bindingInflater.invoke(layoutInflater) }
