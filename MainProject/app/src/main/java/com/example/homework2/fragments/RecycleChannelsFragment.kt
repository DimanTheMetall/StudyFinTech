package com.example.homework2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.Channel
import com.example.homework2.ChannelTopic
import com.example.homework2.ChannelViewModel
import com.example.homework2.R
import com.example.homework2.databinding.FragmentRecycleChannelsBinding

class RecycleChannelsFragment : Fragment() {

    lateinit var binding: FragmentRecycleChannelsBinding
    private var recycleAdapter = ChannelRecycleViewAdapter() {
        openFrag(ChatFragment.newInstance(), ChatFragment.TAG)
        println("AAA OpenChat")
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

        viewModel.channelList.observe(viewLifecycleOwner) {
            recycleAdapter.updateList(it)
        }
        binding.textButton.setOnClickListener {
            viewModel.updateData(
                mutableListOf(
                    Channel(
                        "NameChannel",
                        mutableListOf(
                            ChannelTopic("topikName1", 1),
                            ChannelTopic("topikName2", 2)
                        )
                    )
                )
            )
        }
        return binding.root
    }

    private fun openFrag(fragment: Fragment, tag: String? = null) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, fragment, tag)
            .addToBackStack(null)
            .commit()

        println("AAA Open chat fragment")
    }

    companion object {

        fun newInstance(): RecycleChannelsFragment {
            return RecycleChannelsFragment()
        }
    }
}