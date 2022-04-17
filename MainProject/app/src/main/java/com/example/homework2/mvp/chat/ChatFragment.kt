package com.example.homework2.mvp.chat

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.*
import com.example.homework2.customviews.CustomBottomSheetDialog
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.databinding.FragmentChatBinding
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseFragment
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime

class ChatFragment : BaseFragment<ChatPresenter, FragmentChatBinding>(), ChatView {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var topic: Topic
    private lateinit var stream: Stream
    private lateinit var shimmer: ShimmerFrameLayout

    private var messageId = -1L
    private val compositeDisposable = CompositeDisposable()

    private var dateTime: DateTime = DateTime()
    private var bottomSheetDialog: CustomBottomSheetDialog? = null

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

//    override fun onStart() {
//        super.onStart()
//        viewModel.startObserveChat(requireActivity().zulipApp().retrofitService, stream.name, topic.name)
//    }

//    override fun onStop() {
//        super.onStop()
//        viewModel.stopObserveChat()
//    }


//    private fun onEmptyLoad(messageId: Any){
//        if (messageId == 0L){
//            loadTopicMessage(messageId = "oldest", 20)
//        }
//    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatBinding {
        return FragmentChatBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): ChatPresenter {
        return ChatPresenterImpl(
            view = this,
            model = ChatModelImpl(
                database = ZulipDataBase.getInstance(context = requireContext()),
                retrofitService = requireActivity().zulipApp().retrofitService
            )
        )
    }

    override fun initBottomSheetDialog() {
        bottomSheetDialog = CustomBottomSheetDialog(requireContext()) { emojiName, emojiCode ->
            presenter.onEmojiInSheetDialogClick(
                messageId = messageId,
                emojiName = emojiName,
                reactionType = getString(R.string.unicodeEmoji),
            )
            bottomSheetDialog?.hide()
        }

    }

    override fun initClickListenerOnMessageTranslateImage() {
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

    override fun initImageSwitcher() {

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

    override fun initArguments() {
        topic = requireArguments().getParcelable(Constance.TOPIC_KEY)!!
        stream = requireArguments().getParcelable(Constance.STREAM_KEY)!!
        binding.streamName.text = getString(R.string.topiclable, topic.name)
    }

    override fun initRecycleAdapter() {
        messageAdapter = MessageAdapter({ messageId ->
            this.messageId = messageId
            bottomSheetDialog?.show()
        }, { reaction, isSelected, messageId ->
            presenter.onEmojiInMessageClick(
                messageId = messageId,
                emojiName = reaction.emoji_name,
                reactionType = getString(R.string.unicodeEmoji),
                isSelected = isSelected
            )
        })
        binding.rcView.adapter = messageAdapter
    }

    override fun configureRecycleAdapter() {

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
            addOnPageScrollListener {
                presenter.onMessagesLoadRequested(stream = stream, topic = topic)
            }
            addItemDecoration(itemDivider)
        }
    }

    override fun initShimmer() {
        shimmer = binding.chatShimmer
    }

    override fun showError() {
        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
        shimmer.hideShimmer()
    }

    override fun showProgress() {
        shimmer.showShimmer(true)
    }

    override fun showMessages(messages: List<SelectViewTypeClass.Chat.Message>) {
        //Каст на мутабельность из-за ссылочного типа
        messageAdapter.replaceMessageList(newList = messages.toMutableList())
        shimmer.hideShimmer()
    }


    companion object {
        const val TAG = "ChatFragment"

        fun newInstance(topic: Topic, stream: Stream): ChatFragment {
            val fragment = ChatFragment()
            val argument = Bundle()
            argument.putParcelable(Constance.TOPIC_KEY, topic)
            argument.putParcelable(Constance.STREAM_KEY, stream)
            fragment.arguments = argument
            return fragment
        }
    }
}
