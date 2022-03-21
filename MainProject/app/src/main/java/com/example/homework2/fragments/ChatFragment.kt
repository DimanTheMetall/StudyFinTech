package com.example.homework2.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.R
import com.example.homework2.customviews.*
import com.example.homework2.databinding.ActivityMainBinding
import com.example.homework2.databinding.FragmentChatBinding
import org.joda.time.DateTime


class ChatFragment : Fragment() {


    private var index = 0
    private var position = -1
    private lateinit var binding: FragmentChatBinding
    private val messageAdapter = MessageAdapter { position ->
        this.position = position
        bottomSheetDialog?.show()
    }

    private var dateTime: DateTime = DateTime()
    private var bottomSheetDialog: CustomBottomSheetDialog? = null

    private var itemDivider = object : RecyclerView.ItemDecoration() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetDialog = CustomBottomSheetDialog(requireContext())
        { emoji ->
            messageAdapter.addEmojiReaction(Reaction(emoji, 3), position)
            bottomSheetDialog?.hide()

            binding.apply {
                rcView.adapter = messageAdapter
                rcView.layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                rcView.addItemDecoration(itemDivider)

                messageTranslateImage.setOnClickListener { nextMessage() }

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

        }

        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    //Test function
    private fun nextMessage() {

        val firstMessage =
            SelectViewTypeClass.Message(
                binding.messageField.text.toString(),
                "Title Message 1",
                R.mipmap.ic_launcher
            )

        val secondMessage =
            SelectViewTypeClass.Message(
                binding.messageField.text.toString(),
                "text title 2",
                R.mipmap.ic_launcher,
                false
            )
        val thirdMessage = SelectViewTypeClass.Data(
            dateTime.toString("MMM") + " " + dateTime.dayOfMonth
        )
        when (index) {
            0 -> {
                messageAdapter.addMessage(firstMessage)
                index++
            }
            1 -> {
                messageAdapter.addMessage(secondMessage)
                index++
            }

            2 -> {
                messageAdapter.addMessage(thirdMessage)
                index = 0
            }
        }
    }


    companion object {
        const val TAG = "ChatFragment"

        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
}
