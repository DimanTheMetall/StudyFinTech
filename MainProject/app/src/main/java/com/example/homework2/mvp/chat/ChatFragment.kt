package com.example.homework2.mvp.chat

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.*
import com.example.homework2.customviews.CustomBottomSheetDialog
import com.example.homework2.customviews.MessageCustomBottomSheetDialog
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
    private lateinit var helpAdapter: TopicsHelpAdapter
    private lateinit var shimmer: ShimmerFrameLayout

    private lateinit var topic: Topic
    private lateinit var stream: Stream

    @Inject
    override lateinit var presenter: ChatPresenter

    private var messageId = -1L
    private val compositeDisposable = CompositeDisposable()

    private var reactionBottomSheetDialog: CustomBottomSheetDialog? = null
    private var messageBottomSheetDialog: MessageCustomBottomSheetDialog? = null

    private var isStreamChat = false

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
        initReactionBottomSheetDialog()
        initMessageBottomSheetDialog()
        initImageSwitcher()
        initArguments()
        setVisibilityComponents(isStreamChat)
        initRecycleAdapter()
        configureRecycleAdapter()
        initShimmer()
        initClickListenerOnMessageTranslateImage()
        initMessageRequest(isStreamChat)
        initHelpRecycleAdapter()
        initHelpListener()
        setCLickListenerOnCancelHelpBtn()
        initInTopicClickListener()
        initInTopicImageSwitcher()

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

    override fun showProgress() {
        shimmer.showShimmer(true)
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

    override fun showMessages(messages: List<SelectViewTypeClass.Message>) {
        //Каст на мутабельность из-за ссылочного типа
        messageAdapter.replaceMessageList(newList = messages.toMutableList())
        shimmer.hideShimmer()
    }

    override fun changeHelpVisibility(visibility: Int) {
        binding.help.root.visibility = visibility
    }

    override fun fillTopicField(topic: Topic) {
        binding.topicField.setText(topic.name)
    }

    override fun openFrag(fragment: Fragment, tag: String?) {
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_holder, fragment, tag)
            .commit()
    }

    override fun setTopicListInStream(topicList: List<Topic>) {
        helpAdapter.setTopicsList(topicList)
    }

    override fun showReactionDialog() {
        reactionBottomSheetDialog?.show()
    }

    override fun hideReactionDialog() {
        reactionBottomSheetDialog?.hide()
    }

    override fun showMessageBottomDialog(message: SelectViewTypeClass.Message) {
        messageBottomSheetDialog?.show()
        messageBottomSheetDialog?.setMessage(message)
    }

    override fun hideMessageBottomDialog() {
        messageBottomSheetDialog?.hide()
    }

    private fun initMessageBottomSheetDialog() {
        messageBottomSheetDialog =
            MessageCustomBottomSheetDialog(
                context = requireContext(),
                { message -> presenter.onAddReactionMessageClick(message = message) },
                { message -> presenter.onDeleteMessageClick(message = message) },
                { message -> },
                { message -> },
                { message -> })
    }

    private fun initReactionBottomSheetDialog() {
        reactionBottomSheetDialog =
            CustomBottomSheetDialog(requireContext()) { emojiName, _ ->
                presenter.onEmojiInSheetDialogClick(
                    messageId = messageId,
                    emojiName = emojiName,
                    reactionType = getString(R.string.unicodeEmoji),
                )
                reactionBottomSheetDialog?.hide()
            }

    }

    private fun initClickListenerOnMessageTranslateImage() {
        binding.apply {
            messageTranslateImage.setOnClickListener {
                if (isStreamChat) {
                    if (topicField.text.isNullOrBlank()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_write_topic_name),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        sendMessage(
                            sentText = messageField.text.toString(),
                            topic = createTopic(topicField.text.toString()),
                            stream = stream,
                            isStreamChat = isStreamChat
                        )
                    }
                } else {
                    sendMessage(
                        sentText = messageField.text.toString(),
                        topic = topic,
                        stream = stream,
                        isStreamChat = isStreamChat
                    )
                }
            }
        }
    }

    private fun sendMessage(sentText: String, topic: Topic, stream: Stream, isStreamChat: Boolean) {
        if (sentText.isBlank()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_write_message_text),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            presenter.onSendMessageInTopicRequest(
                sentText = sentText,
                topic = topic,
                stream = stream,
                isStreamChat = isStreamChat
            )
        }
    }

    private fun initInTopicClickListener() {
        binding.inTopicImage.setOnClickListener {
            presenter.onInTopicCLick(
                createTopic(binding.topicField.text.toString()),
                stream = stream
            )
        }
    }

    private fun createTopic(name: String): Topic {
        return Topic(name = name)
    }

    private fun initInTopicImageSwitcher() {
        with(binding) {
            topicField.doOnTextChanged { text, _, _, _ ->
                if (text.isNullOrBlank()) {
                    inTopicImage.visibility = View.GONE
                } else {
                    inTopicImage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initMessageRequest(isStreamChat: Boolean) {
        if (isStreamChat) {
            presenter.onInitMessageForStreamRequest(stream = stream)
        } else {
            presenter.onInitMessageForTopicRequest(stream = stream, topic = topic)
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

        binding.messageField.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                switchImageOnTextChanged(true)
            } else {
                switchImageOnTextChanged(false)
            }
        }
    }

    private fun setVisibilityComponents(isStreamChat: Boolean) {
        if (isStreamChat) {
            with(binding) {
                topicName.visibility = View.GONE
                topicField.visibility = View.VISIBLE
            }
        } else {
            with(binding) {
                topicName.visibility = View.VISIBLE
                topicField.visibility = View.GONE
                binding.topicName.text = getString(R.string.topiclable, topic.name)
            }
        }
    }

    private fun setCLickListenerOnCancelHelpBtn() {
        binding.help.closeHelpBtn.setOnClickListener {
            presenter.onCancelHelpBtnCLick()
        }
    }

    private fun initHelpRecycleAdapter() {
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

        helpAdapter = TopicsHelpAdapter { topicItem ->
            presenter.onHelpTopicItemCLick(topic = topicItem)
        }
        with(binding.help.helpRecycle) {
            adapter = helpAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(itemDivider)
        }
        setTopicListInStream(stream.topicList)
    }

    private fun initHelpListener() {
        binding.topicField.setOnFocusChangeListener { _, hasFocus ->
            presenter.onFocusChanged(hasFocus)
        }
    }

    private fun initArguments() {
        topic = requireArguments().getParcelable(Constance.TOPIC_KEY)!!
        stream = requireArguments().getParcelable(Constance.STREAM_KEY)!!
        isStreamChat = topic.name == Constance.NONEXISTTOPIC
    }

    private fun initRecycleAdapter() {
        messageAdapter = MessageAdapter({ message ->
            presenter.onMessageLongClick(message = message)
            messageId = message.id
        }, { reaction, isSelected, messageId ->
            presenter.onEmojiInMessageClick(
                messageId = messageId,
                emojiName = reaction.emojiName,
                reactionType = getString(R.string.unicodeEmoji),
                isSelected = isSelected
            )
        }, { message ->
            this.messageId = message.id
            presenter.onAddInMessageClick()
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
                if (isStreamChat) {
                    presenter.onStreamMessageNextPgeLoadRequest(stream = stream)
                } else {
                    presenter.onTopicMessagesNextPageLoadRequested(stream = stream, topic = topic)
                }

            }, {
                if (isStreamChat) {
                    presenter.onStreamMessagePreviousPgeLoadRequest(stream = stream)
                } else {
                    presenter.onTopicMessagePreviousPageLoadRequest(stream = stream, topic = topic)
                }

            })
            addItemDecoration(itemDivider)
        }
    }

    private fun initShimmer() {
        shimmer = binding.chatShimmer
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
