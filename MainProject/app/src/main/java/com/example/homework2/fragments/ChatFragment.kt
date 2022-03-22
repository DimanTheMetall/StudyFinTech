package com.example.homework2.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.R
import com.example.homework2.customviews.*
import com.example.homework2.databinding.FragmentChatBinding
import com.example.homework2.viewmodels.ChatViewModel
import org.joda.time.DateTime


class ChatFragment() : Fragment() {


    private var index = 0
    private var position = -1
    private lateinit var binding: FragmentChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private var dateTime: DateTime = DateTime()

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
        binding = FragmentChatBinding.inflate(inflater)
        messageAdapter = MessageAdapter { position ->
            this.position = position
            bottomSheetDialog?.show()
        }

        viewModel.chatList.observe(viewLifecycleOwner) {
            messageAdapter.updateChatList(it)
        }

        bottomSheetDialog = CustomBottomSheetDialog(requireContext()) { emoji ->
            val newList = viewModel.chatList.value
            (newList as List<SelectViewTypeClass.Message>)[position].emojiList.add(
                Reaction(
                    emoji,
                    1
                )
            )

            viewModel.updateListData(newList)
            messageAdapter.notifyItemChanged(position)
            bottomSheetDialog?.hide()
        }

        binding.apply {
            rcView.adapter = messageAdapter

            rcView.addItemDecoration(itemDivider)

            messageTranslateImage.setOnClickListener {
                if (messageField.text.toString() != "") {
                    nextMessage(messageField.text.toString())
                }
            }

            messageField.doOnTextChanged { text, start, before, count ->
                if (text.isNullOrEmpty()) {
                    binding.messageTranslateImage.setImageResource(R.drawable.ic_add_circle_no_text)
                    binding.messageTranslateImage.setBackgroundResource(R.drawable.send_message_circle_background_no_text)
                } else {
                    binding.messageTranslateImage.setImageResource(R.drawable.ic_add_circle_yes_text)
                    binding.messageTranslateImage.setBackgroundResource(R.drawable.send_message_circle_background_text)
                }
            }
        }
        return binding.root
    }

    private fun nextMessage(message: String) {
        messageAdapter.addMessage(message){
            viewModel.updateListData(it)
        }

    }


    companion object {
        const val TAG = "ChatFragment"

        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
}
