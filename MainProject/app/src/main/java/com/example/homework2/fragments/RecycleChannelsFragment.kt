package com.example.homework2.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Constance
import com.example.homework2.viewmodels.ChannelViewModel
import com.example.homework2.R
import com.example.homework2.customviews.dpToPx
import com.example.homework2.databinding.FragmentRecycleChannelsBinding
import com.example.homework2.dataclasses.ResultChannel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class RecycleChannelsFragment : Fragment() {

    lateinit var binding: FragmentRecycleChannelsBinding
    private var recycleAdapter = ChannelRecycleViewAdapter() {
        openFrag(ChatFragment.newInstance(), ChatFragment.TAG)
    }

    private val compositeDisposable = CompositeDisposable()

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
        val shimmer = binding.channelShimmer
        val isSubscribed = requireArguments().getBoolean(Constance.ALL_OR_SUBSCRIBED_KEY)

        //Не нужно фильтровать пустую строку для возврата исходного результата
        val searchDisposable = (parentFragment as ChannelFragment).searchObservable
            .debounce(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .subscribe {
                when (isSubscribed) {
                    true -> {
                        viewModel.onSearchChangedSubscribedChannel(it)
                    }
                    false -> {
                        viewModel.onSearchChangedAllChannel(it)
                    }
                }
            }
        compositeDisposable.add(searchDisposable)


        when (isSubscribed) {
            true -> {
                val subscribedChannelsDisposable = viewModel.subscribedChannelsObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            is ResultChannel.Success -> {
                                recycleAdapter.updateList(it.channelList)
                                shimmer.hideShimmer()
                            }
                            is ResultChannel.Progress -> {
                                shimmer.showShimmer(true)
                            }
                            is ResultChannel.Error -> {
                                Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG)
                                    .show()
                                shimmer.hideShimmer()
                            }
                        }
                    }
                compositeDisposable.add(subscribedChannelsDisposable)
            }
            else -> {
                val allChannelsDisposable = viewModel.allChannelsObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            is ResultChannel.Success -> {
                                recycleAdapter.updateList(it.channelList)
                                shimmer.hideShimmer()
                            }
                            is ResultChannel.Error -> {
                                Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG)
                                    .show()
                                shimmer.hideShimmer()
                            }
                            is ResultChannel.Progress -> {
                                shimmer.showShimmer(true)
                            }
                        }
                    }
                compositeDisposable.add(allChannelsDisposable)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
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
