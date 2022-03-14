package com.example.homework2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.customviews.CustomBottomSheetDialog
import com.example.homework2.customviews.MessageAdapter
import com.example.homework2.customviews.SelectViewTypeClass
import com.example.homework2.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val messageAdapter = MessageAdapter { position ->
        Toast.makeText(this, "AAA $position", Toast.LENGTH_LONG).show()
        bottomSheetDialog?.show()
    }
    private var dateTime: DateTime = DateTime()
    private var bottomSheetDialog: CustomBottomSheetDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheetDialog = CustomBottomSheetDialog(this)



        binding.apply {
            rcView.adapter = messageAdapter
            rcView.layoutManager =
                LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL, false
                )

            messageField.doOnTextChanged { text, start, before, count ->
                if (text.isNullOrEmpty()) {
                    binding.messageTranslateImage.setImageResource(R.drawable.ic_add_circle_no_text)
                } else {
                    binding.messageTranslateImage.setImageResource(R.drawable.ic_add_circle_yes_text)
                }
            }
            binding.messageTranslateImage.setOnClickListener { nextMessage() }

        }
    }


    private var index = 0
    private fun nextMessage() {

        val firstMessage =
            SelectViewTypeClass.Message(
                "Text message 1",
                "Title Message 1",
                R.mipmap.ic_launcher
            )

        val secondMessage =
            SelectViewTypeClass.Message(
                "Text message 2",
                "Title Message 2",
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


