package ae.oleapp.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.databinding.FragmentAlerSideMenuDialogBinding
import ae.oleapp.databinding.SideMenuRecItemBinding
import ae.oleapp.databinding.SubscriptionRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.openActivity
import android.app.Dialog
import android.graphics.Color
import android.view.Window
import androidx.fragment.app.DialogFragment
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable

class AlerSideMenuDialogFragment : DialogFragment() {

    private var _binding: FragmentAlerSideMenuDialogBinding? = null
    private val binding get() = _binding!!

    private val menuList by lazy { ArrayList<GenericModelClass>() }
    private lateinit var adapter: GenericAdapter<GenericModelClass, SideMenuRecItemBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlerSideMenuDialogBinding.inflate(inflater, container, false)
        binding.backBtn.setOnClickListener {
            dismiss()
        }
        setupAdapters()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // Request window features before the dialog is shown
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }

    }


    private fun setupAdapters() {
        menuList.clear()
        menuList.add(GenericModelClass(
            title = "Rank",
            icon = 0))
        menuList.add(GenericModelClass(
            title = "Membership Plans",
            icon = 0, screen = SubscriptionActivity::class.java))
        menuList.add(GenericModelClass(
            title = "Settings",
            icon = 0,
            screen = DataUserRegisterActivity::class.java))
        menuList.add(GenericModelClass(
            title = "Live Chat",
            icon = 0))
        menuList.add(GenericModelClass(
            title = "Terms & Conditions",
            icon = 0))
        menuList.add(GenericModelClass(
            title = "Privacy Policy",
            icon = 0))
        menuList.add(GenericModelClass(
            title = "Delete Account",
            icon = 0))

        adapter = GenericAdapter(
            items = menuList,
            bindingInflater = SideMenuRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTV.text = item.title
            binding.root.setOnClickListener {
                item.screen?.let { requireContext().openActivity(it) }
                dismiss()
            }
        }
        binding.rec.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}