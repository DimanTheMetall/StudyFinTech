package com.example.homework2

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.customviews.CustomViewGroup
import com.example.homework2.customviews.MessageAdapter
import com.example.homework2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val messageAdapter = MessageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            rcView.adapter = messageAdapter
            rcView.layoutManager =
                LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )

            binding.messageTranslateImage.setOnClickListener { nextMessage() }


        }
    }
    private var index = 0
    private fun nextMessage() {

        val firstMessage =
            Message("Text message 1", "Title Message 1", R.mipmap.ic_launcher)
        val secondMessage =
            Message("Text message 2", "Title Message 2", R.mipmap.ic_launcher, false)
        when (index) {
            0 -> {
                messageAdapter.addMessage(firstMessage)
                index++
            }
            1 -> {
                messageAdapter.addMessage(secondMessage)
                index = 0
            }
        }
    }
}


