package com.example.homework2.mvp.chat

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.*
import com.example.homework2.customviews.CustomBottomSheetDialog
import com.example.homework2.databinding.FragmentChatBinding
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.di.chatcomponent.ChatComponent
import com.example.homework2.di.chatcomponent.DaggerChatComponent
import com.example.homework2.mvp.BaseFragment
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ChatFragment : BaseFragment<ChatPresenter, FragmentChatBinding>(), ChatView {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var topic: Topic
    private lateinit var stream: Stream
    private lateinit var shimmer: ShimmerFrameLayout

    @Inject
    override lateinit var presenter: ChatPresenter

    private var messageId = -1L
    private val compositeDisposable = CompositeDisposable()

    private var bottomSheetDialog: CustomBottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val chatComponent: ChatComponent =
            DaggerChatComponent.factory().create(requireActivity().zulipApp().zulipComponent)
        chatComponent.inject(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheetDialog()
        initImageSwitcher()
        initArguments()
        initRecycleAdapter()
        configureRecycleAdapter()
        initShimmer()
        initClickListenerOnMessageTranslateImage()
        presenter.onInitMessageRequest(stream = stream, topic = topic)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatBinding {
        return FragmentChatBinding.inflate(inflater, container, false)
    }

    override fun configureActionBar() {
        (activity as MainActivity).supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(true)
            title = "#${stream.name}"
        }
    }

    private fun initBottomSheetDialog() {
        bottomSheetDialog = CustomBottomSheetDialog(requireContext()) { emojiName, emojiCode ->
            presenter.onEmojiInSheetDialogClick(
                messageId = messageId,
                emojiName = emojiName,
                reactionType = getString(R.string.unicodeEmoji),
            )
            bottomSheetDialog?.hide()
        }

    }

    private fun initClickListenerOnMessageTranslateImage() {
        binding.apply {
            messageTranslateImage.setOnClickListener {
                presenter.onSendMessageRequest(
                    sentText = messageField.text.toString(),
                    topic = topic,
                    stream = stream,
                )
            }
        }
    }

    private fun initImageSwitcher() {

        fun switchImageOnTextChanged(isTextEmpty: Boolean) {
            when (isTextEmpty) {
                true -> {
                    binding.messageTranslateImage.setImageResource(
                        R.drawable.ic_add_circle_no_text
                    )
                    binding.messageTranslateImage.setBackgroundResource(
                        R.drawable.send_message_circle_background_no_text
                    )
                }
                false -> {
                    binding.messageTranslateImage.setImageResource(
                        R.drawable.ic_add_circle_yes_text
                    )
                    binding.messageTranslateImage.setBackgroundResource(
                        R.drawable.send_message_circle_background_text
                    )
                }
            }
        }

        binding.messageField.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                switchImageOnTextChanged(true)
            } else {
                switchImageOnTextChanged(false)
            }
        }
    }

    private fun initArguments() {
        topic = requireArguments().getParcelable(Constance.TOPIC_KEY)!!
        stream = requireArguments().getParcelable(Constance.STREAM_KEY)!!
        binding.streamName.text = getString(R.string.topiclable, topic.name)
    }

    private fun initRecycleAdapter() {
        messageAdapter = MessageAdapter({ messageId ->
            this.messageId = messageId
            bottomSheetDialog?.show()
        }, { reaction, isSelected, messageId ->
            presenter.onEmojiInMessageClick(
                messageId = messageId,
                emojiName = reaction.emojiName,
                reactionType = getString(R.string.unicodeEmoji),
                isSelected = isSelected
            )
        })
        binding.rcView.adapter = messageAdapter
    }

    private fun configureRecycleAdapter() {

        val itemDivider = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.apply {
                    top += requireContext().dpToPx(10)
                    bottom += requireContext().dpToPx(10)
                }
            }
        }

        binding.rcView.apply {
            addOnPageScrollListener({
                presenter.onMessagesNextPageLoadRequested(stream = stream, topic = topic)
            }, {
                presenter.onMessagePreviousPageLoadRequest(stream = stream, topic = topic)
            })
            addItemDecoration(itemDivider)
        }
    }

    private fun initShimmer() {
        shimmer = binding.chatShimmer
    }

    override fun showError(throwable: Throwable, error: Errors) {
        val messageText = when (error) {
            Errors.INTERNET -> getString(R.string.internetError)
            Errors.SYSTEM -> getString(R.string.systemError)
        }
        Log.e(Constance.LogTag.MESSAGES_AND_REACTIONS, messageText, throwable)
        Toast.makeText(requireContext(), messageText, Toast.LENGTH_SHORT).show()
        shimmer.hideShimmer()
    }

    override fun showProgress() {
        shimmer.showShimmer(true)
    }

    override fun showMessages(messages: List<SelectViewTypeClass.Message>) {
        //Каст на мутабельность из-за ссылочного типа
        messageAdapter.replaceMessageList(newList = messages.toMutableList())
        shimmer.hideShimmer()
    }

    companion object {
        const val TAG = "ChatFragment"

        fun newInstance(topic: Topic, stream: Stream): ChatFragment {
            val argument = Bundle()
            argument.putParcelable(Constance.TOPIC_KEY, topic)
            argument.putParcelable(Constance.STREAM_KEY, stream)
            return ChatFragment().apply {
                arguments = argument
            }
        }
    }
}
