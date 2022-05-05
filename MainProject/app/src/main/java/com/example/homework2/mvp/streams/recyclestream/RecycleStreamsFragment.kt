package com.example.homework2.mvp.streams.recyclestream

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.*
import com.example.homework2.customviews.StreamsCustomBottomSheetDialog
import com.example.homework2.databinding.FragmentRecycleChannelsBinding
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.di.recyclestreamcomponent.DaggerRecycleStreamsComponent
import com.example.homework2.di.recyclestreamcomponent.RecycleStreamsComponent
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.mvp.chat.ChatFragment
import com.example.homework2.mvp.streams.StreamFragment
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecycleStreamsFragment :
    BaseFragment<RecycleStreamPresenter, FragmentRecycleChannelsBinding>(), RecycleStreamView {

    private lateinit var recycleAdapter: StreamRecycleViewAdapter
    private lateinit var shimmer: ShimmerFrameLayout
    private var bottomSheetDialog: StreamsCustomBottomSheetDialog? = null

    @Inject
    override lateinit var presenter: RecycleStreamPresenter

    private var isSubscribed = false
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recycleStreamsComponent: RecycleStreamsComponent =
            DaggerRecycleStreamsComponent.factory()
                .create(requireActivity().zulipApp().zulipComponent)
        recycleStreamsComponent.inject(this)

        super.onCreateView(inflater, container, savedInstanceState)
        isSubscribed = getIsSubscribedBoolean()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleAdapter()
        initShimmer()
        initSearchTextListener()
        loadStreamsFromZulip()
        initCancelCliclListener()
        initBottomSheetDialog()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecycleChannelsBinding {
        return FragmentRecycleChannelsBinding.inflate(layoutInflater, container, false)
    }

    private fun initSearchTextListener() {

        val textSubject = PublishSubject.create<String>()

        val disposable = textSubject
            .debounce(1, TimeUnit.SECONDS)
            .filter { it.isNotBlank() }
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
                (parentFragment as StreamFragment).binding.cancelImage.visibility = View.VISIBLE
                textSubject.onNext(text.toString())
            } else {
                (parentFragment as StreamFragment).binding.cancelImage.visibility = View.INVISIBLE
            }
        }
    }

    override fun showProgress() {
        shimmer.showShimmer(true)
    }

    override fun showError(throwable: Throwable, error: Errors) {
        val textMessage = when (error) {
            Errors.INTERNET -> getString(R.string.internetError)
            Errors.SYSTEM -> getString(R.string.systemError)
        }
        shimmer.hideShimmer()
        Log.e(Constance.LogTag.TOPIC_AND_STREAM, textMessage, throwable)
        Toast.makeText(requireContext(), textMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showStreams(streamList: List<Stream>) {
        val sortedList = streamList.sortedBy { it.name }
        recycleAdapter.updateList(list = sortedList)
        shimmer.hideShimmer()
    }

    override fun showBottomSheetDialog() {
        bottomSheetDialog?.show()
    }

    override fun hideBottomSheetDialog() {
        bottomSheetDialog?.hide()
    }

    private fun loadStreamsFromZulip() {
        when (isSubscribed) {
            true -> {
                presenter.onSubscribedStreamsNeeded()
            }
            false -> {
                presenter.onAllStreamsNeeded()
            }
        }
    }

    private fun initShimmer() {
        shimmer = (parentFragment as StreamFragment).binding.streamsShimmer
    }

    private fun initCancelCliclListener() {
        (parentFragment as StreamFragment).binding.cancelImage.setOnClickListener {
            loadStreamsFromZulip()
            (parentFragment as StreamFragment).binding.searchStreamsEditText.setText("")

        }
    }

    private fun initBottomSheetDialog() {
        bottomSheetDialog = StreamsCustomBottomSheetDialog(
            context = requireContext(),
            { streamName, streamDescription ->

            },
            { })
    }

    private fun getIsSubscribedBoolean(): Boolean {
        return requireArguments().getBoolean(Constance.ALL_OR_SUBSCRIBED_KEY)
    }

    private fun initRecycleAdapter() {
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

        recycleAdapter = StreamRecycleViewAdapter({ topic, stream ->
            openFrag(ChatFragment.newInstance(topic, stream), ChatFragment.TAG)
        }, { presenter.onLastStreamClick() })

        binding.recycleChannel.adapter = recycleAdapter
        binding.recycleChannel.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.recycleChannel.addItemDecoration(itemDivider)
    }

    companion object {

        fun newInstance(isSubscribed: Boolean): RecycleStreamsFragment {
            val arguments = Bundle()
            arguments.putBoolean(Constance.ALL_OR_SUBSCRIBED_KEY, isSubscribed)

            return RecycleStreamsFragment().also {
                it.arguments = arguments
            }
        }
    }
}
