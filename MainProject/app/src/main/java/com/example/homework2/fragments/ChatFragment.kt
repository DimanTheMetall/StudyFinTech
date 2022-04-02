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
import com.example.homework2.Constance
import com.example.homework2.MessageAdapter
import com.example.homework2.R
import com.example.homework2.ZulipApp
import com.example.homework2.customviews.*
import com.example.homework2.databinding.FragmentChatBinding
import com.example.homework2.dataclasses.Stream
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.Topic
import com.example.homework2.viewmodels.ChatViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime

class ChatFragment() : Fragment() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var topic: Topic
    private lateinit var stream: Stream

    private var position = -1
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

        messageAdapter = MessageAdapter({ position ->
            this.position = position
            bottomSheetDialog?.show()
        }, { position, reaction -> viewModel.updateEmoji(position, reaction) })

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
            }
        }


//        bottomSheetDialog = CustomBottomSheetDialog(requireContext()) { emoji ->
//            viewModel.onEmojiClick(emoji, position)
//            bottomSheetDialog?.hide()
//        }

        binding.apply {
            rcView.adapter = messageAdapter
            streamName.text = "Topik #${topic.name}"
            rcView.addItemDecoration(itemDivider)

            messageTranslateImage.setOnClickListener {
                viewModel.loadTopicMessage(
                    (requireActivity().application as ZulipApp).retrofitService,
                    topic.name,
                    stream.name
                )
//                if (!messageField.text.isNullOrEmpty()) {
//                    viewModel.onNextMassageClick(messageField.text.toString())
//                }
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
