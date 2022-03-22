package com.example.homework2.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Constance
import com.example.homework2.viewmodels.ChannelViewModel
import com.example.homework2.R
import com.example.homework2.customviews.dpToPx
import com.example.homework2.databinding.FragmentRecycleChannelsBinding

class RecycleChannelsFragment : Fragment() {

    lateinit var binding: FragmentRecycleChannelsBinding
    private var recycleAdapter = ChannelRecycleViewAdapter() {
        openFrag(ChatFragment.newInstance(), ChatFragment.TAG)
    }

    private var itemDivider = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.apply {
                top += requireContext().dpToPx(2)
                bottom += requireContext().dpToPx(2)
            }
        }
    }

    private val viewModel: ChannelViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecycleChannelsBinding.inflate(inflater)
        binding.recycleChannel.adapter = recycleAdapter
        binding.recycleChannel.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.recycleChannel.addItemDecoration(itemDivider)

        when (requireArguments().getBoolean(Constance.ALL_OR_SUBSCRIBED_KEY)) {
            true -> {
                viewModel.subscribedList.observe(viewLifecycleOwner){
                    recycleAdapter.updateList(it)
                }
            }
            else -> {
                viewModel.allChannelList.observe(viewLifecycleOwner) {
                    recycleAdapter.updateList(it)
                }
            }
        }
        return binding.root
    }

    private fun openFrag(fragment: Fragment, tag: String? = null) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    companion object {

        fun newInstance(isSubscribed: Boolean): RecycleChannelsFragment {

            val fragment = RecycleChannelsFragment()
            val arguments = Bundle()
            arguments.putBoolean(Constance.ALL_OR_SUBSCRIBED_KEY, isSubscribed)
            fragment.arguments = arguments

            return fragment
        }
    }
}
