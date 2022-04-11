package com.example.homework2.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.*
import com.example.homework2.customviews.CustomBottomSheetDialog
import com.example.homework2.customviews.addOnPageScrollListener
import com.example.homework2.customviews.dpToPx
import com.example.homework2.databinding.FragmentChatBinding
import com.example.homework2.dataclasses.Stream
import com.example.homework2.dataclasses.Topic
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.viewmodels.ChatViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime

class ChatFragment : Fragment() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var topic: Topic
    private lateinit var stream: Stream

    private var messageId = -1L
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private var dateTime: DateTime = DateTime()
    private val compositeDisposable = CompositeDisposable()
    private val viewModel: ChatViewModel by viewModels()
    private var bottomSheetDialog: CustomBottomSheetDialog? = null

    private val itemDivider = object : RecyclerView.ItemDecoration() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        topic = requireArguments().getParcelable(Constance.TOPIC_KEY)!!
        stream = requireArguments().getParcelable(Constance.STREAM_KEY)!!

        _binding = FragmentChatBinding.inflate(inflater)

        viewModel.initDateBase(requireContext(), topic = topic, stream = stream)

        binding.rcView.addOnPageScrollListener {
            loadTopicMessage(
                currentLastMessageId = viewModel.messageList.lastOrNull()?.id ?: 0L
            )
        }

        messageAdapter = MessageAdapter({ messageId ->
            this.messageId = messageId
            bottomSheetDialog?.show()
        }, { reaction, isSelected, messageId ->
            viewModel.deleteOrAddReaction(
                retrofitService = requireActivity().zulipApp().retrofitService,
                messageId = messageId,
                emojiName = reaction.emoji_name,
                reactionType = getString(R.string.unicodeEmoji),
                isSelected = isSelected
            )
        }, { mutableList ->
            onUpdateList(mutableList as MutableList<SelectViewTypeClass.Chat.Message>)
        })


        val shimmer = binding.chatShimmer
        val chatDisposable = viewModel.chatObservable.subscribe {
            when (it) {
                is SelectViewTypeClass.Success -> {
                    messageAdapter.addMessagesToList(it.messagesList)
                    messageAdapter.setIsLast(false)
                    shimmer.hideShimmer()
                }
                is SelectViewTypeClass.Error -> {
                    Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
                    shimmer.hideShimmer()
                }
                is SelectViewTypeClass.Progress -> {
                    shimmer.showShimmer(true)
                }
                is SelectViewTypeClass.UploadSuccess -> {
                    loadTopicMessage(currentLastMessageId = "oldest", numAfter = 1, numBefore = 1)
                }

                is SelectViewTypeClass.SuccessLast -> {
                    messageAdapter.addMessagesToList(it.messagesList)
                    messageAdapter.setIsLast(true)
                    shimmer.hideShimmer()
                }

            }
        }

        bottomSheetDialog = CustomBottomSheetDialog(requireContext()) { emojiName, emojiCode ->
            viewModel.uploadNewReaction(
                requireActivity().zulipApp().retrofitService,
                messageId,
                emojiName,
                getString(R.string.unicodeEmoji),
                emojiCode
            )
            bottomSheetDialog?.hide()
        }

        binding.apply {
            rcView.adapter = messageAdapter
            streamName.text = getString(R.string.topiclable, topic.name)
            rcView.addItemDecoration(itemDivider)

            messageTranslateImage.setOnClickListener {
                viewModel.sendMessage(
                    messageField.text.toString(),
                    topic,
                    stream,
                    requireActivity().zulipApp().retrofitService
                )
            }

            messageField.doOnTextChanged { text, start, before, count ->
                if (text.isNullOrEmpty()) {
                    switchImageOnTextChanged(true)
                } else {
                    switchImageOnTextChanged(false)
                }
            }
        }
        compositeDisposable.add(chatDisposable)

        return binding.root
    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.startObserveChat(requireActivity().zulipApp().retrofitService, stream.name, topic.name)
//    }

//    override fun onStop() {
//        super.onStop()
//        viewModel.stopObserveChat()
//    }

    private fun switchImageOnTextChanged(isTextEmpty: Boolean) {
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

//    private fun onEmptyLoad(messageId: Any){
//        if (messageId == 0L){
//            loadTopicMessage(messageId = "oldest", 20)
//        }
//    }

    private fun loadTopicMessage(
        currentLastMessageId: Any,
        numAfter: Int = 20,
        numBefore: Int = 0
    ) {
        viewModel.loadTopicMessage(
            retrofitService = (requireActivity().application as ZulipApp).retrofitService,
            topic = topic.name,
            stream = stream.name,
            lastMessageId = currentLastMessageId,
            numAfter = numAfter,
            numBefore = numBefore
        )
    }

    private fun onUpdateList(list: MutableList<SelectViewTypeClass.Chat.Message>) {
        val oldestOnSaveMessageId = if (list.lastIndex <= 50) 0L else list[list.lastIndex - 50].id

        //Сохрание сообщений в БД
//        viewModel.dataBase.getMessagesAndReactionDao()
//            .insertMessagesFromTopic(list.map { MessageEntity.toEntity(message = it) })


//        list.forEach { message ->
//            viewModel.dataBase.getMessagesAndReactionDao()
//                .insertAllReactionOnMessages(message.reactions.map {
//                    ReactionEntity.toEntity(
//                        reaction = it,
//                        messageId = message.id
//                    )
//                })
//        }

        //Удаление сообщений из БД

//        viewModel.dataBase.getMessagesAndReactionDao()
//            .deleteOldestMessages(oldestMessagedItAfterDeleted = oldestOnSaveMessageId)
//        viewModel.dataBase.getMessagesAndReactionDao()
//            .deleteReactionFromMessagesWhereIdLowes(oldestOnSaveMessageId)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
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
