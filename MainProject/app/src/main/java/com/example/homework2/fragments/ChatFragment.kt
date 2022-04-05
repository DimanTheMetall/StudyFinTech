package com.example.homework2.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.*
import com.example.homework2.customviews.*
import com.example.homework2.databinding.FragmentChatBinding
import com.example.homework2.dataclasses.Stream
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.Topic
import com.example.homework2.viewmodels.ChatViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime

class ChatFragment : Fragment() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var topic: Topic
    private lateinit var stream: Stream

    private var messageId = -1
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
        _binding = FragmentChatBinding.inflate(inflater)

        messageAdapter = MessageAdapter({ messageId ->
            this.messageId = messageId
            bottomSheetDialog?.show()
        }, { reaction, isSelected, messageId ->
            viewModel.deleteOrAddReaction(
                requireActivity().zulipApp().retrofitService,
                messageId, reaction.emoji_name, getString(R.string.unicodeEmoji), isSelected = isSelected
            )
        })

        topic = requireArguments().getParcelable<Topic>(Constance.TOPIC_KEY)!!
        stream = requireArguments().getParcelable<Stream>(Constance.STREAM_KEY)!!

        val shimmer = binding.chatShimmer
        val chatDisposable = viewModel.chatObservable.subscribe {
            when (it) {
                is SelectViewTypeClass.Success -> {
                    messageAdapter.updateChatList(it.messagesList)
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
                    loadTopicMessage()
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
            streamName.text = "Topic #${topic.name}"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTopicMessage()
    }

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

    private fun loadTopicMessage(){
        viewModel.loadTopicMessage(
            (requireActivity().application as ZulipApp).retrofitService,
            topic.name,
            stream.name
        )
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
