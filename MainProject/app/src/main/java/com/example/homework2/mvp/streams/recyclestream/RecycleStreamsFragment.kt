package com.example.homework2.mvp.streams.recyclestream

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.databinding.FragmentRecycleChannelsBinding
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dpToPx
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.mvp.chat.ChatFragment
import com.example.homework2.mvp.streams.StreamFragment
import com.example.homework2.zulipApp
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class RecycleStreamsFragment :
    BaseFragment<RecycleStreamPresenter, FragmentRecycleChannelsBinding>(), RecycleStreamView {

    private lateinit var recycleAdapter: StreamRecycleViewAdapter
    private lateinit var shimmer: ShimmerFrameLayout

    private var isSubscribed = false
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        isSubscribed = getIsSubscribedBoolean()
        return binding.root
    }

    private fun getIsSubscribedBoolean(): Boolean {
        return requireArguments().getBoolean(Constance.ALL_OR_SUBSCRIBED_KEY)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecycleChannelsBinding {
        return FragmentRecycleChannelsBinding.inflate(layoutInflater, container, false)
    }

    override fun initPresenter(): RecycleStreamPresenter =
        RecycleStreamPresenterImpl(
            this,
            RecycleStreamsModelImpl(
                ZulipDataBase.getInstance(requireContext()),
                requireActivity().zulipApp().retrofitService
            )
        )

    override fun initRecycleAdapter() {
        fun openFrag(fragment: Fragment, tag: String? = null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_holder, fragment, tag)
                .addToBackStack(null)
                .commit()
        }

        val itemDivider = object : RecyclerView.ItemDecoration() {
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

        recycleAdapter = StreamRecycleViewAdapter { topic, stream ->
            openFrag(ChatFragment.newInstance(topic, stream), ChatFragment.TAG)
        }
        binding.recycleChannel.adapter = recycleAdapter
        binding.recycleChannel.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.recycleChannel.addItemDecoration(itemDivider)
    }

    override fun initShimmer() {
        shimmer = binding.channelShimmer
    }

    override fun initSearchTextListener() {

        val textSubject = PublishSubject.create<String>()

        val disposable = textSubject
            .debounce(1, TimeUnit.SECONDS)
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (isSubscribed) {
                    true -> {
                        presenter.onSearchedTextChangedSubscribedStreams(text = it.toString())
                    }
                    false -> {
                        presenter.onSearchedTextChangedAllStreams(text = it.toString())
                    }
                }
            }
        compositeDisposable.add(disposable)
        (parentFragment as StreamFragment).binding.searchStreamsEditText.addTextChangedListener { text: Editable? ->
            if (text.toString().isNotBlank()) {
                textSubject.onNext(text.toString())
            }
        }
    }

    override fun loadStreamsFromZulip() {
        when (isSubscribed) {
            true -> {
                presenter.onSubscribedStreamsNeeded()
            }
            false -> {
                presenter.onAllStreamsNeeded()
            }
        }
    }

    override fun showProgress() {
        shimmer.showShimmer(true)
    }

    override fun showError() {
        shimmer.hideShimmer()
    }

    override fun showStreams(streamList: List<Stream>) {
        val sortedList = streamList.sortedBy { it.name }
        recycleAdapter.updateList(list = sortedList)
        shimmer.hideShimmer()
    }

    companion object {

        fun newInstance(isSubscribed: Boolean): RecycleStreamsFragment {
            val fragment = RecycleStreamsFragment()
            val arguments = Bundle()
            arguments.putBoolean(Constance.ALL_OR_SUBSCRIBED_KEY, isSubscribed)
            fragment.arguments = arguments

            return fragment
        }
    }
}
