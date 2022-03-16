package com.example.homework2

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.customviews.*
import com.example.homework2.databinding.ActivityMainBinding
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {

    private var position = -1
    private lateinit var binding: ActivityMainBinding
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
                top += this@MainActivity.dpToPx(10)
                bottom += this@MainActivity.dpToPx(10)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSheetDialog = CustomBottomSheetDialog(this) { emoji ->
            messageAdapter.addEmojiReaction(Reaction(emoji, 3), position)
            bottomSheetDialog?.hide()
        }

        binding.apply {
            rcView.adapter = messageAdapter
            rcView.layoutManager =
                LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL, false
                )
            rcView.addItemDecoration(itemDivider)

            messageField.doOnTextChanged { text, start, before, count ->
                if (text.isNullOrEmpty()) {
                    binding.messageTranslateImage.setImageResource(R.drawable.ic_add_circle_no_text)
                    binding.messageTranslateImage.setBackgroundResource(R.drawable.send_message_circle_background_no_text)
                } else {
                    binding.messageTranslateImage.setImageResource(R.drawable.ic_add_circle_yes_text)
                    binding.messageTranslateImage.setBackgroundResource(R.drawable.send_message_circle_background_text)
                }
            }
            binding.messageTranslateImage.setOnClickListener { nextMessage() }
        }
    }

    private var index = 0

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
}
