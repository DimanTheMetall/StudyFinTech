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
import com.example.homework2.StreamRecycleViewAdapter
import com.example.homework2.Constance
import com.example.homework2.viewmodels.StreamViewModel
import com.example.homework2.R
import com.example.homework2.ZulipApp
import com.example.homework2.customviews.dpToPx
import com.example.homework2.databinding.FragmentRecycleChannelsBinding
import com.example.homework2.dataclasses.ResultStream
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class RecycleChannelsFragment : Fragment() {

    private var _binding: FragmentRecycleChannelsBinding? = null
    private val binding get() = _binding!!

    private var recycleAdapter = StreamRecycleViewAdapter {
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

    private val viewModel: StreamViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecycleChannelsBinding.inflate(inflater)
        binding.recycleChannel.adapter = recycleAdapter
        binding.recycleChannel.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.recycleChannel.addItemDecoration(itemDivider)
        val shimmer = binding.channelShimmer
        val isSubscribed = requireArguments().getBoolean(Constance.ALL_OR_SUBSCRIBED_KEY)

        binding.textButtom.setOnClickListener {
//            viewModel.loadAllStreams((requireActivity().application as ZulipApp).retrofitService)
            viewModel.loadSubscribedStreams((requireActivity().application as ZulipApp).retrofitService)
        }

        //Не нужно фильтровать пустую строку для возврата исходного результата
        val searchDisposable = (parentFragment as StreamFragment).searchObservable
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

        fun updateChannelResult(result: ResultStream){
            when(result){
                is ResultStream.Success -> {
                    recycleAdapter.updateList(result.streamList)
                    shimmer.hideShimmer()
                }
                is ResultStream.Error -> {
                    shimmer.hideShimmer()
                }
                is ResultStream.Progress -> {
                    Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG)
                        .show()
                    shimmer.showShimmer(true)
                }
            }
        }

        when (isSubscribed) {
            true -> {
                val subscribedChannelsDisposable = viewModel.subscribedChannelsObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        updateChannelResult(it)
                    }
                compositeDisposable.add(subscribedChannelsDisposable)
            }
            false -> {
                val allChannelsDisposable = viewModel.allChannelsObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        updateChannelResult(it)
                    }
                compositeDisposable.add(allChannelsDisposable)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
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
