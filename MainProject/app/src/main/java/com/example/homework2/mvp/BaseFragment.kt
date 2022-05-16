package com.example.homework2.mvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.homework2.MainActivity

abstract class BaseFragment<P : BasePresenter, VB : ViewBinding> : Fragment(), BaseView {

    abstract var presenter: P

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttach(this)
    }

    override fun onResume() {
        super.onResume()
        configureActionBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroyView()
        _binding = null
    }

    override fun configureActionBar() {
        (activity as MainActivity).supportActionBar?.hide()
    }

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

}
