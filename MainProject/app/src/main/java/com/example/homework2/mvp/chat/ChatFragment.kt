package com.example.homework2.mvp.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.*
import com.example.homework2.Errors.*
import com.example.homework2.customviews.ChangeMessageTopicBottomSheetDialog
import com.example.homework2.customviews.CustomReactionBottomSheetDialog
import com.example.homework2.customviews.EditMessageCustomBottomSheetDialog
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

    private var reactionReactionBottomSheetDialog: CustomReactionBottomSheetDialog? = null
    private var messageBottomSheetDialog: MessageCustomBottomSheetDialog? = null
    private var editMessageBottomSheetDialog: EditMessageCustomBottomSheetDialog? = null
    private var changeTopicBottomSheetBottomSheetDialog: ChangeMessageTopicBottomSheetDialog? = null

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
        initEditMessageSheetDialog()
        initChangeTopicDialog()
        initImageSwitcher()
        initArguments()
        setVisibilityComponents(isStreamChat)
        initRecycleAdapter()
        configureRecycleAdapter()
        initShimmer()
        setClickListenerOnMessageTranslateImage()
        initMessageRequest(isStreamChat)
        initHelpRecycleAdapter()
        initHelpListener()
        setCLickListenerOnCancelHelpBtn()
        setInTopicClickListener()
        initInTopicImageSwitcher()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy(isStreamChat = isStreamChat, stream = stream, topic = topic)
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
            INTERNET -> getString(R.string.internetError)
            SYSTEM -> getString(R.string.systemError)
            BACKEND -> getString(R.string.backend_error)
        }

        Log.e(Constants.LogTag.MESSAGES_AND_REACTIONS, messageText, throwable)
        Toast.makeText(requireContext(), messageText, Toast.LENGTH_SHORT).show()
        shimmer.hideShimmer()
    }

    override fun showMessages(messages: List<SelectViewTypeClass.Message>) {
        //???????? ???? ?????????????????????????? ????-???? ???????????????????? ????????
        messageAdapter.replaceMessageList(
            newList = messages.toMutableList().sortedBy { it.timestamp })
        shimmer.hideShimmer()
    }

    override fun changeHelpVisibility(visibility: Int) {
        if (visibility == View.VISIBLE) helpAdapter.setTopicsList(stream.topicList)
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
        stream.topicList = topicList.toMutableList()
    }

    override fun clearMessageField() {
        binding.messageField.setText("")
    }

    override fun showReactionDialog(message: SelectViewTypeClass.Message) {
        reactionReactionBottomSheetDialog?.show()
        reactionReactionBottomSheetDialog?.setMessage(message = message)
    }

    override fun hideReactionDialog() {
        reactionReactionBottomSheetDialog?.hide()
    }

    override fun showMessageBottomDialog(message: SelectViewTypeClass.Message) {
        messageBottomSheetDialog?.show()
        messageBottomSheetDialog?.setMessage(message)
    }

    override fun hideMessageBottomDialog() {
        messageBottomSheetDialog?.hide()
    }

    override fun showEditMessageDialog(message: SelectViewTypeClass.Message) {
        with(editMessageBottomSheetDialog) {
            this?.setMessage(seatedMessage = message)
            this?.show()
        }
    }

    override fun hideEditMessageDialog() {
        editMessageBottomSheetDialog?.hide()
    }

    override fun showChangeTopicDialog(message: SelectViewTypeClass.Message, stream: Stream) {
        with(changeTopicBottomSheetBottomSheetDialog) {
            this?.setTopicList(stream.topicList)
            this?.setMessage(message_ = message)
            this?.show()
        }

    }

    override fun hideChangeTopicDialog() {
        changeTopicBottomSheetBottomSheetDialog?.hide()
    }

    private fun initMessageBottomSheetDialog() {
        messageBottomSheetDialog =
            MessageCustomBottomSheetDialog(
                context = requireContext(),
                onAddReactionClick = { message -> presenter.onAddReactionMessageClick(message = message) },
                onDeleteMessageClick = { message -> presenter.onDeleteMessageClick(message = message) },
                onEditMessageClick = { message -> presenter.onEditMessageClick(message = message) },
                onMoveMessageClick = { message ->
                    presenter.onChangeTopicClick(message = message, stream = stream)
                },
                onCopyMessageClick = { message ->
                    val clipData = ClipData.newPlainText("label", message.content)
                    val clipBoard =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipBoard.setPrimaryClip(clipData)
                })

    }

    private fun initEditMessageSheetDialog() {
        editMessageBottomSheetDialog = EditMessageCustomBottomSheetDialog(
            context = requireContext(),
            onApplyClick = { message ->
                presenter.onApplyEditMessageClick(
                    message = message,
                    isStreamChat = isStreamChat,
                    stream = stream
                )
            },
            onCancelClick = { presenter.onCancelEditMessageClick() })
    }

    private fun initReactionBottomSheetDialog() {
        reactionReactionBottomSheetDialog =
            CustomReactionBottomSheetDialog(requireContext()) { emojiName, message ->
                presenter.onEmojiInSheetDialogClick(
                    message = message,
                    emojiName = emojiName,
                    reactionType = getString(R.string.unicodeEmoji),
                )
                reactionReactionBottomSheetDialog?.hide()
            }

    }

    private fun setClickListenerOnMessageTranslateImage() {
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

    private fun setInTopicClickListener() {
        binding.inTopicImage.setOnClickListener {
            var isExist = false
            stream.topicList.forEach { topic ->
                if (topic.name == binding.topicField.text.toString()) isExist = true
            }

            if (isExist) {
                presenter.onInTopicCLick(
                    createTopic(binding.topicField.text.toString()),
                    stream = stream
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.topic_is_not_exist),
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.shape_divider)!!)

        helpAdapter = TopicsHelpAdapter { topicItem ->
            presenter.onHelpTopicItemCLick(topic = topicItem)
        }
        with(binding.help.helpRecycle) {
            adapter = helpAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(divider)
        }
        setTopicListInStream(stream.topicList)
    }

    private fun initHelpListener() {
        binding.topicField.setOnFocusChangeListener { _, hasFocus ->
            presenter.onFocusChanged(hasFocus)
        }
    }

    private fun initArguments() {
        topic = requireArguments().getParcelable(Constants.TOPIC_KEY)!!
        stream = requireArguments().getParcelable(Constants.STREAM_KEY)!!
        isStreamChat = topic.name == Constants.NOT_EXIST_TOPIC
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
            presenter.onAddInMessageClick(message = message)
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
                    presenter.onStreamMessageNextPageLoadRequest(stream = stream)
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

    private fun initChangeTopicDialog() {
        changeTopicBottomSheetBottomSheetDialog =
            ChangeMessageTopicBottomSheetDialog(requireContext()) { message ->
                presenter.onApplyChangeTopicForMessage(
                    message = message, isStreamChat = isStreamChat, stream = stream
                )
            }
    }

    companion object {
        const val TAG = "ChatFragment"

        fun newInstance(topic: Topic, stream: Stream): ChatFragment {
            return ChatFragment().apply {
                arguments = bundleOf(Constants.TOPIC_KEY to topic, Constants.STREAM_KEY to stream)
            }
        }
    }
}
